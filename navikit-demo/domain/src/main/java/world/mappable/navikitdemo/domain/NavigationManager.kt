package world.mappable.navikitdemo.domain

import world.mappable.mapkit.LocalizedValue
import world.mappable.mapkit.RequestPoint
import world.mappable.mapkit.directions.driving.DrivingRoute
import world.mappable.mapkit.navigation.automotive.SpeedLimitStatus
import world.mappable.mapkit.navigation.automotive.UpcomingLaneSign
import world.mappable.mapkit.navigation.automotive.UpcomingManoeuvre
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface NavigationManager {
    val roadName: Flow<String>
    val roadFlags: Flow<String>
    val upcomingManeuvers: Flow<List<UpcomingManoeuvre>>
    val upcomingLaneSigns: Flow<List<UpcomingLaneSign>>
    val currentRoute: StateFlow<DrivingRoute?>

    fun serializeNavigation()

    fun requestRoutes(points: List<RequestPoint>)

    fun startGuidance(route: DrivingRoute)
    fun stopGuidance()
    fun resetRoutes()

    fun resume()
    fun suspend()

    fun speedLimit(): LocalizedValue?
    fun speedLimitStatus(): SpeedLimitStatus
}

val NavigationManager.isGuidanceActive: Boolean
    get() = currentRoute.value != null
