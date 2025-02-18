package world.mappable.navikitdemo.domain

import kotlinx.coroutines.CoroutineScope

interface SpeakerManager {
    fun startIn(scope: CoroutineScope)
}
