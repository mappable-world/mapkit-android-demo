package world.mappable.mapkitdemo.placemark

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import world.mappable.mapkit.MapKitFactory
import world.mappable.mapkit.geometry.Point
import world.mappable.mapkit.map.CameraPosition
import world.mappable.mapkit.map.MapObjectTapListener
import world.mappable.mapkit.mapview.MapView
import world.mappable.mapkitdemo.common.CommonDrawables
import world.mappable.mapkitdemo.common.CommonId
import world.mappable.mapkitdemo.common.showToast
import world.mappable.runtime.image.ImageProvider

class Activity : AppCompatActivity() {
    private lateinit var mapView: MapView

    private val placemarkTapListener = MapObjectTapListener { _, point ->
        showToast("Tapped the point (${point.longitude}, ${point.latitude})")
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_layout)
        mapView = findViewById(R.id.mapview)

        val mapkitVersionView = findViewById<LinearLayout>(R.id.mapkit_version)
        val mapkitVersionTextView =
            mapkitVersionView.findViewById<TextView>(CommonId.mapkit_version_value)
        mapkitVersionTextView.text = MapKitFactory.getInstance().version

        val map = mapView.mapWindow.map
        map.move(POSITION)

        val imageProvider = ImageProvider.fromResource(this, CommonDrawables.ic_dollar_pin)
        val placemarkObject = map.mapObjects.addPlacemark().apply {
            geometry = POINT
            setIcon(imageProvider)
        }
        placemarkObject.addTapListener(placemarkTapListener)
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    companion object {
        private val POINT = Point(25.198176, 55.272924)
        private val POSITION = CameraPosition(POINT, 17.0f, 150.0f, 30.0f)
    }
}
