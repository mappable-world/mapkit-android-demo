package world.mappable.navikitdemo

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import world.mappable.navikitdemo.domain.NavigationManager
import world.mappable.navikitdemo.domain.SettingsManager
import world.mappable.navikitdemo.domain.helpers.NavigationClient
import world.mappable.navikitdemo.domain.helpers.NavigationSuspenderManager
import world.mappable.navikitdemo.domain.isGuidanceActive
import world.mappable.navikitdemo.domain.models.StyleMode
import world.mappable.navikitdemo.domain.utils.isNightModeActive
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class AppActivityViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val navigationManager: NavigationManager,
    private val navigationSuspenderManager: NavigationSuspenderManager,
) : ViewModel() {

    fun nightModeActive(resources: Resources): Flow<Boolean> {
        val system = resources.isNightModeActive()
        return settingsManager.styleMode.changes()
            .map {
                when (it) {
                    StyleMode.NIGHT -> true
                    StyleMode.DAY -> false
                    StyleMode.SYSTEM -> system
                }
            }
    }

    fun onResume() {
        navigationManager.resume()
        navigationSuspenderManager.register(NavigationClient.ACTIVITY)
    }

    fun onPause() {
        serializeNavigationIfNeeded()
        navigationSuspenderManager.removeClient(NavigationClient.ACTIVITY)
    }

    private fun serializeNavigationIfNeeded() {
        if (
            navigationManager.isGuidanceActive
            && settingsManager.restoreGuidanceState.value
        ) {
            navigationManager.serializeNavigation()
        }
    }
}
