package com.example.project.ui.location

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project.R
import com.example.project.adapters.PopularPlacesAdapter
import com.example.project.api.PlacesService
import com.example.project.databinding.FragmentLocationBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.OpeningHours
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URLEncoder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class LocationFragment : Fragment() {
    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!

    private lateinit var place: Place
    private lateinit var placeDetails: PlaceDetails
    private lateinit var placesClient: PlacesClient
    private var latitude: Double? = null
    private var longitude: Double? = null

    private lateinit var popularPlacesAdapter: PopularPlacesAdapter
    private var nearbyPlacesList = mutableListOf<MyPlace>()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/maps/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val placesApiService = retrofit.create(PlacesService::class.java)

    companion object {
        private const val ARG_PLACE = "place"
        private const val ARG_POPULAR_PLACES = "popularPlaces"
        private const val ARG_LATITUDE = "latitude"
        private const val ARG_LONGITUDE = "longitude"
        private const val ARG_LOCATION_NAME = "locationName"

        fun newInstance(
            place: Place,
            popularPlaces: List<String>,
            latitude: Double?,
            longitude: Double?,
            locationName: String?
        ): LocationFragment {
            val fragment = LocationFragment()
            val args = Bundle().apply {
                putParcelable(ARG_PLACE, place)
                putStringArrayList(ARG_POPULAR_PLACES, ArrayList(popularPlaces))
                putDouble(ARG_LATITUDE, latitude ?: 0.0)
                putDouble(ARG_LONGITUDE, longitude ?: 0.0)
                putString(ARG_LOCATION_NAME, locationName)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            place = it.getParcelable(ARG_PLACE) ?: throw IllegalStateException("Place data is required.")
            val locationName = arguments?.getString(ARG_LOCATION_NAME)
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

        val locationName = arguments?.getString(ARG_LOCATION_NAME)
        if (!locationName.isNullOrEmpty()) {
            searchPlacesByName(locationName)
        }
    }

    private fun setupRecyclerView() {
        popularPlacesAdapter = PopularPlacesAdapter(nearbyPlacesList)
        binding.popularPlacesRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.popularPlacesRecyclerView.adapter = popularPlacesAdapter
    }

    private fun fetchNearbyPlaces() {
        val locationBias = RectangularBounds.newInstance(
            LatLng(latitude ?: (0.0 - 0.1), longitude ?: (0.0 - 0.1)),
            LatLng(latitude ?: (0.0 + 0.1), longitude ?: (0.0 + 0.1))
        )

        // Use a coroutine to fetch nearby places asynchronously
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Create a request to find nearby places
                val request = FindCurrentPlaceRequest.newInstance(listOf(Place.Field.NAME))

                // Check for location permissions
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Handle the case where location permissions are not granted.
                    // You can request permissions here.
                    return@launch
                }

                // Fetch nearby places using PlacesClient
                val response = placesClient.findCurrentPlace(request).await()

                // Extract and log nearby places
                val nearbyPlaces = response.placeLikelihoods
                val nearbyPlaceIds = mutableListOf<String>()

                for (likelihood in nearbyPlaces) {
                    val place = likelihood.place
                    val placeId = place.id
                    if (placeId != null) {
                        nearbyPlaceIds.add(placeId)
                    }
                }

                logPopularPlaces(nearbyPlaceIds)

                // Fetch details for the nearby places and update the UI
                updateNearbyPopularPlaces(nearbyPlaceIds)
            } catch (e: Exception) {
                Log.e("LocationFragment", "Error fetching nearby places: ${e.message}", e)
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

    private fun searchPlacesByName(locationName: String) {
        val apiKey = getString(R.string.google_maps_key) // Replace with your actual API key
        val language = "en"
        val fields = "name,formatted_address,photos,types,opening_hours,rating" // Specify the fields you want

        // Encode the locationName for the query
        val encodedQuery = URLEncoder.encode(locationName, "UTF-8")

        // Make the API request in a Coroutine
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Call the searchPlaces function from your PlacesService
                val response = placesApiService.searchPlaces(encodedQuery, language, fields, apiKey)

                // Check if the response is successful (HTTP 200-299)
                if (response.isSuccessful) {
                    val placesResponse = response.body()

                    // Handle the response here, e.g., update your UI with the results
                    val places = placesResponse?.results

                    // Check if places is not null before updating your UI
                    if (places != null) {
                        // You can update your UI with the list of places here
                        // For example, you can update a RecyclerView with the places data
                        updateUIWithNearbyPlaces(places)
                    } else {
                        // Handle the case where places is null or empty
                    }
                } else {
                    // Handle the case where the HTTP request was not successful
                    Log.e("LocationFragment", "API request failed with code: ${response.code()}")
                }
            } catch (e: Exception) {
                // Handle errors, e.g., network error or API error
                Log.e("LocationFragment", "Error searching places: ${e.message}", e)
            }
        }
    }


    private fun logPopularPlaces(popularPlaceIds: List<String>) {
        Log.d("LocationFragment", "Nearby place IDs: ${popularPlaceIds.joinToString(", ")}")
    }

    private suspend fun updateUIWithNearbyPlaces(places: List<Place>) {
        // Create a list of MyPlace objects from the retrieved Place objects
        val nearbyPlaces = places.map { place ->
            MyPlace(
                name = place.name,
                address = place.address,
                photoMetadatas = place.photoMetadatas,
                types = place.types,
                openingHours = place.openingHours,
                rating = place.rating
            )
        }

        // Update the nearbyPlacesList on the main thread
        withContext(Dispatchers.Main) {
            nearbyPlacesList.clear()
            nearbyPlacesList.addAll(nearbyPlaces)
            popularPlacesAdapter.notifyDataSetChanged()
        }
    }


    private fun updateNearbyPopularPlaces(placeIds: List<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val places = mutableListOf<MyPlace>()

                for (placeId in placeIds) {
                    val placeFields = listOf(
                        Place.Field.ID,
                        Place.Field.NAME,
                        Place.Field.ADDRESS,
                        Place.Field.PHOTO_METADATAS,
                        Place.Field.TYPES,
                        Place.Field.OPENING_HOURS,
                        Place.Field.RATING
                    )

                    val request = FetchPlaceRequest.newInstance(placeId, placeFields)
                    val response = placesClient.fetchPlace(request).await()
                    val place = response.place

                    // Convert Google Place to your custom Place model
                    val myPlace = MyPlace(
                        name = place.name,
                        address = place.address,
                        photoMetadatas = place.photoMetadatas,
                        types = place.types,
                        openingHours = place.openingHours,
                        rating = place.rating
                    )

                    places.add(myPlace)
                }

                // Update the nearbyPlacesList on the main thread
                withContext(Dispatchers.Main) {
                    nearbyPlacesList.clear()
                    nearbyPlacesList.addAll(places)
                    popularPlacesAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.e("LocationFragment", "Error fetching place details: ${e.message}", e)
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
data class MyPlace(
    val name: String?,
    val address: String?,
    val photoMetadatas: List<PhotoMetadata>?,
    val types: List<Place.Type>?,
    val openingHours: OpeningHours?,
    val rating: Double?
    // Add other fields as needed
)

