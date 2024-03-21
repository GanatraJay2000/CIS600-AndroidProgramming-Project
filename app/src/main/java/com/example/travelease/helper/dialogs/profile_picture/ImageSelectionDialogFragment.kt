package com.example.travelease.helper.dialogs.profile_picture

import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.travelease.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.Manifest
import android.util.Log
import com.example.travelease.ui.profile.ProfileFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage

class ImageSelectionDialogFragment : BottomSheetDialogFragment() {
    private var photoUri: Uri? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image_selection_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.buttonChooseFromGallery).setOnClickListener {
            requestPermissionAndPickImage()
        }
        view.findViewById<Button>(R.id.buttonTakePhoto).setOnClickListener {
            requestPermissionAndTakePhoto()
        }
    }


    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { sendBackResult(it) }
    }
    private fun requestPermissionAndPickImage() {
        pickImageLauncher.launch("image/*")
    }



    private fun sendBackResult(imageUri: Uri) {
        handleImageResult(imageUri)
        Log.v("ImageSelectionDialog", "Image URI: $imageUri")
        dismiss()
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        Log.v("ProfileFragment", "Uploading image to Firebase Storage")
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storageRef = FirebaseStorage.getInstance().getReference("profile_images/$userId.jpg")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                // Image upload successful
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    updateUserProfile(downloadUri)
                }
            }
            .addOnFailureListener {
                // Handle unsuccessful uploads
            }
    }

    fun handleImageResult(uri: Uri) {
        uploadImageToFirebaseStorage(uri)
        //update profile image on profile fragment
        val profileFragment = parentFragment as ProfileFragment
        profileFragment.updateProfileImage(uri)
    }

    private fun updateUserProfile(imageUri: Uri) {
        val user = FirebaseAuth.getInstance().currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setPhotoUri(imageUri)
            .build()

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Profile updated successfully
                } else {
                    // Handle the failure toast
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            photoUri = createImageUri()
            takePhotoLauncher.launch(photoUri)
        } else {
            // Toast that user denied the permission
            Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()


        }
    }
    private fun createImageUri(): Uri? {
        val contentResolver = requireContext().contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "temp_image_${System.currentTimeMillis()}")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            // Add more contentValues as needed
        }
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }


    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            photoUri?.let { sendBackResult(it) }
        }
    }

    private fun requestPermissionAndTakePhoto() {
        // Check and request camera permissions
        val hasCameraPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasCameraPermission) {
            photoUri = createImageUri()
            takePhotoLauncher.launch(photoUri)

        } else {
            // Request camera permission
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

}
