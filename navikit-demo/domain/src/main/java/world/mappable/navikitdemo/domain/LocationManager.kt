package world.mappable.navikitdemo.domain

import world.mappable.mapkit.location.Location
import kotlinx.coroutines.flow.StateFlow

interface LocationManager {
    fun location(): StateFlow<Location?>
}
