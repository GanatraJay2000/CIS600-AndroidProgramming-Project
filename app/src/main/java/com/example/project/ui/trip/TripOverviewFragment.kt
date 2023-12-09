package com.example.project.ui.trip

import android.os.Bundle
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
import com.example.project.models.SectionType
import com.example.project.models.dummyChecklistItems
import com.example.project.models.dummyNotes
import com.example.project.models.dummySections


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

        checklistsAdapter = ChecklistsAdapter(dummyChecklistItems)
        binding.checklistRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = checklistsAdapter
        }


    }



    companion object {
        fun newInstance() = TripOverviewFragment()
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
    fun navigateToDestination(destinationId: Int, locationId: String) {
        val navController = findNavController()
        val currentDestination = navController.currentDestination?.id

        if (currentDestination != destinationId) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(navController.graph.startDestinationId, false)
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .build()
            val bundle = Bundle().apply { putString("SECTION_TYPE", locationId) }
            navController.navigate(destinationId, bundle, navOptions)
        }
    }
}