package world.mappable.navikitdemo.ui.guidance.views.maneuver

import android.content.Context
import androidx.annotation.DrawableRes
import world.mappable.mapkit.directions.driving.Action
import world.mappable.mapkit.directions.driving.LaneDirection
import world.mappable.mapkit.directions.driving.LaneKind

interface UpcomingManeuverResourcesProvider {
    fun getLaneKindResources(laneKind: LaneKind, context: Context): Pair<Int, Int>?
    fun getSmallResourceForLaneDirection(direction: LaneDirection): Int
    fun getLargeResourceForLaneDirection(direction: LaneDirection): Int

    enum class DimensionType {
        SMALL_OVERLAP,
        LARGE_OVERLAP,
        INDENT,
        WIDTH,
        HEIGHT,
    }

    fun getLaneItemDimension(dimension: DimensionType): Int

    @DrawableRes
    fun getManeuverResource(action: Action): Int?
}
