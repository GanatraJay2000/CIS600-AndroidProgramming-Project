package com.example.project.ui.trip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.example.project.R
import com.example.project.databinding.FragmentTripBinding
import com.example.project.helpers.search.SearchBottomSheetViewModel
import com.example.project.models.Trip
import com.example.project.models.dummyTrips
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class TripFragment : Fragment() {

    private var _binding: FragmentTripBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var trip: Trip

    companion object {
        fun newInstance() = TripFragment()
    }

    private lateinit var viewModel: TripViewModel
    private lateinit var searchBottomSheetModel: SearchBottomSheetViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[TripViewModel::class.java]

        viewModel = ViewModelProvider(this).get(TripViewModel::class.java)
        searchBottomSheetModel = ViewModelProvider(requireActivity()).get(SearchBottomSheetViewModel::class.java)
        searchBottomSheetModel.turnOnNavigate()

        val tripId = arguments?.getInt("tripId")
        if (tripId != null) {
            trip = dummyTrips.find { it?.id == tripId }!!
            binding.tripTitleTextView.text = trip.title
            binding.tripBannerImage.load(trip.imageUrl) {
                placeholder(R.drawable.image_placeholder)
                error(R.drawable.profile_placeholder)
            }

            tabLayout = binding.tabs
            viewPager = binding.tripViewPager

            viewPager.adapter = object : FragmentStateAdapter(this) {
                override fun getItemCount(): Int = 2

                override fun createFragment(position: Int): Fragment {
                    return when (position) {
                        0 -> TripOverviewFragment()
                        1 -> ItineraryFragment().apply {
                            arguments = Bundle().apply {
                                putSerializable("trip", trip)
                            }
                        }

                        else -> throw IllegalStateException("Unexpected position $position")
                    }
                }
            }
            // Set up TabLayout with ViewPager2
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> "Overview"
                    1 -> "Itinerary"
                    else -> null
                }
            }.attach()
        }
    }


    fun navigateToDestination(destinationId: Int) {
        val navController = findNavController()
        val currentDestination = navController.currentDestination?.id

        if (currentDestination != destinationId) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(navController.graph.startDestinationId, false)
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .build()
            navController.navigate(destinationId, null, navOptions)
        }
    }

    fun navigateToDestination(destinationId: Int, locationId: Int) {
        val navController = findNavController()
        val currentDestination = navController.currentDestination?.id

        if (currentDestination != destinationId) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(navController.graph.startDestinationId, false)
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .build()
            val bundle = Bundle().apply { putInt("locationId", locationId) }
            navController.navigate(destinationId, bundle, navOptions)
        }
    }
}
