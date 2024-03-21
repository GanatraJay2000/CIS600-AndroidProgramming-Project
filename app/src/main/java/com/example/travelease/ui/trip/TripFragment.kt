package com.example.travelease.ui.trip

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.travelease.R
import com.example.travelease.SharedViewModel
import com.example.travelease.databinding.FragmentTripBinding
import com.example.travelease.helper.DatabaseHelper
import com.example.travelease.helper.dialogs.search.SearchBottomSheetViewModel
import com.example.travelease.model.Trip
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class TripFragment : Fragment() {

    private var _binding: FragmentTripBinding? = null
    private val binding get() = _binding!!
    private lateinit var placesClient: PlacesClient

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var trip: Trip
    companion object {
        fun newInstance() = TripFragment()
    }

    private lateinit var viewModel: TripViewModel
    private lateinit var searchBottomSheetModel: SearchBottomSheetViewModel
    private lateinit var sharedViewModel: SharedViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripBinding.inflate(inflater, container, false)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        Places.initialize(requireContext(), getString(R.string.google_maps_key))
        placesClient = Places.createClient(requireContext())


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(TripViewModel::class.java)
        searchBottomSheetModel = ViewModelProvider(requireActivity()).get(SearchBottomSheetViewModel::class.java)
        searchBottomSheetModel.turnOnNavigate()

        val tripId = sharedViewModel.tripId!!
        val dbHelper = DatabaseHelper(requireContext())
        val trip = dbHelper.getTripWithDetails(tripId)!!

        var place: com.example.travelease.model.Place?= null
        val placeId = trip.placeId
        place = getPlace(placeId.toString())
        Log.v("TripFragment", place.toString());


        binding.tripTitleTextView.text = trip.title
        getPhoto(binding.tripBannerImage, trip.placeId.toString())

        tabLayout = binding.tabs
        viewPager = binding.tripViewPager
        // Setup the adapter with OverviewFragment and ItineraryFragment
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


    fun setPhoto(imageView: ImageView, metadata: MutableList<PhotoMetadata>) {
        val photoMetadata = metadata.first()
        val photoRequest = FetchPhotoRequest.builder(photoMetadata).setMaxWidth(500).setMaxHeight(300).build()
        placesClient.fetchPhoto(photoRequest)
            .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                val bitmap = fetchPhotoResponse.bitmap
                imageView.setImageBitmap(bitmap)
            }
    }
    fun getPhoto(imageView: ImageView, placeId: String){
        val fields = listOf(com.google.android.libraries.places.api.model.Place.Field.PHOTO_METADATAS)
        val placeRequest = FetchPlaceRequest.newInstance(placeId, fields)
        placesClient.fetchPlace(placeRequest)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place
                val metadata = place.photoMetadatas
                if (metadata == null || metadata.isEmpty()) return@addOnSuccessListener
                setPhoto(imageView, metadata)
            }
    }
    fun getPlace(placeId: String): com.example.travelease.model.Place? {
        val placeFields = listOf(
            Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,
            Place.Field.PHOTO_METADATAS
        )
        var myplace: com.example.travelease.model.Place? = null
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
        placesClient.fetchPlace(request).addOnSuccessListener { response: FetchPlaceResponse ->
            val place = response.place
            val placeId = place.id
            val placeName = place.name
            val placeAddress = place.address
            val photoMetadata = place.photoMetadatas
            myplace = com.example.travelease.model.Place(placeId, null,  placeName, placeAddress, photoMetadata)
            viewModel.setPlace(myplace!!)

        }
        return myplace
    }
}