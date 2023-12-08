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
        private const val ARG_POPULAR_PLACES = "popularPlaces"

        fun newInstance(place: Place, popularPlaces: List<String>): LocationFragment {
            val fragment = LocationFragment()
            val args = Bundle().apply {
                putParcelable(ARG_PLACE, place)
                putStringArrayList(ARG_POPULAR_PLACES, ArrayList(popularPlaces))
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
        Places.initialize(requireContext(), "AIzaSyBvQIUByA2GmXPnNMZ51hNtVHDhBLMAvoI")
        placesClient = Places.createClient(requireContext())

        // Now set the values in your UI components
        updateUIWithPlaceDetails(placeDetails)
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

