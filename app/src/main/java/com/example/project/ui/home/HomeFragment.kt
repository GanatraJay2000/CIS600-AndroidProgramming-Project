package com.example.project.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project.R
import com.example.project.adapters.FeaturedGuidesAdapter
import com.example.project.adapters.TopLocationsAdapter
import com.example.project.databinding.FragmentHomeBinding
import com.example.project.helpers.add_trip.AddTripFragment
import com.example.project.models.dummyGuides
import com.example.project.models.dummyLocations

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.cypButton.setOnClickListener {
            val addTripFragment = AddTripFragment()
            addTripFragment.show(parentFragmentManager, addTripFragment.tag)
        }

        //  Featured Guides
        val guidesList = dummyGuides
        val flAdapter = FeaturedGuidesAdapter(guidesList)


        val locationsList = dummyLocations
        val tlAdapter  = TopLocationsAdapter(locationsList) { locationId ->
            navigateToDestination(R.id.nav_location, locationId)
        }
        val tlRecyclerView = binding.dashboardTopLocations
        tlRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        tlRecyclerView.adapter = tlAdapter

        val spotsList = dummyLocations
        val snmAdapter = TopLocationsAdapter(locationsList) { locationId ->
            navigateToDestination(R.id.nav_location, locationId)
        }
        val snmRecyclerView = binding.dashboardSpotsNearMe
        snmRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        snmRecyclerView.adapter = snmAdapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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