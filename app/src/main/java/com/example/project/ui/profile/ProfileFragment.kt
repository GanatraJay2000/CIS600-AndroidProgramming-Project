package com.example.project.ui.profile

import MyDatabaseHelper
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.project.R
import com.example.project.adapters.TripAdapter
import com.example.project.databinding.FragmentProfileBinding
import com.example.project.helpers.profile_picture_dialog.ImageSelectionDialogFragment
import com.example.project.models.ChecklistItem
import com.example.project.models.Note
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var tripAdapter: TripAdapter
    private lateinit var databaseHelper: MyDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val userId = firebaseUser?.uid

        context?.let {
            databaseHelper = MyDatabaseHelper(it)
        } ?: Log.e("ProfileFragment", "Context is null")

        viewModel = ViewModelProvider(this, ProfileViewModelFactory(databaseHelper))[ProfileViewModel::class.java]

        userId?.let { uid ->
            viewModel.getTripsByUserId(uid.toInt())

            viewModel.tripsLiveData.observe(viewLifecycleOwner) { trips ->
                tripAdapter.updateTrips(trips)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profileImageView.setOnClickListener {
            showImageSelectionDialog()
        }

        tripAdapter = TripAdapter(mutableListOf(),
            { tripId ->  // Handle trip click
                navigateToDestination(R.id.nav_trip, tripId)
            },
            { tripId ->  // Handle trip delete
                deleteTripById(tripId)
            }
        )

        binding.tripsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tripAdapter
        }

        Log.v("ProfileFragment", Firebase.auth.currentUser?.photoUrl.toString())
        binding.profileImageView.load(Firebase.auth.currentUser?.photoUrl ?: R.drawable.profile_placeholder)
        binding.nameTextView.text = Firebase.auth.currentUser?.displayName ?: "Anonymous"
    }

    private fun deleteTripById(tripId: Int) {

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val userId = firebaseUser?.uid

        val deletedRows = databaseHelper.deleteTripById(tripId)
        if (deletedRows > 0) {
            // Trip deleted successfully
            if (userId != null) {
                viewModel.getTripsByUserId(userId.toInt())
            } // Assuming you have a method to refresh the trips list
        } else {
            // Failed to delete the trip
            // Handle failure (e.g., show an error message)
        }
    }

    fun updateProfileImage(imageUri: Uri) {
        binding.profileImageView.load(imageUri)
    }

    private fun showImageSelectionDialog() {
        val dialog = ImageSelectionDialogFragment()
        dialog.show(childFragmentManager, dialog.tag)
    }

    private fun navigateToDestination(destinationId: Int, locationId: Int) {
        val navController = findNavController()
        val currentDestination = navController.currentDestination?.id

        if (currentDestination != destinationId) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(navController.graph.startDestinationId, false)
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .build()
            val bundle = Bundle().apply { putInt("tripId", locationId) }
            navController.navigate(destinationId, bundle, navOptions)
        }
    }

    // Update a Note
    fun updateNote(note: Note) {
        val updatedRows = databaseHelper.updateNote(note)
        if (updatedRows > 0) {
            // Note updated successfully, you can perform any necessary UI updates
        } else {
            // Failed to update the note
        }
    }

    // Delete a Note by ID
    fun deleteNoteById(noteId: Int) {
        val deletedRows = databaseHelper.deleteNoteById(noteId)
        if (deletedRows > 0) {
            // Note deleted successfully, you can perform any necessary UI updates
        } else {
            // Failed to delete the note
        }
    }

    // Update a Checklist Item
    fun updateChecklistItem(checklistItem: ChecklistItem) {
        val updatedRows = databaseHelper.updateChecklistItem(checklistItem)
        if (updatedRows > 0) {
            // Checklist item updated successfully, you can perform any necessary UI updates
        } else {
            // Failed to update the checklist item
        }
    }

    // Delete a Checklist Item by ID
    fun deleteChecklistItemById(checklistItemId: Int) {
        val deletedRows = databaseHelper.deleteChecklistItemById(checklistItemId)
        if (deletedRows > 0) {
            // Checklist item deleted successfully, you can perform any necessary UI updates
        } else {
            // Failed to delete the checklist item
        }
    }

    // Implement similar methods for other entities (Places, Sections, Trips, etc.)

}
