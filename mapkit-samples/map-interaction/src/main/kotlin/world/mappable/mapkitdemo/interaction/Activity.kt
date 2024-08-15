package world.mappable.mapkitdemo.interaction

import android.graphics.PointF
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import world.mappable.mapkit.Animation
import world.mappable.mapkit.MapKitFactory
import world.mappable.mapkit.ScreenPoint
import world.mappable.mapkit.ScreenRect
import world.mappable.mapkit.geometry.Geometry
import world.mappable.mapkit.geometry.Point
import world.mappable.mapkit.geometry.Polyline
import world.mappable.mapkit.layers.GeoObjectTapListener
import world.mappable.mapkit.map.CameraPosition
import world.mappable.mapkit.map.GeoObjectSelectionMetadata
import world.mappable.mapkit.map.IconStyle
import world.mappable.mapkit.map.InputListener
import world.mappable.mapkit.map.Map
import world.mappable.mapkit.map.MapWindow
import world.mappable.mapkit.map.PlacemarkMapObject
import world.mappable.mapkit.map.SizeChangedListener
import world.mappable.mapkitdemo.common.CommonDrawables
import world.mappable.mapkitdemo.common.CommonId
import world.mappable.mapkitdemo.common.showToast
import world.mappable.mapkitdemo.interaction.databinding.ActivityLayoutBinding
import world.mappable.runtime.image.ImageProvider

class Activity : AppCompatActivity() {

    private lateinit var binding: ActivityLayoutBinding
    private lateinit var mapWindow: MapWindow
    private lateinit var map: Map
    private lateinit var placemarkMapObject: PlacemarkMapObject

    private val sizeChangedListener = SizeChangedListener { _, _, _ ->
        // Recalculate FocusRect and FocusPoint on every map's size change
        updateFocusInfo()
    }

    private val inputListener = object : InputListener {
        override fun onMapLongTap(map: Map, point: Point) {
            // Move placemark after long tap
            placemarkMapObject.geometry = point
        }

        override fun onMapTap(map: Map, point: Point) = Unit
    }

    private val geoObjectTapListener = GeoObjectTapListener {
        // Move camera to selected geoObject
        val point = it.geoObject.geometry.firstOrNull()?.point ?: return@GeoObjectTapListener true
        map.cameraPosition.run {
            val position = CameraPosition(point, zoom, azimuth, tilt)
            map.move(position, SMOOTH_ANIMATION, null)
        }

        val selectionMetadata =
            it.geoObject.metadataContainer.getItem(GeoObjectSelectionMetadata::class.java)
        map.selectGeoObject(selectionMetadata)
        showToast("Tapped ${it.geoObject.name} id = ${selectionMetadata.objectId}")

        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(this)
        binding = ActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapkitVersionTextView =
            binding.mapkitVersion.findViewById<TextView>(CommonId.mapkit_version_value)
        mapkitVersionTextView.text = MapKitFactory.getInstance().version

        mapWindow = binding.mapview.mapWindow
        map = mapWindow.map

        mapWindow.addSizeChangedListener(sizeChangedListener)
        updateFocusInfo()

        map.addInputListener(inputListener)
        map.addTapListener(geoObjectTapListener)

        map.move(START_POSITION, START_ANIMATION) { showToast("Initial camera move") }

        createPlacemark(START_POSITION.target)

        val polyline = Polyline(POINTS)
        map.mapObjects.addPolyline(polyline)

        binding.apply {
            // Changing camera's zoom by controls on the map
            buttonMinus.setOnClickListener { changeZoomByStep(-ZOOM_STEP) }
            buttonPlus.setOnClickListener { changeZoomByStep(ZOOM_STEP) }

            buttonFocusGeometry.setOnClickListener {
                val geometry = Geometry.fromPolyline(polyline)
                val position = map.cameraPosition(geometry)
                map.move(position, SMOOTH_ANIMATION, null)
            }
            buttonFocusPlacemark.setOnClickListener {
                val position = map.cameraPosition.run {
                    CameraPosition(placemarkMapObject.geometry, zoom, azimuth, tilt)
                }
                map.move(position, SMOOTH_ANIMATION, null)
            }
            buttonCreatePlacemark.setOnClickListener {
                // Usage of the screen coordinates to display placemarks in the center of a screen.
                val focusPoint = mapWindow.focusPoint ?: return@setOnClickListener
                val point = mapWindow.screenToWorld(focusPoint) ?: return@setOnClickListener
                placemarkMapObject.geometry = point
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapview.onStart()
    }

    override fun onStop() {
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    private fun createPlacemark(point: Point) {
        placemarkMapObject = map.mapObjects.addPlacemark().apply {
            geometry = point
            setIcon(
                ImageProvider.fromResource(this@Activity, CommonDrawables.ic_dollar_pin),
                IconStyle().apply { anchor = PointF(0.5f, 1.0f) })
            isDraggable = true
        }
    }

    private fun changeZoomByStep(value: Float) {
        with(map.cameraPosition) {
            map.move(
                CameraPosition(target, zoom + value, azimuth, tilt),
                SMOOTH_ANIMATION,
                null,
            )
        }
    }

    private fun updateFocusInfo() {
        val defaultPadding = resources.getDimension(R.dimen.default_focus_rect_padding)
        val bottomPadding = binding.layoutBottomCard.measuredHeight
        val rightPadding = binding.buttonMinus.measuredWidth
        // Focus rect consider a bottom card UI and map zoom controls.
        mapWindow.focusRect = ScreenRect(
            ScreenPoint(defaultPadding, defaultPadding),
            ScreenPoint(
                mapWindow.width() - rightPadding - defaultPadding,
                mapWindow.height() - bottomPadding - defaultPadding,
            )
        )
        mapWindow.focusPoint = ScreenPoint(
            mapWindow.width() / 2f,
            mapWindow.height() / 2f,
        )
    }

    companion object {
        private const val ZOOM_STEP = 1f

        private val START_ANIMATION = Animation(Animation.Type.LINEAR, 1f)
        private val SMOOTH_ANIMATION = Animation(Animation.Type.SMOOTH, 0.4f)

        private val START_POSITION = CameraPosition(Point(25.196862, 55.274684), 14f, 0f, 0f)

        private val POINTS = listOf(
            Point(25.185258, 55.277641),
            Point(25.191593, 55.268581),
            Point(25.194521, 55.267103),
            Point(25.194521, 55.267103),
            Point(25.199055, 55.273207),
            Point(25.200917, 55.277665),
            Point(25.194865, 55.288942),
            Point(25.190461, 55.282314),
        )
    }
}
