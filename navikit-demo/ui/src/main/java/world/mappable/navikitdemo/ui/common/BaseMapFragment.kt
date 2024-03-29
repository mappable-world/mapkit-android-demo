package world.mappable.navikitdemo.ui.common

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import world.mappable.mapkit.ScreenPoint
import world.mappable.mapkit.ScreenRect
import world.mappable.mapkit.logo.Alignment
import world.mappable.mapkit.logo.HorizontalAlignment
import world.mappable.mapkit.logo.Padding
import world.mappable.mapkit.logo.VerticalAlignment
import world.mappable.mapkit.map.MapWindow
import world.mappable.mapkit.map.SizeChangedListener
import world.mappable.navikitdemo.domain.CameraManager
import world.mappable.navikitdemo.domain.SettingsManager
import world.mappable.navikitdemo.domain.helpers.AlertDialogFactory
import world.mappable.navikitdemo.domain.helpers.MapTapManager
import world.mappable.navikitdemo.ui.R
import world.mappable.navikitdemo.ui.settings.SettingsFragmentDirections
import world.mappable.navikitdemo.ui.settings.settingslist.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseMapFragment(@LayoutRes resId: Int) : Fragment(resId) {

    @Inject
    lateinit var mapWindow: MapWindow

    @Inject
    lateinit var cameraManager: CameraManager

    @Inject
    lateinit var settingsManager: SettingsManager

    @Inject
    lateinit var mapTapManager: MapTapManager

    @Inject
    lateinit var alertDialogFactory: AlertDialogFactory

    /**
     * Represents map's focus rect and point.
     */
    data class FocusBounds(
        val left: Float,
        val top: Float,
        val right: Float,
        val bottom: Float,
        val point: Pair<Float, Float>,
    )

    protected val mapControlsView: MapControlsView
        get() = requireView().findViewById(R.id.view_map_controls)

    private val sizeChangedListener = SizeChangedListener { _, _, _ -> onMapWindowSizeUpdated() }

    private val focusRectPadding by lazy { resources.getDimension(R.dimen.focus_rect_padding) }
    private val mappableLogoPadding by lazy { resources.getDimension(R.dimen.mappable_logo_padding) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapWindow.addSizeChangedListener(sizeChangedListener)
        view.post { onMapWindowSizeUpdated() }

        mapControlsView.apply {
            setSettingsButtonClickCallback { openSettings() }
            setPlusButtonClickCallback { cameraManager.changeZoomByStep(CameraManager.ZoomStep.PLUS) }
            setMinusButtonClickCallback { cameraManager.changeZoomByStep(CameraManager.ZoomStep.MINUS) }
        }

        cameraManager.start(viewLifecycleOwner.lifecycleScope)
    }

    /**
     * Recalculates visible bounds of the map.
     */
    private fun onMapWindowSizeUpdated() {
        if (view == null) return
        if (!settingsManager.focusRectsAutoUpdate.value) return

        with(calculateFocusBounds()) {
            mapWindow.focusRect = ScreenRect(
                ScreenPoint(left, top),
                ScreenPoint(right, bottom)
            )
            mapWindow.focusPoint = ScreenPoint(point.first, point.second)
            // Changes Mappable logo position, to stay it visible to users.
            mapWindow.map.logo.apply {
                setAlignment(Alignment(HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM))
                val verticalPadding =
                    (mapWindow.height() - bottom - focusRectPadding + mappableLogoPadding).toInt()
                setPadding(Padding(0, verticalPadding))
            }
        }
    }

    open fun calculateFocusBounds(): FocusBounds {
        return FocusBounds(
            focusRectPadding,
            focusRectPadding,
            mapWindow.width() - focusRectPadding,
            mapWindow.height() - focusRectPadding,
            mapWindow.width() / 2f to mapWindow.height() / 2f,
        )
    }

    private fun openSettings() {
        val action = SettingsFragmentDirections.actionGlobalSettingsFragment(SettingsScreen.START)
        findNavController().navigate(action)
    }
}
