package world.mappable.navikitdemo.domain

import world.mappable.mapkit.navigation.automotive.layer.styling.NavigationStyleProvider
import world.mappable.navikitdemo.domain.models.JamsMode

interface NavigationStyleManager : NavigationStyleProvider {
    var trafficLightsVisibility: Boolean
    var roadEventsOnRouteVisibility: Boolean
    var balloonsVisibility: Boolean
    var predictedVisibility: Boolean
    var currentJamsMode: JamsMode
}
