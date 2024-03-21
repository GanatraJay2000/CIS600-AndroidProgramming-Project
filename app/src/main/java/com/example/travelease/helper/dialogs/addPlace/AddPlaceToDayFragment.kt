package com.example.travelease.helper.dialogs.addPlace

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.travelease.R
import com.example.travelease.SharedViewModel
import com.example.travelease.databinding.FragmentAddPlaceToDayBinding
import com.example.travelease.helper.DatabaseHelper
import com.example.travelease.helper.dialogs.search.SearchBottomSheetViewModel
import com.example.travelease.model.ItineraryDay
import com.example.travelease.model.Place
import com.example.travelease.model.Trip
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.auth

class AddPlaceToDayFragment : BottomSheetDialogFragment() {
    private val auth = com.google.firebase.Firebase.auth
        private var _binding: FragmentAddPlaceToDayBinding? = null
        private val binding get() = _binding!!
    private lateinit var placesClient: PlacesClient
    private lateinit var viewModel: AddPlaceToDayViewModel
        private var trips: List<Trip>?=null
        private var itineraryDays: List<ItineraryDay>?=null

        companion object {
            fun newInstance() = AddPlaceToDayFragment()
        }

        private lateinit var searchBottomSheetModel: SearchBottomSheetViewModel
        private lateinit var sharedViewModel: SharedViewModel

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            _binding = FragmentAddPlaceToDayBinding.inflate(inflater, container, false)
            val root: View = binding.root
            viewModel = ViewModelProvider(requireActivity())[AddPlaceToDayViewModel::class.java]
            Places.initialize(requireContext(), getString(R.string.google_maps_key))
            placesClient = Places.createClient(requireContext())
            searchBottomSheetModel = ViewModelProvider(requireActivity())[SearchBottomSheetViewModel::class.java]
            searchBottomSheetModel.turnOffNavigate()
            sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

            return root
        }
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val dbHelper = DatabaseHelper(requireContext())
            trips = dbHelper.getTripsForUser(auth.currentUser?.uid!!) // Assume this returns a list of Trip objects
            val tripTitles = trips!!.map { it.title }
            val tripAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tripTitles)
            tripAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.tripSpinner.adapter = tripAdapter

            binding.tripSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedTrip = trips!![position]
                    sharedViewModel.setTripId(selectedTrip.id!!)
                    selectedTrip.id?.let { populateItineraryDaysSpinner(it) }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle case when nothing is selected, if needed
                }
            }
        }

    private fun populateItineraryDaysSpinner(tripId: Int) {
        val dbHelper = DatabaseHelper(requireContext())
        itineraryDays = dbHelper.getItineraryDaysForTrip(tripId)
        val itineraryDayDates = itineraryDays!!.map { it.date.toString() } // Format date as needed
        val itineraryDayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, itineraryDayDates)
        itineraryDayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.itineraryDaySpinner.adapter = itineraryDayAdapter
    }


    override fun onStart() {
            super.onStart()
            val dialog = dialog as BottomSheetDialog?
            val bottomSheet =
                dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as ViewGroup?
            val bottomSheetBehavior = bottomSheet?.let { BottomSheetBehavior.from(it) }
            if (bottomSheetBehavior != null) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                bottomSheetBehavior.peekHeight = 0
            }

            // Optionally, set the height to full screen
            bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT

            binding.startPlanningButton.setOnClickListener {
                val selectedTrip = trips?.get(binding.tripSpinner.selectedItemPosition)
                val selectedItineraryDay = itineraryDays?.get(binding.itineraryDaySpinner.selectedItemPosition)
                val tripId = selectedTrip?.id
                val itineraryDayId = selectedItineraryDay?.id
                val placeId = sharedViewModel.locationId
                val place = getPlace(placeId.value!!)
                Log.v("AddPlaceToDayFragment", " placeId: ${place}")
                val dbHelper = DatabaseHelper(requireContext())
                //observe viewmodel.location
                viewModel.location.observe(viewLifecycleOwner) {
                    val myplace = Place(placeId.value!!, itineraryDayId, it.title, it.address, it.photoMetadata)
                    val id = dbHelper.addPlace(myplace)
                    Log.v("AddPlaceToDayFragment", "id: $id")
                    dismiss()
                }


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

    fun getPlace(placeId: String) {
        val placeFields = listOf(
            com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.ADDRESS,
            com.google.android.libraries.places.api.model.Place.Field.PHOTO_METADATAS
        )
        var someplace: com.example.travelease.model.Place? = null

        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
        placesClient.fetchPlace(request).addOnSuccessListener { response: FetchPlaceResponse ->
            var place = response.place
            val placeId = place.id
            val placeName = place.name
            val placeAddress = place.address
            val photoMetadata = place.photoMetadatas

            someplace = com.example.travelease.model.Place(placeId, null, placeName, placeAddress, photoMetadata)

            viewModel.setLocation(someplace!!)
        }

    }

    }