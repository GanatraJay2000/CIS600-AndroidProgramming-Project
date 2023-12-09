package com.example.project.ui.trip

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project.R
import com.example.project.adapters.TripOverviewSectionsAdapter
import com.example.project.databinding.FragmentDayWiseItineraryBinding
import com.example.project.models.ItineraryDay

class DayWiseItineraryFragment : Fragment() {

    private var itineraryDay: ItineraryDay? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itineraryDay = it.getSerializable("itineraryDay") as ItineraryDay?
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentDayWiseItineraryBinding.inflate(inflater, container, false)

        binding.sectionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = TripOverviewSectionsAdapter(itineraryDay?.sections ?: emptyList())
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
