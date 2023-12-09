package com.example.project.ui.trip

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project.R
import com.example.project.adapters.ChecklistsAdapter
import com.example.project.adapters.NotesAdapter
import com.example.project.databinding.FragmentTripOverviewBinding
import com.example.project.models.dummyChecklistItems
import com.example.project.models.dummyChecklists
import com.example.project.models.dummyNotes


class TripOverviewFragment : Fragment() {

    private var _binding: FragmentTripOverviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var notesAdapter: NotesAdapter
    private lateinit var checklistsAdapter: ChecklistsAdapter

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


        notesAdapter = NotesAdapter(dummyNotes)
        binding.notesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = notesAdapter
        }

        checklistsAdapter = ChecklistsAdapter(dummyChecklists)
        binding.checklistRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = checklistsAdapter
        }


    }

    companion object {
        fun newInstance() = TripOverviewFragment()
    }
}