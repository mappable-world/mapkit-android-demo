package world.mappable.navikitdemo.ui.routevariants

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import world.mappable.mapkit.geometry.Point
import world.mappable.navikitdemo.ui.R
import world.mappable.navikitdemo.ui.common.BaseMapFragment
import world.mappable.navikitdemo.ui.databinding.FragmentRouteVariantsBinding
import world.mappable.navikitdemo.ui.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RouteVariantsFragment : BaseMapFragment(R.layout.fragment_route_variants) {

    private lateinit var binding: FragmentRouteVariantsBinding
    private val viewModel: RouteVariantsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRouteVariantsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) { closeRouteVariants() }

        binding.apply {
            viewMapControls.setFindMeButtonClickCallback { cameraManager.moveCameraToUserLocation() }
            buttonCancel.setOnClickListener { closeRouteVariants() }
            buttonGo.setOnClickListener { openGuidance() }
        }

        viewModel.hasRequestPoints.subscribe(viewLifecycleOwner) {
            binding.buttonGo.isVisible = !it
        }

        mapTapManager.longTapActions.subscribe(viewLifecycleOwner) {
            showRequestPointDialog(point = it)
        }
    }

    override fun calculateFocusBounds(): FocusBounds {
        return super.calculateFocusBounds().run {
            copy(
                bottom = bottom - binding.layoutCardContent.height,
                right = right - binding.viewMapControls.width,
            )
        }
    }

    private fun showRequestPointDialog(point: Point) {
        alertDialogFactory.requestPointDialog({
            viewModel.setToPoint(point)
        }, {
            viewModel.addViaPoint(point)
        }, {
            viewModel.setFromPoint(point)
        }).show()
    }

    private fun openGuidance() {
        val action = RouteVariantsFragmentDirections.actionRouteVariantsFragmentToGuidanceFragment()
        findNavController().navigate(action)
    }

    private fun closeRouteVariants() {
        viewModel.resetRouteVariants()
        findNavController().popBackStack()
    }
}
