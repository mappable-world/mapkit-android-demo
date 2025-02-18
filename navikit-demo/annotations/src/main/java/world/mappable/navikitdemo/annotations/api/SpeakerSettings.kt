package world.mappable.navikitdemo.annotations.api

import world.mappable.mapkit.annotations.AnnotationLanguage

data class SpeakerSettings(
    val annotationLanguage: AnnotationLanguage,
    val usePreRecordedAnnotations: Boolean,
    val textAnnotations: Boolean,
)
