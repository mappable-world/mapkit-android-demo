package world.mappable.navikitdemo.annotations.api

import android.content.Context
import world.mappable.mapkit.annotations.AnnotationLanguage
import world.mappable.mapkit.annotations.Speaker
import world.mappable.navikitdemo.annotations.impl.ToastSpeaker
import world.mappable.navikitdemo.annotations.impl.TtsSpeaker
import world.mappable.navikitdemo.annotations.impl.local.LocalSpeaker

object SpeakerFactory {

    fun createSpeaker(context: Context, settings: SpeakerSettings): Speaker {
        val speaker =
            if (settings.usePreRecordedAnnotations && settings.annotationLanguage in preRecordedLanguages) {
                LocalSpeaker(context, settings.annotationLanguage)
            } else {
                TtsSpeaker(context, settings.annotationLanguage)
            }

        return if (settings.textAnnotations) {
            ToastSpeaker(context, speaker)
        } else {
            speaker
        }
    }

    private val preRecordedLanguages = setOf(
        AnnotationLanguage.ENGLISH,
        AnnotationLanguage.RUSSIAN
    )
}
