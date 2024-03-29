package world.mappable.navikitdemo.di

import world.mappable.navikitdemo.data.AnnotationsManagerImpl
import world.mappable.navikitdemo.data.CameraManagerImpl
import world.mappable.navikitdemo.data.NavigationLayerManagerImpl
import world.mappable.navikitdemo.data.SpeakerImpl
import world.mappable.navikitdemo.data.helpers.AlertDialogFactoryImpl
import world.mappable.navikitdemo.data.helpers.MapTapManagerImpl
import world.mappable.navikitdemo.data.helpers.PermissionManagerImpl
import world.mappable.navikitdemo.data.helpers.SettingsBinderManagerImpl
import world.mappable.navikitdemo.domain.AnnotationsManager
import world.mappable.navikitdemo.domain.NavigationLayerManager
import world.mappable.navikitdemo.domain.SpeakerManager
import world.mappable.navikitdemo.domain.helpers.AlertDialogFactory
import world.mappable.navikitdemo.domain.helpers.MapTapManager
import world.mappable.navikitdemo.domain.helpers.PermissionManager
import world.mappable.navikitdemo.domain.helpers.SettingsBinderManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class ActivityModule {

    @Binds
    abstract fun alertDialogFactory(impl: AlertDialogFactoryImpl): AlertDialogFactory

    @Binds
    abstract fun mapTapManager(impl: MapTapManagerImpl): MapTapManager

    @Binds
    abstract fun permissionManager(impl: PermissionManagerImpl): PermissionManager

    @Binds
    abstract fun cameraManager(impl: CameraManagerImpl): world.mappable.navikitdemo.domain.CameraManager

    @Binds
    abstract fun navigationLayerManager(impl: NavigationLayerManagerImpl): NavigationLayerManager

    @Binds
    abstract fun settingsBinderManager(impl: SettingsBinderManagerImpl): SettingsBinderManager

    @Binds
    abstract fun speakerManager(impl: SpeakerImpl): SpeakerManager

    @Binds
    abstract fun annotationsManager(impl: AnnotationsManagerImpl): AnnotationsManager
}
