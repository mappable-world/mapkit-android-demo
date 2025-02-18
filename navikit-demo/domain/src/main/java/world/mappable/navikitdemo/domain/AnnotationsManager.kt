package world.mappable.navikitdemo.domain

import world.mappable.navikitdemo.domain.models.AnnotatedEventsType
import world.mappable.navikitdemo.domain.models.AnnotatedRoadEventsType

interface AnnotationsManager {
    fun setAnnotationsEnabled(isEnabled: Boolean)

    fun setAnnotatedEventEnabled(event: AnnotatedEventsType, isEnabled: Boolean)
    fun setAnnotatedRoadEventEnabled(event: AnnotatedRoadEventsType, isEnabled: Boolean)
}
