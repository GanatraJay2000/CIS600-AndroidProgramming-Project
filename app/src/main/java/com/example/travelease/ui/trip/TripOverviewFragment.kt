package com.example.travelease.ui.trip

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelease.SharedViewModel
import com.example.travelease.adapters.ChecklistsAdapter
import com.example.travelease.adapters.NotesAdapter
import com.example.travelease.databinding.FragmentTripOverviewBinding
import com.example.travelease.helper.DatabaseHelper
import com.example.travelease.model.ChecklistItem
import com.example.travelease.model.Note

class TripOverviewFragment : Fragment() {

    private var _binding: FragmentTripOverviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var notesAdapter: NotesAdapter
    private lateinit var checklistsAdapter: ChecklistsAdapter
    private lateinit var sharedViewModel: SharedViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
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

        val dbHelper = DatabaseHelper(requireContext())
        val notes = dbHelper.getNotesForTrip(sharedViewModel.tripId!!)
        notesAdapter = NotesAdapter(notes.toMutableList(), dbHelper)
        binding.addNoteButton.setOnClickListener {
            val newNoteText = binding.newNoteEditText.text.toString()
            if (newNoteText.isNotEmpty()) {
                val newNote = Note(null, sharedViewModel.tripId!!, newNoteText)
                dbHelper.addNote(newNote)

                notesAdapter.notes.add(newNote)
                notesAdapter.notifyItemInserted(notesAdapter.notes.size + 1)
                binding.newNoteEditText.text.clear()
            }
        }
        binding.notesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = notesAdapter
        }

        val checkListItems = dbHelper.getAllChecklistItemsForTrip(sharedViewModel.tripId!!)
        checklistsAdapter = ChecklistsAdapter(checkListItems.toMutableList(), dbHelper)
        binding.addCheckListItemButton.setOnClickListener {
            val newChecklistTitle = binding.newChecklistEditText.text.toString()
            if (newChecklistTitle.isNotEmpty()) {
                val newChecklistItem = ChecklistItem(null, sharedViewModel.tripId!!, newChecklistTitle, false)
                dbHelper.addChecklistItem(newChecklistItem)

                checklistsAdapter.checklists.add(newChecklistItem)
                checklistsAdapter.notifyItemInserted(checklistsAdapter.checklists.size - 1)
                binding.newChecklistEditText.text.clear()
            }
        }

        binding.deleteCheckedItemsButton.setOnClickListener {
            val checkedItems = checklistsAdapter.checklists.filter { it.isCompleted }
            checkedItems.forEach { dbHelper.deleteChecklistItemWithTripIdAndTitleAs(it.tripId, it.title) }
            checklistsAdapter.checklists.removeAll(checkedItems)
            checklistsAdapter.notifyDataSetChanged()
        }

        binding.checklistRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = checklistsAdapter
        }
    }

    companion object {
        fun newInstance() = TripOverviewFragment()
    }
}