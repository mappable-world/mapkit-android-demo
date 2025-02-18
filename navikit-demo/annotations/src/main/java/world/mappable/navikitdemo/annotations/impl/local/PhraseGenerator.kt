package world.mappable.navikitdemo.annotations.impl.local

import world.mappable.mapkit.annotations.LocalizedPhrase

internal interface PhraseGenerator {
    fun generate(phrase: LocalizedPhrase): LocalPhrase
}
