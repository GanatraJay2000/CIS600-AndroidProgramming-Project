package com.example.project.ui.profile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.project.R
import com.example.project.adapters.TripAdapter
import com.example.project.adapters.TripOverviewSectionsAdapter
import com.example.project.databinding.FragmentProfileBinding
import com.example.project.helpers.profile_picture_dialog.ImageSelectionDialogFragment
import com.example.project.models.dummySections
import com.example.project.models.dummyTrips
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profileImageView.setOnClickListener {
            // Show bottom dialog for image selection
            showImageSelectionDialog()
        }

        Log.v("ProfileFragment", Firebase.auth.currentUser?.photoUrl.toString())
        // Load profile image
        Log.v("ProfileFragment", Firebase.auth.currentUser?.displayName.toString())
        binding.profileImageView.load(Firebase.auth.currentUser?.photoUrl ?: R.drawable.profile_placeholder)
        binding.nameTextView.text = Firebase.auth.currentUser?.displayName ?: "Anonymous"
        // Initialize RecyclerView for trips
        binding.tripsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = TripAdapter(dummyTrips) { tripId ->
                navigateToDestination(R.id.nav_trip, tripId)
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
    fun navigateToDestination(destinationId: Int, locationId: Int) {
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
}
