package world.mappable.navikitdemo.di

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import world.mappable.mapkit.directions.DirectionsFactory
import world.mappable.mapkit.directions.driving.DrivingRouter
import world.mappable.mapkit.directions.driving.DrivingRouterType
import world.mappable.mapkit.search.SearchFactory
import world.mappable.mapkit.search.SearchManager
import world.mappable.mapkit.search.SearchManagerType
import world.mappable.navikitdemo.data.AnnotationsManagerImpl
import world.mappable.navikitdemo.data.LocationManagerImpl
import world.mappable.navikitdemo.data.NavigationHolderImpl
import world.mappable.navikitdemo.data.NavigationManagerImpl
import world.mappable.navikitdemo.data.NavigationStyleManagerImpl
import world.mappable.navikitdemo.data.RequestPointsManagerImpl
import world.mappable.navikitdemo.data.SettingsManagerImpl
import world.mappable.navikitdemo.data.SimulationManagerImpl
import world.mappable.navikitdemo.data.smartroute.SmartRouteSearchFactoryImpl
import world.mappable.navikitdemo.data.SpeakerImpl
import world.mappable.navikitdemo.data.VehicleOptionsManagerImpl
import world.mappable.navikitdemo.data.helpers.BackgroundServiceManagerImpl
import world.mappable.navikitdemo.data.helpers.KeyValueStorageImpl
import world.mappable.navikitdemo.data.helpers.NavigationDeserializerImpl
import world.mappable.navikitdemo.data.helpers.NavigationFactoryImpl
import world.mappable.navikitdemo.data.helpers.NavigationSuspenderManagerImpl
import world.mappable.navikitdemo.data.smartroute.SimpleSmartRoutePlanningFactoryImpl
import world.mappable.navikitdemo.data.smartroute.SmartRoutePlanningFactoryImpl
import world.mappable.navikitdemo.domain.AnnotationsManager
import world.mappable.navikitdemo.domain.LocationManager
import world.mappable.navikitdemo.domain.NavigationHolder
import world.mappable.navikitdemo.domain.NavigationManager
import world.mappable.navikitdemo.domain.NavigationStyleManager
import world.mappable.navikitdemo.domain.RequestPointsManager
import world.mappable.navikitdemo.domain.SettingsManager
import world.mappable.navikitdemo.domain.SimulationManager
import world.mappable.navikitdemo.domain.SpeakerManager
import world.mappable.navikitdemo.domain.VehicleOptionsManager
import world.mappable.navikitdemo.domain.helpers.BackgroundServiceManager
import world.mappable.navikitdemo.domain.helpers.KeyValueStorage
import world.mappable.navikitdemo.domain.helpers.NavigationDeserializer
import world.mappable.navikitdemo.domain.helpers.NavigationFactory
import world.mappable.navikitdemo.domain.helpers.NavigationSuspenderManager
import world.mappable.navikitdemo.domain.smartroute.SmartRoutePlanningFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun navigationStyleManager(impl: NavigationStyleManagerImpl): NavigationStyleManager

    @Binds
    abstract fun backgroundServiceManager(impl: BackgroundServiceManagerImpl): BackgroundServiceManager

    @Binds
    abstract fun keyValueStorage(impl: KeyValueStorageImpl): KeyValueStorage

    @Binds
    abstract fun locationManager(impl: LocationManagerImpl): LocationManager

    @Binds
    abstract fun navigationManager(impl: NavigationManagerImpl): NavigationManager

    @Binds
    abstract fun smartRouteWithViaPlanningFactory(impl: SmartRoutePlanningFactoryImpl): SmartRoutePlanningFactory

    @Binds
    abstract fun requestPointsManager(impl: RequestPointsManagerImpl): RequestPointsManager

    @Binds
    abstract fun settingsManager(impl: SettingsManagerImpl): SettingsManager

    @Binds
    abstract fun simulationManager(impl: SimulationManagerImpl): SimulationManager

    @Binds
    abstract fun vehicleOptionsManager(impl: VehicleOptionsManagerImpl): VehicleOptionsManager

    @Binds
    abstract fun navigationFactory(impl: NavigationFactoryImpl): NavigationFactory

    @Binds
    abstract fun navigationSuspenderManger(impl: NavigationSuspenderManagerImpl): NavigationSuspenderManager

    @Binds
    abstract fun navigationDeserializer(impl: NavigationDeserializerImpl): NavigationDeserializer

    @Binds
    abstract fun navigationHolder(impl: NavigationHolderImpl): NavigationHolder

    @Binds
    abstract fun speakerManager(impl: SpeakerImpl): SpeakerManager

    @Binds
    abstract fun annotationsManager(impl: AnnotationsManagerImpl): AnnotationsManager

    companion object {

        @Singleton
        @Provides
        fun notificationManager(
            application: Application,
        ): NotificationManager {
            return application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        @Singleton
        @Provides
        fun drivingRouter(): DrivingRouter {
            return DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.COMBINED)
        }

        @Singleton
        @Provides
        fun searchManager(): SearchManager {
            return SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        }

    }
}
