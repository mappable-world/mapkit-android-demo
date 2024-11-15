package world.mappable.navikitdemo.domain

import world.mappable.mapkit.LocalizedValue
import world.mappable.mapkit.RequestPoint
import world.mappable.mapkit.annotations.AnnotationLanguage
import world.mappable.mapkit.directions.driving.DrivingRoute
import world.mappable.mapkit.navigation.automotive.SpeedLimitStatus
import world.mappable.mapkit.navigation.automotive.SpeedLimitsPolicy
import world.mappable.mapkit.navigation.automotive.UpcomingLaneSign
import world.mappable.mapkit.navigation.automotive.UpcomingManoeuvre
import world.mappable.navikitdemo.domain.models.NavigationState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface NavigationManager : LocationManager {
    val currentRoute: StateFlow<DrivingRoute?>
    val roadName: Flow<String>
    val roadFlags: Flow<String>
    val upcomingManeuvers: Flow<List<UpcomingManoeuvre>>
    val upcomingLaneSigns: Flow<List<UpcomingLaneSign>>

    val speedLimit: LocalizedValue?
    val speedLimitStatus: SpeedLimitStatus
    val speedLimitTolerance: Double
    val speedLimitsPolicy: SpeedLimitsPolicy

    val navigationRouteState: Flow<NavigationState>

    fun requestRoutes(points: List<RequestPoint>)
    fun startGuidance(route: DrivingRoute)
    fun stopGuidance()
    fun resetRoutes()

    fun resume()
    fun suspend()

    fun setAnnotationLanguage(language: AnnotationLanguage)
    fun setEnabledAlternatives(isEnabled: Boolean)
    fun setSpeedLimitTolerance(tolerance: Double)
    fun setAvoidTolls(isAvoid: Boolean)
    fun setAvoidUnpaved(isAvoid: Boolean)
    fun setAvoidPoorConditions(isAvoid: Boolean)
}

val NavigationManager.isGuidanceActive: Boolean
    get() = currentRoute.value != null
