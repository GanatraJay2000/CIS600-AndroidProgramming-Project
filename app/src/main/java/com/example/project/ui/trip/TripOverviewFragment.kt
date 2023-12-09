package com.example.project.ui.trip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project.adapters.TripOverviewSectionsAdapter
import com.example.project.databinding.FragmentTripOverviewBinding
import com.example.project.models.dummySections

class TripOverviewFragment : Fragment() {

    private var _binding: FragmentTripOverviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var overviewAdapter: TripOverviewSectionsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTripOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        overviewAdapter = TripOverviewSectionsAdapter(dummySections)
        binding.sectionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = overviewAdapter
        }
    }

    companion object {
        fun newInstance() = TripOverviewFragment()
    }
}