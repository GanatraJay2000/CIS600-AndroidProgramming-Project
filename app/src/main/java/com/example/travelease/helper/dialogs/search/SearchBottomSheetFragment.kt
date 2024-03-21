package com.example.travelease.helper.dialogs.search

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
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelease.R
import com.example.travelease.SharedViewModel
import com.example.travelease.adapters.SearchResultsAdapter
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.okhttp.OkHttpClient

class SearchBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var searchResultsRecyclerView: RecyclerView
    private lateinit var searchResultsAdapter: SearchResultsAdapter
    private lateinit var placesClient: PlacesClient
    private val okHttpClient = OkHttpClient()
    private lateinit var viewModel: SearchBottomSheetViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private val searchResultsList = mutableListOf<AutocompletePrediction>()

    companion object {
        private const val TAG = "SearchBottomSheetFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search_bottom_sheet, container, false).apply {

            viewModel = ViewModelProvider(requireActivity()).get(SearchBottomSheetViewModel::class.java)
            sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

            Places.initialize(requireContext(), getString(R.string.google_maps_key))
            placesClient = Places.createClient(requireContext())


            autoCompleteTextView = findViewById(R.id.autoCompleteTextView)
            searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView)
            searchResultsAdapter = SearchResultsAdapter(searchResultsList) { prediction ->
                onSearchResultClick(prediction)
            }
            searchResultsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            searchResultsRecyclerView.adapter = searchResultsAdapter



            autoCompleteTextView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable?) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!s.isNullOrEmpty()) {
                        fetchAutocompleteSuggestions(s.toString())
                    }
                }
            })


            findViewById<ImageView>(R.id.closeButton).setOnClickListener {
                dismiss()
            }



        }
    }

    private fun fetchAutocompleteSuggestions(query: String) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
            searchResultsList.clear()
            searchResultsList.addAll(response.autocompletePredictions)
            searchResultsAdapter.notifyDataSetChanged()
        }.addOnFailureListener { exception ->
            if (exception is ApiException) {
                Log.e(TAG, "Autocomplete prediction API error: ${exception.message}")
            }
        }
    }

    private fun onSearchResultClick(prediction: AutocompletePrediction) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)

        sharedViewModel.setLocationId(prediction.placeId)
        sharedViewModel.setLocationTitle(prediction.getFullText(null).toString())
        dismiss()
        Log.v("AddTripFragment", "navigate to Location fragment")
        if(viewModel.navigateToLocation.value == true)
            navigateToDestination(R.id.nav_location)
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
}
