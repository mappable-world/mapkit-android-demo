package world.mappable.navikitdemo.data.helpers

import android.util.Base64
import world.mappable.mapkit.directions.driving.DrivingRouterType
import world.mappable.mapkit.navigation.automotive.Navigation
import world.mappable.mapkit.navigation.automotive.NavigationSerialization
import world.mappable.navikitdemo.domain.SettingsManager
import world.mappable.navikitdemo.domain.helpers.NavigationFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationFactoryImpl @Inject constructor(
    private val settingsManager: SettingsManager,
) : NavigationFactory {

    private var wasDeserializedImpl = false

    override fun wasDeserializedFirstTime(): Boolean {
        val value = wasDeserializedImpl
        wasDeserializedImpl = false
        return value
    }

    /**
     * Recreates Navigation from the serialized data, otherwise creates a new instance.
     */
    override fun create(): Navigation {
        val serializedNavigation = settingsManager.serializedNavigation.value
        if (serializedNavigation.isNotEmpty()) {
            val data = Base64.decode(serializedNavigation, Base64.DEFAULT)
            val navigation = NavigationSerialization.deserialize(data)
            if (navigation != null) {
                wasDeserializedImpl = true
                settingsManager.serializedNavigation.value = ""
                return navigation
            }
        }
        // Create new Navigation instance.
        return world.mappable.mapkit.navigation.automotive.NavigationFactory.createNavigation(
            DrivingRouterType.COMBINED
        )
    }
}
