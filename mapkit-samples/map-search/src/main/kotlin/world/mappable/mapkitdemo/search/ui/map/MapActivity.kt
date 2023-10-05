package world.mappable.mapkitdemo.search.ui.map

import android.os.Bundle
import android.text.TextWatcher
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import world.mappable.mapkit.Animation
import world.mappable.mapkit.GeoObject
import world.mappable.mapkit.MapKitFactory
import world.mappable.mapkit.ScreenPoint
import world.mappable.mapkit.ScreenRect
import world.mappable.mapkit.geometry.BoundingBox
import world.mappable.mapkit.geometry.Geometry
import world.mappable.mapkit.geometry.Point
import world.mappable.mapkit.map.CameraListener
import world.mappable.mapkit.map.CameraPosition
import world.mappable.mapkit.map.CameraUpdateReason
import world.mappable.mapkit.map.IconStyle
import world.mappable.mapkit.map.MapObjectTapListener
import world.mappable.mapkit.map.SizeChangedListener
import world.mappable.mapkitdemo.common.CommonId
import world.mappable.mapkitdemo.common.showToast
import world.mappable.mapkitdemo.search.R
import world.mappable.mapkitdemo.search.data.SelectedObjectHolder
import world.mappable.mapkitdemo.search.databinding.LayoutActivityBinding
import world.mappable.mapkitdemo.search.ui.details.DetailsDialogFragment
import world.mappable.runtime.image.ImageProvider
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class Activity : AppCompatActivity() {

    private lateinit var binding: LayoutActivityBinding
    private val viewModel: MapViewModel by viewModels()

    private val map by lazy { binding.mapview.mapWindow.map }
    private val suggestAdapter = SuggestsListAdapter()
    private lateinit var editQueryTextWatcher: TextWatcher

    private val cameraListener = CameraListener { _, _, reason, _ ->
        // Updating current visible region to apply research on map moved by user gestures.
        if (reason == CameraUpdateReason.GESTURES) {
            viewModel.setVisibleRegion(map.visibleRegion)
        }
    }

    private val searchResultPlacemarkTapListener = MapObjectTapListener { mapObject, _ ->
        // Show details dialog on placemark tap.
        val selectedObject = (mapObject.userData as? GeoObject)
        SelectedObjectHolder.selectedObject = selectedObject
        DetailsDialogFragment().show(supportFragmentManager, null)
        true
    }

    private val sizeChangedListener = SizeChangedListener { _, _, _ -> updateFocusRect() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.initialize(this)
        binding = LayoutActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapkitVersionTextView = binding.mapkitVersion.findViewById<TextView>(CommonId.mapkit_version_value)
        mapkitVersionTextView.text = MapKitFactory.getInstance().version

        map.move(START_POSITION)
        map.addCameraListener(cameraListener)
        viewModel.setVisibleRegion(map.visibleRegion)

        binding.mapview.mapWindow.addSizeChangedListener(sizeChangedListener)
        updateFocusRect()

        binding.apply {
            listSuggests.adapter = suggestAdapter

            buttonSearch.setOnClickListener { viewModel.startSearch() }
            buttonReset.setOnClickListener { viewModel.reset() }

            buttonSearchCoffee.setOnClickListener { editQuery.setText("Coffee") }
            buttonSearchMall.setOnClickListener { editQuery.setText("Mall") }
            buttonSearchHotel.setOnClickListener { editQuery.setText("Hotel") }

            editQueryTextWatcher = editQuery.doAfterTextChanged { text ->
                if (text.toString() == viewModel.uiState.value.query) return@doAfterTextChanged
                viewModel.setQueryText(text.toString())
            }

            editQuery.setOnEditorActionListener { _, _, _ ->
                viewModel.startSearch()
                true
            }
        }

        viewModel.uiState
            .flowWithLifecycle(lifecycle)
            .onEach {
                suggestAdapter.items =
                    (it.suggestState as? SuggestState.Success)?.items ?: emptyList()

                if (it.suggestState is SuggestState.Error) {
                    showToast("Suggest error, check your network connection")
                }

                val successSearchState = it.searchState as? SearchState.Success
                val searchItems = successSearchState?.items ?: emptyList()
                updateSearchResponsePlacemarks(searchItems)
                if (successSearchState?.zoomToItems == true) {
                    focusCamera(
                        searchItems.map { item -> item.point },
                        successSearchState.itemsBoundingBox
                    )
                }

                if (it.searchState is SearchState.Error) {
                    showToast("Search error, check your network connection")
                }

                binding.apply {
                    editQuery.apply {
                        if (text.toString() != it.query) {
                            removeTextChangedListener(editQueryTextWatcher)
                            setText(it.query)
                            addTextChangedListener(editQueryTextWatcher)
                        }
                    }
                    textSearchStatus.text =
                        "Search: ${it.searchState.toTextStatus()}; Suggest: ${it.suggestState.toTextStatus()}"
                    buttonSearch.isEnabled =
                        it.query.isNotEmpty() && it.searchState == SearchState.Off
                    buttonReset.isEnabled =
                        it.query.isNotEmpty() || it.searchState !is SearchState.Off
                    layoutCategoryButtons.isVisible = it.query.isEmpty()
                    editQuery.isEnabled = it.searchState is SearchState.Off
                }
            }
            .launchIn(lifecycleScope)

        viewModel.subscribeForSuggest().flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
        viewModel.subscribeForSearch().flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
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

    private fun updateSearchResponsePlacemarks(items: List<SearchResponseItem>) {
        map.mapObjects.clear()

        val imageProvider = ImageProvider.fromResource(this, R.drawable.search_result)

        items.forEach {
            map.mapObjects.addPlacemark(
                it.point,
                imageProvider,
                IconStyle().apply { scale = 0.5f }
            ).apply {
                addTapListener(searchResultPlacemarkTapListener)
                userData = it.geoObject
            }
        }
    }

    private fun focusCamera(points: List<Point>, boundingBox: BoundingBox) {
        if (points.isEmpty()) return

        val position = if (points.size == 1) {
            map.cameraPosition.run {
                CameraPosition(points.first(), zoom, azimuth, tilt)
            }
        } else {
            map.cameraPosition(Geometry.fromBoundingBox(boundingBox))
        }

        map.move(position, Animation(Animation.Type.SMOOTH, 0.5f), null)
    }

    private fun updateFocusRect() {
        val horizontal = resources.getDimension(R.dimen.window_horizontal_padding)
        val vertical = resources.getDimension(R.dimen.window_vertical_padding)
        val window = binding.mapview.mapWindow

        window.focusRect = ScreenRect(
            ScreenPoint(horizontal, vertical),
            ScreenPoint(window.width() - horizontal, window.height() - vertical),
        )
    }

    companion object {
        private val START_POSITION = CameraPosition(Point(25.198200, 55.272758), 13.0f, 0f, 0f)
    }
}
