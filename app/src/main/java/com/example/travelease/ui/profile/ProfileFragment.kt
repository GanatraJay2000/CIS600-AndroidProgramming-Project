package com.example.travelease.ui.profile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.travelease.R
import com.example.travelease.SharedViewModel
import com.example.travelease.adapters.TripAdapter
import com.example.travelease.databinding.FragmentProfileBinding
import com.example.travelease.helper.DatabaseHelper
import com.example.travelease.helper.dialogs.profile_picture.ImageSelectionDialogFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profileImageView.setOnClickListener {
            // Show bottom dialog for image selection
            showImageSelectionDialog()
        }

        binding.profileImageView.load(Firebase.auth.currentUser?.photoUrl ?: R.drawable.profile_placeholder)
        binding.nameTextView.text = Firebase.auth.currentUser?.displayName ?: "Anonymous"
        // Initialize RecyclerView for trips
        val dbHelper = DatabaseHelper(requireContext())
        val trips = dbHelper.getTripsForUser(Firebase.auth.currentUser?.uid!!)
        binding.tripsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = TripAdapter(requireContext(), trips) { tripId ->
                Log.v("ProfileFragment", "Trip ID: $tripId")
                sharedViewModel.setTripId(tripId)
                navigateToDestination(R.id.nav_trip)
            }
        }
    }
    fun updateProfileImage(imageUri: Uri) {
        binding.profileImageView.load(imageUri)
    }

    private fun showImageSelectionDialog() {
        val dialog = ImageSelectionDialogFragment()
        dialog.show(childFragmentManager, dialog.tag)
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
