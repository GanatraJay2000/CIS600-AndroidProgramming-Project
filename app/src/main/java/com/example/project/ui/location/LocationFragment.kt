package com.example.project.ui.location

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.project.databinding.FragmentLocationBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.OpeningHours
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient

class LocationFragment : Fragment() {
    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!

    private lateinit var place: Place
    private lateinit var placeDetails: PlaceDetails
    private lateinit var placesClient: PlacesClient


    companion object {
        private const val ARG_PLACE = "place"

        fun newInstance(place: Place): LocationFragment {
            val fragment = LocationFragment()
            val args = Bundle().apply {
                putParcelable(ARG_PLACE, place)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            place = it.getParcelable(ARG_PLACE) ?: throw IllegalStateException("Place data is required.")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Places.initialize(requireContext(), "AIzaSyBvQIUByA2GmXPnNMZ51hNtVHDhBLMAvoI")

        // Create the PlacesClient
        placesClient = Places.createClient(requireContext())


        // Fetch additional place details
        place.id?.let { fetchPlaceDetails(it) }

        // Populate the UI with basic place details
        binding.placeNameTextView.text = place.name
        binding.placeAddressTextView.text = place.address
        binding.placeDescriptionTextView.text = place.editorialSummary
        binding.placeAttractionsTextView.text = place.types?.joinToString(", ") { it.name } ?: ""
        binding.ratingBar.rating = place.rating?.toFloat() ?: 0f
        binding.openingHoursTextView.text = place.openingHours?.weekdayText?.joinToString("\n") ?: ""

        Log.d("LocationFragment", "Place Name: ${place.name}")
        Log.d("LocationFragment", "Place Address: ${place.address}")
        Log.d("LocationFragment", "Place Description: ${place.editorialSummary}")
        Log.d("LocationFragment", "Place Attractions: ${place.types?.joinToString(", ") { it.name } ?: ""}")
        Log.d("LocationFragment", "Place Rating: ${place.rating?.toFloat() ?: 0f}")
        Log.d("LocationFragment", "Opening Hours: ${place.openingHours?.weekdayText?.joinToString("\n") ?: ""}")

// Now set the values in your UI components
        binding.placeNameTextView.text = place.name
        binding.placeAddressTextView.text = place.address
        binding.placeDescriptionTextView.text = place.editorialSummary
        binding.placeAttractionsTextView.text = place.types?.joinToString(", ") { it.name } ?: ""
        binding.ratingBar.rating = place.rating?.toFloat() ?: 0f
        binding.openingHoursTextView.text = place.openingHours?.weekdayText?.joinToString("\n") ?: ""

        // Here, you can add more UI updates for other basic place details

        // Example: If you have an ImageView for the place's image, you can load the image here
        // Example: If you have a TextView for a description, you can set its text here
    }

    private fun fetchPlaceDetails(placeId: String) {
        // Define the fields you want to retrieve for place details
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
            placeDetails = PlaceDetails(
                place.name,
                place.address,
                place.photoMetadatas,
                place.types,
                place.openingHours,
                place.rating,
                place.editorialSummary,
            )

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
        if (placeDetails.photoMetadatas != null && placeDetails.photoMetadatas.isNotEmpty()) {
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

        // Add more fields as needed
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
    // Add more fields as needed
)

