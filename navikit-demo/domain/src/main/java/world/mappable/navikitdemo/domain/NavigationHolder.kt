package world.mappable.navikitdemo.domain

import world.mappable.mapkit.navigation.automotive.Navigation
import kotlinx.coroutines.flow.StateFlow

interface NavigationHolder {
    val navigation: StateFlow<Navigation>

    fun serialize()
    fun deserialize()
}
