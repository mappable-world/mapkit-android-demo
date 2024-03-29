package world.mappable.navikitdemo.domain.helpers

import kotlinx.coroutines.CoroutineScope

interface SettingsBinderManager {
    fun applySettingsChanges(scope: CoroutineScope)
}
