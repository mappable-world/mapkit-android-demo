package world.mappable.navikitdemo.annotations.impl.local

import android.content.Context
import world.mappable.mapkit.annotations.AnnotationLanguage
import world.mappable.mapkit.annotations.LocalizedPhrase
import world.mappable.mapkit.annotations.Speaker

internal class LocalSpeaker(
    context: Context,
    annotationLanguage: AnnotationLanguage,
) : Speaker {

    private var localPhrase: LocalPhrase? = null
    private val exoPlayerManager = PlayerManager(context)
    private val phraseGenerator: PhraseGenerator = PhraseGeneratorCached(
        CompositePhraseGenerator(context.assets, annotationLanguage)
    )

    override fun reset() {
        exoPlayerManager.reset()
    }

    override fun say(phrase: LocalizedPhrase) {
        phraseGenerator.generate(phrase).items.let(exoPlayerManager::play)
    }

    override fun duration(phrase: LocalizedPhrase): Double {
        localPhrase = phraseGenerator.generate(phrase)
        return (localPhrase?.duration ?: 0.0) / 1000.0
    }
}
