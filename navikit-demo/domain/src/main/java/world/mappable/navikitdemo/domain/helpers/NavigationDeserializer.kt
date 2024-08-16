package world.mappable.navikitdemo.domain.helpers

import world.mappable.mapkit.navigation.automotive.Navigation

interface NavigationDeserializer {
    fun deserializeNavigationFromSettings(): Navigation?
}
