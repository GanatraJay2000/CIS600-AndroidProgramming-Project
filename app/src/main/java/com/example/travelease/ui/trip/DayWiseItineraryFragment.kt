package com.example.travelease.ui.trip

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelease.R
import com.example.travelease.adapters.PlacesAdapter
import com.example.travelease.databinding.FragmentDayWiseItineraryBinding
import com.example.travelease.helper.dialogs.addPlace.AddPlaceToDayFragment
import com.example.travelease.helper.dialogs.search.SearchBottomSheetFragment
import com.example.travelease.helper.dialogs.search.SearchBottomSheetViewModel
import com.example.travelease.model.ItineraryDay

class DayWiseItineraryFragment : Fragment() {

    private var itineraryDay: ItineraryDay? = null
    private lateinit var searchBottomSheetModel: SearchBottomSheetViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchBottomSheetModel = ViewModelProvider(this).get(SearchBottomSheetViewModel::class.java)

        arguments?.let {
            itineraryDay = it.getSerializable("itineraryDay") as ItineraryDay?
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentDayWiseItineraryBinding.inflate(inflater, container, false)

        Log.v("DayWiseItinerary", "onCreateView: $itineraryDay")

        binding.placesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = PlacesAdapter(requireContext(), itineraryDay?.places ?: emptyList())
        }
        return binding.root
    }
    companion object {
        fun newInstance(itineraryDay: ItineraryDay): DayWiseItineraryFragment {
            val fragment = DayWiseItineraryFragment()
            val args = Bundle()
            args.putSerializable("itineraryDay", itineraryDay)
            fragment.arguments = args
            return fragment
        }
    }
}
