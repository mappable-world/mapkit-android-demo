package world.mappable.navikitdemo.data.smartroute

import world.mappable.mapkit.GeoObject
import world.mappable.mapkit.RequestPoint
import world.mappable.mapkit.RequestPointType
import world.mappable.mapkit.directions.driving.DrivingOptions
import world.mappable.mapkit.directions.driving.DrivingRoute
import world.mappable.mapkit.directions.driving.DrivingRouter
import world.mappable.mapkit.directions.driving.DrivingSession
import world.mappable.mapkit.directions.driving.VehicleOptions
import world.mappable.mapkit.geometry.Geo
import world.mappable.mapkit.geometry.Point
import world.mappable.mapkit.geometry.Polyline
import world.mappable.mapkit.geometry.PolylinePosition
import world.mappable.mapkit.geometry.Segment
import world.mappable.mapkit.geometry.Subpolyline
import world.mappable.mapkit.geometry.SubpolylineHelper
import world.mappable.mapkit.geometry.geo.PolylineIndex
import world.mappable.mapkit.geometry.geo.PolylineUtils
import world.mappable.mapkit.search.SearchManager
import world.mappable.navikitdemo.domain.models.SmartRouteOptions
import world.mappable.navikitdemo.domain.smartroute.SmartRoutePoint
import world.mappable.navikitdemo.domain.smartroute.SmartRouteResult
import world.mappable.runtime.Error
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull

class SimpleSmartRoutePlanningFactoryImpl(
    searchManager: SearchManager,
    private val drivingRouter: DrivingRouter,
) {
    private val routeSearchFactory = SmartRouteSearchFactoryImpl(searchManager)

    suspend fun requestRoutes(
        from: RequestPoint,
        to: RequestPoint,
        smartRouteOptions: SmartRouteOptions
    ): SmartRouteResult {
        val fromToPoints = listOf(from, to)
        val drivingOptions = smartRouteOptions.drivingOptions.setRoutesCount(1)
        val drivingRoute =
            drivingRouter.requestRoutes(fromToPoints, drivingOptions, smartRouteOptions.vehicleOptions).firstOrNull()
                ?: return SmartRouteResult.Error("Failed to build a route for a segment! from = ${from.point}, to = ${to.point}")
        val (viaGeoObjects, remainingDistance) = getViaPoints(drivingRoute, smartRouteOptions)
            ?: return SmartRouteResult.Error("Failed to find a charging point for a segment! from = ${from.point}, to = ${to.point}")
        val smartRoutePoints = createRequestPoints(from, viaGeoObjects, to)
        return SmartRouteResult.Success(smartRoutePoints, remainingDistance)
    }

    private suspend fun getViaPoints(
        drivingRoute: DrivingRoute,
        smartRouteOptions: SmartRouteOptions
    ): Pair<List<GeoObject>, Double>? {
        val routeGeometry = drivingRoute.geometry
        val maxTravelDistance =
            smartRouteOptions.maxTravelDistanceInMeters - smartRouteOptions.thresholdDistanceInMeters
        var currentRange = smartRouteOptions.currentRangeLvlInMeters
        val viaPoints = mutableListOf<GeoObject>()
        while (drivingRoute.routePosition.distanceToFinish() > currentRange) {
            val currentPosition = drivingRoute.position
            val targetPosition = drivingRoute.advancePositionOnRoute(currentRange) ?: return null

            val sectionPolyline = routeGeometry.sectionSubpolyline(currentPosition, targetPosition)
                ?: return null
            val viaPoint = getViaForPolyline(sectionPolyline, viaPoints.lastOrNull(), smartRouteOptions)
                ?: return null
            val closestPosition = sectionPolyline.closestPolylinePosition(routeGeometry, viaPoint.point())
                ?: return null

            drivingRoute.position = closestPosition
            currentRange = maxTravelDistance
            viaPoints.add(viaPoint)
        }
        val remainingDistance = currentRange - drivingRoute.routePosition.distanceToFinish()
        return viaPoints to remainingDistance
    }

    private suspend fun getViaForPolyline(
        sectionPolyline: Polyline,
        lastChargingPoint: GeoObject?,
        smartRouteOptions: SmartRouteOptions
    ): GeoObject? {
        val thresholdPoint = sectionPolyline.points.lastOrNull() ?: return null
        return routeSearchFactory.getViaForPolyline(
            thresholdPoint,
            sectionPolyline,
            smartRouteOptions
        )?.takeIf { lastChargingPoint == null || Geo.distance(it.point(), lastChargingPoint.point()) > 1 }
    }

    private fun GeoObject.point(): Point {
        return geometry.first().point!!
    }

    private fun createRequestPoints(
        from: RequestPoint,
        viaGeoObjects: List<GeoObject>,
        to: RequestPoint,
    ): List<SmartRoutePoint> {
        return buildList {
            add(SmartRoutePoint.RegularPoint(from))
            addAll(viaGeoObjects.map { geoObject ->
                SmartRoutePoint.ChargingPoint(
                    RequestPoint(geoObject.point(), RequestPointType.WAYPOINT, null, null),
                    geoObject
                )
            })
            add(SmartRoutePoint.RegularPoint(to))
        }
    }

    private fun Polyline.closestPolylinePosition(
        routePolyline: Polyline,
        viaPoint: Point
    ): PolylinePosition? {
        return points
            .zipWithNext { a, b -> Segment(a, b) }
            .map { Geo.closestPoint(viaPoint, it) }
            .minByOrNull { Geo.distance(viaPoint, it) }
            ?.let {
                val polylineIndex = PolylineUtils.createPolylineIndex(routePolyline)
                polylineIndex.closestPolylinePosition(
                    it,
                    PolylineIndex.Priority.CLOSEST_TO_RAW_POINT,
                    1.0
                )
            }
    }

    private fun Polyline.sectionSubpolyline(
        begin: PolylinePosition,
        end: PolylinePosition
    ): Polyline? {
        val subpolyline = Subpolyline(begin, end)
        val sectionSubpolyline = if (begin.segmentIndex < end.segmentIndex) {
            SubpolylineHelper.subpolyline(this, subpolyline)
        } else {
            null
        }
        return sectionSubpolyline
    }

    private fun DrivingRoute.advancePositionOnRoute(distance: Double): PolylinePosition? {
        return routePosition.advance(distance).positionOnRoute(routeId)
    }


    private fun DrivingRouter.requestRoutes(
        points: List<RequestPoint>,
        drivingOptions: DrivingOptions,
        vehicleOptions: VehicleOptions
    ): Flow<DrivingRoute?> {
        return callbackFlow {
            val listener = object : DrivingSession.DrivingRouteListener {
                override fun onDrivingRoutes(drivingRoutes: MutableList<DrivingRoute>) {
                    trySend(drivingRoutes.firstOrNull())
                }

                override fun onDrivingRoutesError(error: Error) {
                    trySend(null)
                }
            }
            val drivingSession = requestRoutes(points, drivingOptions, vehicleOptions, listener)
            awaitClose { drivingSession.cancel() }
        }
    }

    private fun RequestPoint.dump(): String {
        return  "lat = ${point.latitude}; lon = ${point.longitude}"
    }
}
