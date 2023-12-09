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

class SearchBottomSheetFragment : BottomSheetDialogFragment() {

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

            Places.initialize(requireContext(), getString(R.string.google_maps_key))
            placesClient = Places.createClient(requireContext())

            searchResultsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            searchResultsRecyclerView.adapter = searchResultsAdapter
            searchResultsAdapter = SearchResultsAdapter(searchResultsList) { prediction ->
                onSearchResultClick(prediction)
            }
            autoCompleteTextView = findViewById(R.id.autoCompleteTextView)
            searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView)



            findViewById<ImageView>(R.id.closeButton).setOnClickListener {
                dismiss()
            }



        }
    }

    private fun onSearchResultClick(prediction: AutocompletePrediction) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)

        val placeId = prediction.placeId
        val placeFields = listOf(
            Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,
            Place.Field.PHOTO_METADATAS, Place.Field.TYPES,
            Place.Field.OPENING_HOURS, Place.Field.RATING, Place.Field.LAT_LNG // Include LAT_LNG field
        )

        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
        placesClient.fetchPlace(request).addOnSuccessListener { response: FetchPlaceResponse ->
            val place = response.place


        }.addOnFailureListener { exception: Exception ->
            if (exception is ApiException) {
                Log.e(TAG, "Place not found: " + exception.message + ", statusCode: " + exception.statusCode)
            }
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
