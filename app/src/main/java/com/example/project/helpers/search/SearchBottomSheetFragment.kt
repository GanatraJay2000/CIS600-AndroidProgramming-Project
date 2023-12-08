package com.example.project.helpers.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R
import com.example.project.adapters.SearchResultsAdapter
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SearchBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var searchResultsRecyclerView: RecyclerView
    private lateinit var searchResultsAdapter: SearchResultsAdapter
    private lateinit var placesClient: PlacesClient

    private val searchResultsList = mutableListOf<AutocompletePrediction>()



    companion object {
        fun newInstance() = SearchBottomSheetFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("Search", "onViewCreated called")

        // Initialize the Places SDK with the context and your API key
        Places.initialize(requireContext(), "AIzaSyBvQIUByA2GmXPnNMZ51hNtVHDhBLMAvoI") // Ensure to use your actual API key

        // Create the PlacesClient
        placesClient = Places.createClient(requireContext())

        autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView)
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView)

        // Initialize the search results adapter with an empty list and a click listener
        searchResultsAdapter = SearchResultsAdapter(searchResultsList) { prediction ->
            // Handle the click event here
            onSearchResultClick(prediction)
        }

        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Perform a search every time the text changes
                if (!s.isNullOrEmpty()) {
                    performSearch(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // No action needed here
            }
        })

        // Set up RecyclerView with the adapter
        searchResultsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchResultsRecyclerView.adapter = searchResultsAdapter

        autoCompleteTextView.setOnEditorActionListener { _, _, _ ->
            // Handle search action here and update search results
            val query = autoCompleteTextView.text.toString()
            performSearch(query) // Update searchResultsList with your search results
            true
        }

        view.findViewById<Button>(R.id.closeButton).setOnClickListener {
            dismiss()
        }
    }



    private fun onSearchResultClick(prediction: AutocompletePrediction) {
        val placeId = prediction.placeId
        val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

        // Fetch the details of the selected place
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
        placesClient.fetchPlace(request).addOnSuccessListener { response: FetchPlaceResponse ->
            val place = response.place
            // Now you have the place details, you can pass them to an activity or fragment to display
            // For example:
            // showPlaceDetails(place)
        }.addOnFailureListener { exception: Exception ->
            if (exception is ApiException) {
                val statusCode = exception.statusCode
                Log.e("Search", "Place not found: " + exception.message + ", statusCode: " + statusCode)
            }
        }
    }

    private fun showPlaceDetails(place: Place) {
        // Implementation depends on how you choose to display place details.
        // This could be starting a new Activity, showing a DialogFragment, etc.
    }


    // Implement the performSearch function to fetch search results based on the query
    private fun performSearch(query: String) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val predictions = task.result?.autocompletePredictions
                if (predictions != null) {
                    updateSearchResults(predictions)
                }
            } else {
                task.exception?.let {
                    Log.e("Search", "Autocomplete prediction request failed", it)
                }
            }
        }
    }

    // Update searchResultsList with the fetched search results
    private fun updateSearchResults(results: List<AutocompletePrediction>) {
        searchResultsList.clear()
        searchResultsList.addAll(results)
        searchResultsAdapter.notifyDataSetChanged()
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
