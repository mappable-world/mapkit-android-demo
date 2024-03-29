package world.mappable.navikitdemo.domain

import world.mappable.mapkit.annotations.Speaker
import kotlinx.coroutines.flow.Flow

interface SpeakerManager : Speaker {
    fun phrases(): Flow<String>
}
