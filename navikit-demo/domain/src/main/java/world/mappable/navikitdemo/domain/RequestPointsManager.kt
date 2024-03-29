package world.mappable.navikitdemo.domain

import world.mappable.mapkit.RequestPoint
import world.mappable.mapkit.geometry.Point
import kotlinx.coroutines.flow.Flow

interface RequestPointsManager {
    val requestPoints: Flow<List<RequestPoint>>

    fun setFromPoint(point: Point)
    fun setToPoint(point: Point)
    fun addViaPoint(point: Point)

    fun resetPoints()
}
