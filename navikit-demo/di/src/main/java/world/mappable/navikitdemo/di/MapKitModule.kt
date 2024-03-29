package world.mappable.navikitdemo.di

import world.mappable.mapkit.navigation.automotive.Annotator
import world.mappable.mapkit.navigation.automotive.Guidance
import world.mappable.mapkit.navigation.automotive.Navigation
import world.mappable.navikitdemo.domain.helpers.NavigationFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MapKitModule {

    companion object {
        @Provides
        @Singleton
        fun navigation(factory: NavigationFactory): Navigation = factory.create()

        @Provides
        @Singleton
        fun guidance(navigation: Navigation): Guidance = navigation.guidance

        @Provides
        @Singleton
        fun annotator(guidance: Guidance): Annotator = guidance.annotator
    }
}
