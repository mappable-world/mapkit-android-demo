package world.mappable.navikitdemo.ui.settings.settingslist

import world.mappable.mapkit.navigation.automotive.SpeedLimitsPolicy
import world.mappable.navikitdemo.domain.NavigationManager
import world.mappable.navikitdemo.domain.SettingsManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SpeedLimitsInteractor @Inject constructor(
    private val navigationManager: NavigationManager,
    settingsManager: SettingsManager,
) {
    val speedLimitsPolicy: SpeedLimitsPolicy
        get() = navigationManager.speedLimitsPolicy

    val speedLimitsTolerance: Double
        get() = navigationManager.speedLimitTolerance

    val viewStateChanges: Flow<Unit> = settingsManager.speedLimitTolerance.changes().map {  }
}
