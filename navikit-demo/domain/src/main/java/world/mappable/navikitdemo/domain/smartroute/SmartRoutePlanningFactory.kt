package world.mappable.navikitdemo.domain.smartroute

import world.mappable.mapkit.RequestPoint
import world.mappable.navikitdemo.domain.models.SmartRouteOptions

interface SmartRoutePlanningFactory {

    suspend fun requestRoutes(
        points: List<RequestPoint>,
        smartRouteOptions: SmartRouteOptions
    ): SmartRouteResult
}
