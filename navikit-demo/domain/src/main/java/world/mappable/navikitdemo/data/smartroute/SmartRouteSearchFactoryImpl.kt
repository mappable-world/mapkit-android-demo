package world.mappable.navikitdemo.data.smartroute

import world.mappable.mapkit.GeoObject
import world.mappable.mapkit.geometry.Geo
import world.mappable.mapkit.geometry.Geometry
import world.mappable.mapkit.geometry.Point
import world.mappable.mapkit.geometry.Polyline
import world.mappable.mapkit.geometry.PolylinePosition
import world.mappable.mapkit.geometry.Subpolyline
import world.mappable.mapkit.geometry.SubpolylineHelper
import world.mappable.mapkit.geometry.geo.PolylineUtils
import world.mappable.mapkit.search.FilterCollection
import world.mappable.mapkit.search.FilterCollectionUtils
import world.mappable.mapkit.search.Response
import world.mappable.mapkit.search.SearchManager
import world.mappable.mapkit.search.SearchOptions
import world.mappable.mapkit.search.SearchType
import world.mappable.mapkit.search.Session
import world.mappable.navikitdemo.domain.models.SmartRouteOptions
import world.mappable.runtime.Error
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull

private const val POLYLINE_CHUNKING_DISTANCE_METERS = 10_000.0

class SmartRouteSearchFactoryImpl(
    private val searchManager: SearchManager
) {
    suspend fun getViaForPolyline(thresholdPoint: Point, polyline: Polyline, options: SmartRouteOptions): GeoObject? {
        val query = options.chargingType.vehicle
        val searchOptions = SearchOptions()
            .setResultPageSize(32)
            .setSearchTypes(SearchType.BIZ.value)
            .setFilters(options.filterTypeCollection())

        val geoObject = findChargingPoints(polyline, query, searchOptions)
            ?.minByOrNull { Geo.distance(thresholdPoint, it.geometry.firstOrNull()?.point) }
            ?: return null

        return geoObject
    }

    private fun SmartRouteOptions.filterTypeCollection(): FilterCollection {
        val connectors = fuelConnectorTypes.map { it.type }
        return FilterCollectionUtils.createFilterCollectionBuilder()
            .also { it.addEnumFilter(chargingType.filter, connectors) }
            .build()
    }

    private suspend fun findChargingPoints(polyline: Polyline, query: String, searchOptions: SearchOptions): List<GeoObject>? {
        val chunked = polyline.chunkByDistance(POLYLINE_CHUNKING_DISTANCE_METERS).map { Geometry.fromPolyline(it) }.reversed()
        for (geometry in chunked) {
            val output = searchManager.submitSearch(query, geometry, searchOptions).firstOrNull()
            if (!output.isNullOrEmpty()) {
                return output
            }
        }
        return null
    }

    private fun Polyline.chunkByDistance(distance: Double): List<Polyline> {
        val result = mutableListOf<Polyline>()
        var currentPosition = PolylinePosition()
        while (true) {
            val end = PolylineUtils.advancePolylinePosition(this, currentPosition, distance)
            if (PolylineUtils.distanceBetweenPolylinePositions(this, currentPosition, end) > 1) {
                result.add(
                    SubpolylineHelper.subpolyline(
                        this,
                        Subpolyline(currentPosition, end)
                    )
                )
                currentPosition = end
            } else {
                return result
            }
        }
    }

    private fun SearchManager.submitSearch(
        query: String,
        geometry: Geometry,
        searchOptions: SearchOptions,
    ): Flow<List<GeoObject>?> {
        return callbackFlow {
            val listener = object : Session.SearchListener {
                override fun onSearchResponse(response: Response) {
                    val items = response.collection.children.mapNotNull { it.obj }
                    trySend(items)
                }

                override fun onSearchError(error: Error) {
                    trySend(null)
                }
            }
            val searchSession = submit(query, geometry, searchOptions, listener)
            awaitClose { searchSession.cancel() }
        }
    }
}
