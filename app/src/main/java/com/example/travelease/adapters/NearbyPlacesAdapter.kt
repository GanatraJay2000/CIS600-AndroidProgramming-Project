package com.example.travelease.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.travelease.R
import com.example.travelease.databinding.PlaceItemBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient

class NearByPlacesAdapter(private val context: Context, private val places: MutableList<com.example.travelease.model.Place>) : RecyclerView.Adapter<NearByPlacesAdapter.PlaceViewHolder>() {
    private var placesClient: PlacesClient

    init {
        Places.initialize(context, "AIzaSyBvQIUByA2GmXPnNMZ51hNtVHDhBLMAvoI")
        placesClient = Places.createClient(context)
    }

    inner class PlaceViewHolder(private val binding: PlaceItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(place: com.example.travelease.model.Place) {

            binding.titleTextView.text = place.title
            binding.addressTextView.text = place.address
            getPhoto(binding.imageView, place.id)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding = PlaceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(places[position])
    }

    override fun getItemCount(): Int {
        return places.size
    }

    fun updateData(newData: MutableList<com.example.travelease.model.Place>) {
        places.clear()
        places.addAll(newData)
        notifyDataSetChanged()
    }

    fun setPhoto(imageView: ImageView, metadata: MutableList<PhotoMetadata>) {
        val photoMetadata = metadata.first()
        val photoRequest = FetchPhotoRequest.builder(photoMetadata).setMaxWidth(500).setMaxHeight(300).build()
        placesClient.fetchPhoto(photoRequest)
            .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                val bitmap = fetchPhotoResponse.bitmap
                imageView.setImageBitmap(bitmap)
            }
    }
    fun getPhoto(imageView: ImageView, placeId: String){
        val fields = listOf(com.google.android.libraries.places.api.model.Place.Field.PHOTO_METADATAS)
        val placeRequest = FetchPlaceRequest.newInstance(placeId, fields)
        placesClient.fetchPlace(placeRequest)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place
                val metadata = place.photoMetadatas
                if (metadata == null || metadata.isEmpty()) return@addOnSuccessListener
                setPhoto(imageView, metadata)
            }
    }
}