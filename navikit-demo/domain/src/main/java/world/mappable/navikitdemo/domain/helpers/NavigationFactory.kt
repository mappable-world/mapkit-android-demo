package world.mappable.navikitdemo.domain.helpers

import world.mappable.mapkit.navigation.automotive.Navigation

interface NavigationFactory {
    fun wasDeserializedFirstTime(): Boolean
    fun create(): Navigation
}
