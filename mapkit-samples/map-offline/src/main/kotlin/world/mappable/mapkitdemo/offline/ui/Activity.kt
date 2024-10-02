package world.mappable.mapkitdemo.offline.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import world.mappable.mapkit.MapKitFactory
import world.mappable.mapkit.geometry.Point
import world.mappable.mapkit.map.CameraPosition
import world.mappable.mapkitdemo.common.CommonId
import world.mappable.mapkitdemo.common.showToast
import world.mappable.mapkitdemo.offline.databinding.ActivityLayoutBinding
import world.mappable.mapkitdemo.offline.utils.OfflineCacheError
import world.mappable.mapkitdemo.offline.utils.offlineCacheErrorsFlow
import world.mappable.runtime.LocalError
import world.mappable.runtime.network.RemoteError
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class Activity : AppCompatActivity() {
    private lateinit var binding: ActivityLayoutBinding
    private lateinit var map: world.mappable.mapkit.map.Map

    private val offlineCacheManager by lazy { MapKitFactory.getInstance().offlineCacheManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(this)
        binding = ActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        map = binding.viewMap.mapWindow.map

        binding.viewVersion.findViewById<TextView>(CommonId.mapkit_version_value).apply {
            text = MapKitFactory.getInstance().version
        }

        map.move(POSITION)

        offlineCacheErrorsFlow(offlineCacheManager)
            .flowWithLifecycle(lifecycle)
            .onEach {
                val errorType = when (it.error) {
                    is LocalError -> "Local"
                    is RemoteError -> "Remote"
                    else -> "Undefined"
                }
                when (it) {
                    is OfflineCacheError.Manager ->
                        showToast("$errorType error in OfflineCacheManager: ${it.error}")
                    is OfflineCacheError.Region ->
                        showToast("$errorType error when process region with ${it.regionId} id: ${it.error}")
                }
            }
            .launchIn(lifecycleScope)
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.viewMap.onStart()
    }

    override fun onStop() {
        binding.viewMap.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    fun mapMove(point: Point) {
        val position = map.cameraPosition.run {
            CameraPosition(point, zoom, azimuth, tilt)
        }
        map.move(position)
    }

    companion object {
        private val POINT = Point(55.751280, 37.629720)
        private val POSITION = CameraPosition(POINT, 14.0f, 150.0f, 0f)
    }
}
