package world.mappable.navikitdemo.di

import android.app.Activity
import android.content.Context
import androidx.lifecycle.lifecycleScope
import world.mappable.mapkit.annotations.AnnotationLanguage
import world.mappable.mapkit.map.MapWindow
import world.mappable.mapkit.navigation.automotive.layer.styling.AutomotiveNavigationStyleProvider
import world.mappable.mapkit.navigation.automotive.layer.styling.NavigationStyleProvider
import world.mappable.mapkit.road_events_layer.StyleProvider
import world.mappable.mapkit.styling.roadevents.RoadEventsLayerDefaultStyleProvider
import world.mappable.navikitdemo.AppActivity
import world.mappable.navikitdemo.ui.guidance.views.maneuver.UpcomingManeuverResourcesProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
abstract class ActivityIntegrationModule {

    companion object {

        @Provides
        @ActivityScoped
        fun mapWindow(activity: Activity): MapWindow = (activity as AppActivity).mapWindow

        @Provides
        @ActivityScoped
        fun coroutineScope(activity: Activity): CoroutineScope =
            (activity as AppActivity).lifecycleScope
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AppIntegrationModule {

    @Binds
    abstract fun upcomingManeuverResourcesProvider(impl: UpcomingManeuverResourcesProviderImpl): UpcomingManeuverResourcesProvider

    companion object {
        @Provides
        @Singleton
        fun navigationStyleProvider(@ApplicationContext context: Context): NavigationStyleProvider =
            AutomotiveNavigationStyleProvider(context)

        @Provides
        @Singleton
        fun roadEventsStyleProvider(@ApplicationContext context: Context): StyleProvider =
            RoadEventsLayerDefaultStyleProvider(context)

        @Provides
        @Singleton
        fun defaultAnnotationsLanguage() = AnnotationLanguage.ENGLISH
    }
}
