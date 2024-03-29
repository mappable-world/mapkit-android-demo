package world.mappable.navikitdemo.ui.map

import androidx.lifecycle.ViewModel
import world.mappable.mapkit.geometry.Point
import world.mappable.navikitdemo.domain.NavigationManager
import world.mappable.navikitdemo.domain.RequestPointsManager
import world.mappable.navikitdemo.domain.SettingsManager
import world.mappable.navikitdemo.domain.helpers.NavigationFactory
import world.mappable.navikitdemo.domain.isGuidanceActive
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val navigationFactory: NavigationFactory,
    private val settingsManager: SettingsManager,
    private val navigationManager: NavigationManager,
    private val requestPointsManager: RequestPointsManager,
) : ViewModel() {

    fun isGuidanceInProgress(): Boolean {
        return navigationFactory.wasDeserializedFirstTime()
            || navigationManager.isGuidanceActive
    }

    fun clearNavigationSerialization() {
        settingsManager.serializedNavigation.value = ""
    }

    fun setToPoint(point: Point) {
        requestPointsManager.setToPoint(point)
    }
}
