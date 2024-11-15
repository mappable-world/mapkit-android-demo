package world.mappable.navikitdemo.data.smartroute

import world.mappable.mapkit.RequestPoint
import world.mappable.mapkit.directions.driving.DrivingRouter
import world.mappable.mapkit.search.SearchManager
import world.mappable.navikitdemo.domain.models.SmartRouteOptions
import world.mappable.navikitdemo.domain.smartroute.SmartRoutePoint
import world.mappable.navikitdemo.domain.smartroute.SmartRouteResult
import world.mappable.navikitdemo.domain.smartroute.SmartRoutePlanningFactory
import javax.inject.Inject

class SmartRoutePlanningFactoryImpl @Inject constructor(
    searchManager: SearchManager,
    drivingRouter: DrivingRouter,
) : SmartRoutePlanningFactory {

    private val smartRoutePlanningFactory = SimpleSmartRoutePlanningFactoryImpl(searchManager, drivingRouter)

    override suspend fun requestRoutes(
        points: List<RequestPoint>,
        smartRouteOptions: SmartRouteOptions
    ): SmartRouteResult {
        if (points.size <= 1) {
            return SmartRouteResult.Error("Cant build smart route with less than 2 points")
        }

        val result = mutableListOf<SmartRoutePoint>()
        result.add(SmartRoutePoint.RegularPoint(points.first()))

        var options = smartRouteOptions

        for (i in 0 until points.size - 1) {
            val smartResult = smartRoutePlanningFactory.requestRoutes(
                points[i],
                points[i + 1],
                options
            )
            if (smartResult is SmartRouteResult.Success) {
                result.addAll(smartResult.points.subList(1, smartResult.points.size))
                options = smartRouteOptions.copy(
                    currentRangeLvlInMeters = smartResult.finishRangeLvlInMeters
                )
            } else {
                return smartResult
            }
        }

        return SmartRouteResult.Success(result, options.currentRangeLvlInMeters)
    }
}
