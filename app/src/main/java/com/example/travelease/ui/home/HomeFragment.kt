package com.example.travelease.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelease.R
import com.example.travelease.SharedViewModel
import com.example.travelease.adapters.LocationCardAdapter
import com.example.travelease.databinding.FragmentHomeBinding
import com.example.travelease.helper.dialogs.addTrip.AddTripFragment
import com.example.travelease.model.Place
import com.example.travelease.model.PlaceData
import com.example.travelease.ui.location.LocationViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.cypButton.setOnClickListener {
            val addTripFragment = AddTripFragment()
            addTripFragment.show(parentFragmentManager, addTripFragment.tag)
        }

        val places = PlaceData()
        val topLocations = places.topLocations
        val nearbyLocations = places.nearbyLocations

        val tlAdapter  = LocationCardAdapter(requireContext(), topLocations) { locationId ->
            sharedViewModel.setLocationId(locationId)
            navigateToDestination(R.id.nav_location)
        }
        val tlRecyclerView = binding.dashboardTopLocations
        tlRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        tlRecyclerView.adapter = tlAdapter

        val nlAdapter  = LocationCardAdapter(requireContext(), nearbyLocations) { locationId ->
            sharedViewModel.setLocationId(locationId)
            navigateToDestination(R.id.nav_location)
        }
        val nlRecyclerView = binding.dashboardNearbyLocations
        nlRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        nlRecyclerView.adapter = nlAdapter
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
}