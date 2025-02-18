package world.mappable.navikitdemo.annotations.impl.local

import android.content.res.AssetManager
import world.mappable.mapkit.annotations.AnnotationLanguage
import world.mappable.mapkit.annotations.LocalizedPhrase

internal class CompositePhraseGenerator(
    private val assetManager: AssetManager,
    private val annotationLanguage: AnnotationLanguage
) : PhraseGenerator {

    private val defaultPhraseGenerator by lazy {
        PhraseGeneratorImpl(
            getDefaultDurations(
                assetManager
            ), getDefaultSoundPath()
        )
    }
    private val languagedPhraseGenerator by lazy {
        PhraseGeneratorImpl(
            annotationLanguage.getDurations(
                assetManager
            ), annotationLanguage.getSoundPath()
        )
    }

    override fun generate(phrase: LocalizedPhrase): LocalPhrase {
        val result = languagedPhraseGenerator.generate(phrase)
        return if (result.items.isEmpty()) {
            defaultPhraseGenerator.generate(phrase)
        } else {
            result
        }
    }
}
