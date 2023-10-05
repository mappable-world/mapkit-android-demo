package world.mappable.mapkitdemo.objects

import android.app.Application
import world.mappable.mapkit.MapKitFactory

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Reading API key from BuildConfig.
        // Do not forget to add your MAPKIT_API_KEY property to local.properties file.
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
    }
}
