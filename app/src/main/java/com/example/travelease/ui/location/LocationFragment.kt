package com.example.travelease.ui.location

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelease.R
import com.example.travelease.SharedViewModel
import com.example.travelease.adapters.NearByPlacesAdapter
import com.example.travelease.databinding.FragmentLocationBinding
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
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.google.android.libraries.places.api.net.SearchByTextResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


class LocationFragment : Fragment(), OnMapReadyCallback {

    companion object {
        fun newInstance() = LocationFragment()
    }

    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!

    private lateinit var placesClient: PlacesClient

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var viewModel: LocationViewModel

    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null

    private lateinit var placesAdapter: NearByPlacesAdapter

    val retrofit = Retrofit.Builder().baseUrl("https://maps.googleapis.com/").addConverterFactory(GsonConverterFactory.create()).build()
    val googlePlacesApi = retrofit.create(GooglePlacesApiService::class.java)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        Places.initialize(requireContext(), getString(R.string.google_maps_key))
        placesClient = Places.createClient(requireContext())

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        viewModel = ViewModelProvider(requireActivity())[LocationViewModel::class.java]

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //observe locationId
        sharedViewModel.locationId.observe(viewLifecycleOwner) { locationId ->
            getPlace(locationId)
        }

        // OBSERVE LOCATION
        viewModel.location.observe(viewLifecycleOwner) { location ->
            binding.locationTitle.text = location.title
            if(location.photoMetadata != null) setPhoto(binding.locationImage, location.photoMetadata)

            fetchPointsOfInterest(location.title!!) // API CALL USING RETROFIT
//            getPointsOfInterest(location.title!!) // SDK



            placesAdapter = NearByPlacesAdapter(requireContext(), mutableListOf()) // Initialize with an empty list
            val placesList = binding.popularAttractions
            placesList.layoutManager = LinearLayoutManager(requireContext())
            placesList.adapter = placesAdapter

            //observe places
            viewModel.places.observe(viewLifecycleOwner) { places ->
                placesAdapter.updateData(places)
                places.forEach {place ->
                    Log.v("LocationFragment", "place: $place")
                    val latLng = place.latlng
                    latLng?.let {
                        Log.v("LocationFragment", "latLng: $it")
                        googleMap?.addMarker(MarkerOptions().position(it).title(place.title))
                    }
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Enable zoom controls
        googleMap?.uiSettings?.isZoomControlsEnabled = true

        // observe locationId
        sharedViewModel.locationId.observe(viewLifecycleOwner) { locationId ->

        val placeId = locationId.toString()
        val placeFields = listOf(Place.Field.LAT_LNG)

        val request = placeId?.let { FetchPlaceRequest.newInstance(it, placeFields) }

        if (request != null) {
            placesClient.fetchPlace(request).addOnSuccessListener { response ->
                val place = response.place
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, 12f))
                place.latLng?.let {
                    googleMap?.addMarker(MarkerOptions().position(it).title(place.name))
                }
            }.addOnFailureListener { exception ->
                // Handle exception
            }
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
    fun getPlace(placeId: String) {
        val placeFields = listOf(
            Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,
            Place.Field.PHOTO_METADATAS
        )

        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
        placesClient.fetchPlace(request).addOnSuccessListener { response: FetchPlaceResponse ->
            val place = response.place
            val placeId = place.id
            val placeName = place.name
            val placeAddress = place.address
            val photoMetadata = place.photoMetadatas

            viewModel.setLocation(com.example.travelease.model.Place(placeId, null, placeName, placeAddress, photoMetadata))
        }
    }

    fun getPointsOfInterest(query: String){
        val placeFields = listOf(
            Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,
            Place.Field.PHOTO_METADATAS, Place.Field.LAT_LNG,
        )
        val searchByTextRequest = SearchByTextRequest.builder("$query point of interest", placeFields).setMaxResultCount(10).build()
        placesClient.searchByText(searchByTextRequest)
            .addOnSuccessListener { response: SearchByTextResponse ->
                val places = response.places
                val placesList = mutableListOf<com.example.travelease.model.Place>()
                if (places != null) {
                    for (result in places) {
                        placesList.add(com.example.travelease.model.Place(result.id, null, result.name, result.address, result.photoMetadatas, result.latLng))
                    }
                }
                viewModel.setPlaces(placesList)
            }.addOnFailureListener() { exception ->
                Log.e("LocationFragment", "API error: ${exception}")
            }
    }
    fun fetchPointsOfInterest(query: String) {
        val call = googlePlacesApi.getPointsOfInterest("$query point of interest", "en", getString(R.string.google_maps_key))
        call.enqueue(object : Callback<PointsOfInterestResponse> {
            override fun onResponse(
                call: Call<PointsOfInterestResponse>,
                response: Response<PointsOfInterestResponse>
            ) {
                if (response.isSuccessful) {
                    val pointsOfInterest = response.body()?.results?.take(10)
                    val placesList = mutableListOf<com.example.travelease.model.Place>()
                    if (pointsOfInterest != null) {
                        for (result in pointsOfInterest) {
                            val name = result.name
                            val formattedAddress = result.formatted_address
                            val placeId = result.place_id
                            val photos = result.photos as? List<*>
                            val latLng = result.geometry.location.let { LatLng(it.lat, result.geometry.location.lng) }
                            val place = com.example.travelease.model.Place(placeId, null, name, formattedAddress, photos as MutableList<PhotoMetadata>?, latLng)
                            placesList.add(place)
                        }
                    }
                   viewModel.setPlaces(placesList)
                }else{
                    Log.e("LocationFragment", "API error: ${response.errorBody()}")
                }
            }
            override fun onFailure(call: Call<PointsOfInterestResponse>, t: Throwable) {}
        })
    }
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
    data class GeoLocation(
        val lat: Double,
        val lng: Double
    )
    data class Viewport(
        val northeast: GeoLocation,
        val southwest: GeoLocation
        // Add other fields as needed
    )
    data class PhotoPlace (
        val height: Int,
        val html_attributions: List<String>,
        val photo_reference: String,
        val width: Int
    )
}