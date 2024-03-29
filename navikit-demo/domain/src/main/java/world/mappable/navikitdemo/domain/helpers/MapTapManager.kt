package world.mappable.navikitdemo.domain.helpers

import world.mappable.mapkit.geometry.Point
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface MapTapManager {
    val longTapActions: Flow<Point>
    fun start(scope: CoroutineScope)
}
