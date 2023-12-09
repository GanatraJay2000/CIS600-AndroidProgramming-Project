package com.example.project.ui.location

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R
import com.example.project.adapters.NearByPlacesAdapter
import com.example.project.databinding.FragmentLocationBinding
import com.example.project.helpers.search.SearchBottomSheetViewModel
import com.example.project.models.GeoLocation
import com.example.project.models.NearByPlace
import com.example.project.models.Viewport
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlacesApiService {
    @GET("maps/api/place/textsearch/json")
    fun getPointsOfInterest(
        @Query("query") query: String,
        @Query("language") language: String,
        @Query("key") apiKey: String
    ): Call<PointsOfInterestResponse> // Define a data class for the response
}

data class PointsOfInterestResponse(
    val results: List<POI>
)

data class POI(
    val name: String,
    val formatted_address: String,
    val place_id: String,
    val photos: List<PhotoPlace>,
    val geometry: PlaceGeometry
)

data class PlaceGeometry (
    val location: GeoLocation,
    val viewport: Viewport
)

data class PhotoPlace (
    val height: Int,
    val html_attributions: List<String>,
    val photo_reference: String,
    val width: Int
)


class LocationFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!
    companion object {
        fun newInstance() = LocationFragment()
    }

    private lateinit var viewModel: LocationViewModel
    private lateinit var place: com.example.project.models.Place
    private lateinit var placesAdapter: NearByPlacesAdapter
    private lateinit var placesList: RecyclerView
    private lateinit var searchViewModel: SearchBottomSheetViewModel
    private lateinit var placesClient: PlacesClient
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null

    val retrofit = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val googlePlacesApi = retrofit.create(GooglePlacesApiService::class.java)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        val root: View = binding.root


        Places.initialize(requireContext(), getString(R.string.google_maps_key))
        placesClient = Places.createClient(requireContext())


        searchViewModel = ViewModelProvider(requireActivity()).get(SearchBottomSheetViewModel::class.java)

        // Initialize MapView
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val place =  searchViewModel.place.value

        val locationId = arguments?.getInt("locationId") ?: 1
        viewModel = ViewModelProvider(this).get(LocationViewModel::class.java)
        viewModel.setLocationId(locationId)


        if (place != null) {
            binding.locationTitle.text = place.title

            if (!place.photoMetadata.isNullOrEmpty()) {
                val photoMetadata = place.photoMetadata[0]
            val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(500)
                .setMaxHeight(300)
                .build()

            placesClient.fetchPhoto(photoRequest).addOnSuccessListener { fetchPhotoResponse ->
                val bitmap = fetchPhotoResponse.bitmap
                binding.locationImage.setImageBitmap(bitmap)
            }.addOnFailureListener { exception ->
                Log.e("LocationFragment", "Error loading photo: ${exception.message}")
            }
        } else {
            binding.locationImage.setImageResource(R.drawable.image_placeholder)
        }
        };


        fetchPointsOfInterest("${place?.title} point of interest")



        placesAdapter = NearByPlacesAdapter(requireContext(), mutableListOf()) // Initialize with an empty list
        placesList = view.findViewById(R.id.popularAttractions)
        placesList.layoutManager = LinearLayoutManager(requireContext())
        placesList.adapter = placesAdapter

    }


    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        // Example: Move the camera to the location from a Place ID
        val placeId = searchViewModel.place.value?.id
        val placeFields = listOf(Place.Field.LAT_LNG)

        val request = placeId?.let { FetchPlaceRequest.newInstance(it, placeFields) }

        if (request != null) {
            placesClient.fetchPlace(request).addOnSuccessListener { response ->
                val place = response.place
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, 13f))

                // Optionally, add a marker at the place's location
                place.latLng?.let {
                    googleMap?.addMarker(MarkerOptions().position(it).title(place.name))
                }
            }.addOnFailureListener { exception ->
                // Handle exception
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    fun fetchPointsOfInterest(query: String) {
        val call = googlePlacesApi.getPointsOfInterest(query, "en", getString(R.string.google_maps_key))
        call.enqueue(object : Callback<PointsOfInterestResponse> {
            override fun onResponse(
                call: Call<PointsOfInterestResponse>,
                response: Response<PointsOfInterestResponse>
            ) {
                Log.v("LocationFragment", "Response: $response")
                if (response.isSuccessful) {
                    val pointsOfInterest = response.body()?.results?.take(10)

                    Log.v("LocationFragment", "Points of interest: $pointsOfInterest")
                    val placesList = mutableListOf<NearByPlace>()
                    if (pointsOfInterest != null) {
                        for (result in pointsOfInterest) {

                                val name = result.name
                                val formattedAddress = result.formatted_address
                                val placeId = result.place_id
                                val photos = result.photos as? List<*>
                                // Add the extracted data to the list
                                val place = NearByPlace(name, formattedAddress, placeId, photos)
                                placesList.add(place)
//                            // Optionally, add a marker at the place's location
                            LatLng(result.geometry.location.lat, result.geometry.location.lng)?.let {
                                googleMap?.addMarker(MarkerOptions().position(it).title(name))
                            }
                        }
                    }
                    placesAdapter.updateData(placesList)

                }
            }

            override fun onFailure(call: Call<PointsOfInterestResponse>, t: Throwable) {
                // Handle network error
            }
        })
    }
}