package world.mappable.navikitdemo.data

import world.mappable.mapkit.map.MapMode
import world.mappable.navikitdemo.domain.SettingsManager
import world.mappable.navikitdemo.domain.StyleManager
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@ActivityScoped
class StyleManagerImpl @Inject constructor(
    private val map: world.mappable.mapkit.map.Map,
    private val settingsManager: SettingsManager,
): StyleManager {

    init {
        map.mode = MapMode.DRIVING
    }

    override fun start(scope: CoroutineScope) {
        subscribeHdMode().launchIn(scope)
        subscribeEnable3DSuperPOI().launchIn(scope)
    }

    private fun subscribeHdMode(): Flow<*> {
        return settingsManager.hdMode.changes()
            .onEach(map::setHdModeEnabled)
    }

    private fun subscribeEnable3DSuperPOI(): Flow<*> {
        return settingsManager.poi3DModels.changes()
            .onEach(map::setAwesomeModelsEnabled)
    }
}
