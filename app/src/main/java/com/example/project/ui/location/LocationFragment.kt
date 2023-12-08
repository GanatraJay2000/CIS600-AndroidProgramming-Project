package com.example.project.ui.location

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project.R
import com.example.project.adapters.PopularPlacesAdapter
import com.example.project.databinding.FragmentLocationBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.OpeningHours
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.model.RectangularBounds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class LocationFragment : Fragment() {
    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!

    private lateinit var place: Place
    private lateinit var placeDetails: PlaceDetails
    private lateinit var placesClient: PlacesClient
    private var latitude: Double? = null
    private var longitude: Double? = null

    private lateinit var popularPlacesAdapter: PopularPlacesAdapter
    private var nearbyPlacesList = mutableListOf<Place>()

    companion object {
        private const val ARG_PLACE = "place"
        private const val ARG_POPULAR_PLACES = "popularPlaces"
        private const val ARG_LATITUDE = "latitude"
        private const val ARG_LONGITUDE = "longitude"

        fun newInstance(
            place: Place,
            popularPlaces: List<String>,
            latitude: Double?,
            longitude: Double?
        ): LocationFragment {
            val fragment = LocationFragment()
            val args = Bundle().apply {
                putParcelable(ARG_PLACE, place)
                putStringArrayList(ARG_POPULAR_PLACES, ArrayList(popularPlaces))
                putDouble(ARG_LATITUDE, latitude ?: 0.0)
                putDouble(ARG_LONGITUDE, longitude ?: 0.0)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            place = it.getParcelable(ARG_PLACE) ?: throw IllegalStateException("Place data is required.")
            val popularPlaces = it.getStringArrayList(ARG_POPULAR_PLACES) ?: emptyList<String>()
            latitude = it.getDouble(ARG_LATITUDE)
            longitude = it.getDouble(ARG_LONGITUDE)
            placeDetails = PlaceDetails(
                name = place.name,
                address = place.address,
                photoMetadatas = place.photoMetadatas,
                types = place.types,
                openingHours = place.openingHours,
                rating = place.rating,
                description = place.editorialSummary,
                popularPlaces = popularPlaces
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Places API
        Places.initialize(requireContext(), getString(R.string.google_maps_key)) // Replace with your API key
        placesClient = Places.createClient(requireContext())

        // Now set the values in your UI components
        updateUIWithPlaceDetails(placeDetails)
        place.id?.let { latitude?.let { it1 -> longitude?.let { it2 -> logNearbyPlaces(it1.toDouble(), it2.toDouble()) } } }
        logPopularPlaces(placeDetails.popularPlaces)

        setupRecyclerView()

        // Fetch and display nearby places
        fetchNearbyPlaces()
    }

    private fun setupRecyclerView() {
        popularPlacesAdapter = PopularPlacesAdapter(nearbyPlacesList)
        binding.popularPlacesRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.popularPlacesRecyclerView.adapter = popularPlacesAdapter
    }

    private fun fetchNearbyPlaces() {
        // Define location parameters
        val locationBias = RectangularBounds.newInstance(
            LatLng(latitude ?: (0.0 - 0.1), longitude ?: (0.0 - 0.1)),
            LatLng(latitude ?: (0.0 + 0.1), longitude ?: (0.0 + 0.1))
        )

        // Fetch places using Google Places API and update RecyclerView
        // This is a simplification. In a real app, you would make an API call.
        // For each place fetched, add it to `nearbyPlacesList` and update the adapter.
        // For example:
        // nearbyPlacesList.add(fetchedPlace)
        // popularPlacesAdapter.notifyDataSetChanged()
    }

    private fun fetchPlaceDetails(placeId: String) {
        // Define the fields you want to retrieve for place details
        val popularPlaceTypes = listOf(Place.Type.RESTAURANT, Place.Type.MUSEUM) // Define your types
        val popularPlacesNearby = mutableListOf<String>()

        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.PHOTO_METADATAS,
            Place.Field.TYPES,
            Place.Field.OPENING_HOURS,
            Place.Field.RATING,
            // Add more fields as needed
        )

        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
        placesClient.fetchPlace(request).addOnSuccessListener { response: FetchPlaceResponse ->
            val place = response.place

            // Handle additional place details
            placeDetails = place.placeTypes?.let {
                PlaceDetails(
                    place.name,
                    place.address,
                    place.photoMetadatas,
                    place.types,
                    place.openingHours,
                    place.rating,
                    place.editorialSummary,
                    popularPlaces = it // Replace with actual popular places
                )
            }!!

            // Update the UI with additional place details
            updateUIWithPlaceDetails(placeDetails)
        }.addOnFailureListener { exception: Exception ->
            if (exception is ApiException) {
                val statusCode = exception.statusCode
                Log.e("LocationFragment", "Place details not found: ${exception.message}, statusCode: $statusCode")
            }
        }
    }

    private fun updateUIWithPlaceDetails(placeDetails: PlaceDetails) {
        // Update the UI with additional place details

        // Update the place description
        if (placeDetails.description != null) {
            binding.placeDescriptionTextView.text = placeDetails.description
        } else {
            binding.placeDescriptionTextView.visibility = View.GONE
        }

        // Update the top popular places
        if (placeDetails.types != null) {
            val topPlacesText = placeDetails.types.joinToString(", ") { it.name }
            binding.placeAttractionsTextView.text = topPlacesText
        } else {
            binding.placeAttractionsTextView.visibility = View.GONE
        }

        // Update the place image (if available)
        if (!placeDetails.photoMetadatas.isNullOrEmpty()) {
            val photoMetadata = placeDetails.photoMetadatas[0] // You can choose which photo to display
            val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(500) // Set the desired width of the image
                .setMaxHeight(300) // Set the desired height of the image
                .build()

            placesClient.fetchPhoto(photoRequest).addOnSuccessListener { fetchPhotoResponse ->
                val bitmap = fetchPhotoResponse.bitmap
                binding.placeImageView.setImageBitmap(bitmap)
            }.addOnFailureListener { exception ->
                Log.e("LocationFragment", "Error loading photo: ${exception.message}")
            }
        } else {
            // Hide or set a placeholder for the image if not available
            binding.placeImageView.visibility = View.GONE
        }

        // Update the title (if needed)
        if (placeDetails.name != null) {
            binding.placeNameTextView.text = placeDetails.name
        } else {
            // Handle the case where the name is not available
            // You can set a default title or hide the title view
        }

        // Update opening hours (if available)
        if (placeDetails.openingHours != null) {
            val openingHoursText = placeDetails.openingHours.weekdayText.joinToString("\n")
            binding.openingHoursTextView.text = openingHoursText
        } else {
            binding.openingHoursTextView.visibility = View.GONE
        }

        // Update rating (if available)
        if (placeDetails.rating != null) {
            binding.ratingBar.rating = placeDetails.rating.toFloat()
            binding.ratingTextView.text = placeDetails.rating.toString()
        } else {
            binding.ratingBar.visibility = View.GONE
            binding.ratingTextView.visibility = View.GONE
        }

        if (placeDetails.popularPlaces.isNotEmpty()) {
            val popularPlacesText = placeDetails.popularPlaces.joinToString(", ")
            binding.placeAttractionsTextView.text = popularPlacesText
        } else {
            binding.placeAttractionsTextView.visibility = View.GONE
        }
    }

    private fun logNearbyPlaces(latitude: Double, longitude: Double) {
        // Define the types of nearby places you want to include (e.g., tourist attractions, museums, restaurants)
        val nearbyPlaceTypes = listOf(Place.Field.TYPES) // You can specify more fields as needed

        // Define the location bias based on latitude and longitude
        val locationBias = RectangularBounds.newInstance(
            LatLng(latitude - 0.1, longitude - 0.1), // Adjust the values as needed
            LatLng(latitude + 0.1, longitude + 0.1)  // Adjust the values as needed
        )

        val request = FindCurrentPlaceRequest.builder(nearbyPlaceTypes)
            .build()


        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Handle permission request
            // TODO: Request location permissions here
            return
        }

        placesClient.findCurrentPlace(request)
            .addOnSuccessListener { response: FindCurrentPlaceResponse ->
                val nearbyPlaces = response.placeLikelihoods

                // Extract nearby places and popular attractions from the response
                val nearbyPopularPlaces = mutableListOf<String>()

                for (likelihood in nearbyPlaces) {
                    val place = likelihood.place
                    val placeName = place.name
                    if (placeName != null) {
                        nearbyPopularPlaces.add(placeName)
                    }
                }

                // Update the UI with nearby popular places
                updateNearbyPopularPlaces(nearbyPopularPlaces)
            }
            .addOnFailureListener { exception: Exception ->
                if (exception is ApiException) {
                    val statusCode = exception.statusCode
                    Log.e("LocationFragment", "Nearby places search failed: ${exception.message}, statusCode: $statusCode")
                }
            }
    }



    private fun logPopularPlaces(popularPlaces: List<String>) {
        // Log popular places
        Log.d("LocationFragment1", "Popular places nearby: ${popularPlaces.joinToString(", ")}")
    }

    private fun updateNearbyPopularPlaces(placeIds: List<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            // Clear the existing list
            nearbyPlacesList.clear()

            placeIds.forEach { placeId ->
                // Fetch details for each place ID
                val placeFields = listOf(
                    Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,
                    Place.Field.LAT_LNG, Place.Field.PHOTO_METADATAS
                    // Add other fields you need
                )

                val request = FetchPlaceRequest.newInstance(placeId, placeFields)
                try {
                    val response = placesClient.fetchPlace(request).await()
                    val place = response.place

                    // Add the fetched place to the list
                    nearbyPlacesList.add(place)
                } catch (e: ApiException) {
                    Log.e("LocationFragment", "Error fetching place details: ${e.message}")
                }
            }

            // Update the RecyclerView on the main thread
            withContext(Dispatchers.Main) {
                popularPlacesAdapter.notifyDataSetChanged()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class PlaceDetails(
    val name: String?,
    val address: String?,
    val photoMetadatas: List<PhotoMetadata>?,
    val types: List<Place.Type>?,
    val openingHours: OpeningHours?,
    val rating: Double?,
    val description: String?,
    val popularPlaces: List<String>
    // Add more fields as needed
)

