package world.mappable.navikitdemo.domain.smartroute

import world.mappable.mapkit.GeoObject
import world.mappable.mapkit.RequestPoint
import world.mappable.mapkit.geometry.Point

sealed class SmartRoutePoint {
    abstract val point: RequestPoint
    data class ChargingPoint(override val point: RequestPoint, val geoObject: GeoObject) : SmartRoutePoint()
    data class RegularPoint(override val point: RequestPoint) : SmartRoutePoint()
}
