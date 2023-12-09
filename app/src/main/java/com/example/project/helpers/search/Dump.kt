package com.example.project.helpers.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R
import com.example.project.adapters.SearchResultsAdapter
import com.example.project.ui.location.LocationFragment
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.okhttp.Callback
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder

class Dump : BottomSheetDialogFragment() {

    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var searchResultsRecyclerView: RecyclerView
    private lateinit var searchResultsAdapter: SearchResultsAdapter
    private lateinit var placesClient: PlacesClient
    private val okHttpClient = OkHttpClient()

    private val searchResultsList = mutableListOf<AutocompletePrediction>()

    companion object {
        private const val TAG = "SearchBottomSheetFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search_bottom_sheet, container, false).apply {
            autoCompleteTextView = findViewById(R.id.autoCompleteTextView)
            searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView)

            Places.initialize(requireContext(), getString(R.string.google_maps_key))
            placesClient = Places.createClient(requireContext())

            searchResultsAdapter = SearchResultsAdapter(searchResultsList) { prediction ->
                onSearchResultClick(prediction)
            }

            autoCompleteTextView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                    searchResultsRecyclerView.visibility = View.VISIBLE
//                    this@apply.findViewById<View>(R.id.frag_maps)?.visibility = View.GONE
//                    findViewById<View>(R.id.frag_maps)?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!s.isNullOrEmpty()) {
                        performSearch(s.toString())
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            searchResultsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            searchResultsRecyclerView.adapter = searchResultsAdapter

            findViewById<ImageView>(R.id.closeButton).setOnClickListener {
                dismiss()
            }
        }
    }

    private fun onSearchResultClick(prediction: AutocompletePrediction) {
        // Close the keyboard
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
        // Rest of your code for handling the search result click
//        this.view?.findViewById<FrameLayout>(R.id.frag_maps)?.visibility = View.VISIBLE
//        searchResultsRecyclerView.visibility = View.GONE
//        view?.findViewById<FrameLayout>(R.id.frag_maps)?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT

        val placeId = prediction.placeId
        val placeFields = listOf(
            Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,
            Place.Field.PHOTO_METADATAS, Place.Field.TYPES,
            Place.Field.OPENING_HOURS, Place.Field.RATING, Place.Field.LAT_LNG // Include LAT_LNG field
        )

        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
        placesClient.fetchPlace(request).addOnSuccessListener { response: FetchPlaceResponse ->
            val place = response.place
            Log.v("SearchBottomSheetFragment", "place: ${place.name}")

            // Extracting place types as popular places for demonstration
            val popularPlaces = place.types
                ?.map { it.name } ?: listOf()

            val latLng = place.latLng // Get the LatLng
            val locationName = place.name // Get the name of the location

            // Pass the place, popular places, latLng, and locationName to LocationFragment
//            navigateToPlaceDetailsFragment(place, popularPlaces, latLng, locationName)
        }.addOnFailureListener { exception: Exception ->
            if (exception is ApiException) {
                Log.e(TAG, "Place not found: " + exception.message + ", statusCode: " + exception.statusCode)
            }
        }
    }

//
//    private fun navigateToPlaceDetailsFragment(place: Place, popularPlaces: List<String>, latLng: LatLng?, locationName: String?) {
//        val fragment = LocationFragment.newInstance(place, popularPlaces, latLng?.latitude, latLng?.longitude, locationName)
//        childFragmentManager.beginTransaction()
//            .replace(R.id.frag_maps, fragment)
//            .addToBackStack(null)
//            .commit()
//    }




    // Implement the performSearch function to fetch search results based on the query
    private fun performSearch(query: String) {

        fetchCoordinates(query) { latLng ->
            performNearbySearch(latLng)
        }
    }

    private fun fetchCoordinates(query: String, callback: (String) -> Unit) {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val url = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=$encodedQuery&inputtype=textquery&fields=geometry/location&key=AIzaSyBvQIUByA2GmXPnNMZ51hNtVHDhBLMAvoI"

        val request = Request.Builder().url(url).build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onResponse(response: Response?) {
                if (response != null) {
                    if (response.isSuccessful) {
                        response.body()?.string()?.let { responseBody ->
                            val jsonObject = JSONObject(responseBody)
                            val candidates = jsonObject.getJSONArray("candidates")
                            if (candidates.length() > 0) {
                                val location = candidates.getJSONObject(0).getJSONObject("geometry").getJSONObject("location")
                                val lat = location.getDouble("lat")
                                val lng = location.getDouble("lng")
                                val latLng = "$lat,$lng"
                                callback(latLng)

                                // Log success
                                Log.d(TAG, "Coordinates fetched successfully: $latLng")
                            } else {
                                // Log that no candidates were found
                                Log.w(TAG, "No candidates found in the response")
                            }
                        }
                    } else {
                        // Log failure with response code
                        Log.e(TAG, "Fetch coordinates request failed with response code: ${response.code()}")
                    }
                } else {
                    // Log failure with no response
                    Log.e(TAG, "No response received")
                }
            }

            override fun onFailure(request: Request?, e: IOException?) {
                // Log failure
                Log.e(TAG, "Fetch coordinates request failed", e)
            }
        })
    }

    private fun performNearbySearch(latLng: String) {
        val encodedLatLng = URLEncoder.encode(latLng, "UTF-8")
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$encodedLatLng&radius=5000&key=AIzaSyBvQIUByA2GmXPnNMZ51hNtVHDhBLMAvoI"

        val request = Request.Builder().url(url).build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onResponse(response: Response?) {
                if (response != null) {
                    if (response.isSuccessful) {
                        response.body()?.string()?.let { responseBody ->
                            val places = parsePlaces(responseBody) // You need to implement parsePlaces to parse the JSON response
                            activity?.runOnUiThread {
                                updateSearchResults(places)
                            }

                            // Log success
                            Log.d(TAG, "Nearby search successful")
                        }
                    } else {
                        // Log failure with response code
                        Log.e(TAG, "Nearby search request failed with response code: ${response.code()}")
                    }
                } else {
                    // Log failure with no response
                    Log.e(TAG, "No response received")
                }
            }

            override fun onFailure(request: Request?, e: IOException?) {
                // Log failure
                Log.e(TAG, "Nearby search request failed", e)
            }
        })
    }


    private fun parsePlaces(jsonResponse: String): List<AutocompletePrediction> {
        val jsonObject = JSONObject(jsonResponse)
        val results = jsonObject.getJSONArray("results")
        val predictions = mutableListOf<AutocompletePrediction>()

        for (i in 0 until results.length()) {
            val result = results.getJSONObject(i)
            val placeId = result.getString("place_id")
            val name = result.getString("name")

            val prediction = AutocompletePrediction.builder(placeId)
                .setFullText(name)
                .build()

            predictions.add(prediction)
        }

        return predictions
    }

    // Update searchResultsList with the fetched search results
    private fun updateSearchResults(results: List<AutocompletePrediction>) {
        Log.d(TAG, "Updating search results: ${results.size} items found.")
        Log.d("main results", "search results: $results items found.")
        searchResultsList.clear()
        searchResultsList.addAll(results)
        searchResultsAdapter.run {
            notifyDataSetChanged()
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as BottomSheetDialog?
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as ViewGroup?
        val bottomSheetBehavior = bottomSheet?.let { BottomSheetBehavior.from(it) }
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBehavior.peekHeight = 0 // Optional: Remove the 'peek' height
        }

        // Optionally, set the height to full screen
        bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
    }

}
