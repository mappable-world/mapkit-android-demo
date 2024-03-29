package world.mappable.navikitdemo.domain.helpers

import world.mappable.mapkit.navigation.automotive.GuidanceListener
import world.mappable.mapkit.navigation.automotive.RouteChangeReason

abstract class SimpleGuidanceListener : GuidanceListener {
    override fun onLocationChanged() = Unit
    override fun onCurrentRouteChanged(p0: RouteChangeReason) = Unit
    override fun onRouteLost() = Unit
    override fun onReturnedToRoute() = Unit
    override fun onRouteFinished() = Unit
    override fun onWayPointReached() = Unit
    override fun onStandingStatusChanged() = Unit
    override fun onRoadNameChanged() = Unit
    override fun onSpeedLimitUpdated() = Unit
    override fun onSpeedLimitStatusUpdated() = Unit
    override fun onAlternativesChanged() = Unit
    override fun onFastestAlternativeChanged() = Unit
}
