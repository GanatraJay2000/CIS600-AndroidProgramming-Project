package com.example.travelease.adapters

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelease.R
import com.example.travelease.model.Place
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient

class LocationCardAdapter(private val context: Context, private val locationsList: List<Place>, private val onLocationClicked: (String) -> Unit) :
    RecyclerView.Adapter<LocationCardAdapter.ViewHolder>() {
    private lateinit var placesClient: PlacesClient
    init {
        Places.initialize(context, "AIzaSyBvQIUByA2GmXPnNMZ51hNtVHDhBLMAvoI")
        placesClient = Places.createClient(context)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.locationImageView)
        val nameView: TextView = view.findViewById(R.id.locationNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.location_card_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = locationsList[position]
        holder.nameView.text = location.title
        getPhoto(holder.imageView, location.id)

        holder.itemView.setOnClickListener {
            Log.v("TopLocationsAdapter", "Location clicked: ${location.id}")
            onLocationClicked(location.id)
        }
    }

    override fun getItemCount() = locationsList.size



    private fun setPhoto(imageView: ImageView, photoMetadata: PhotoMetadata) {
        val photoRequest = FetchPhotoRequest.builder(photoMetadata).setMaxWidth(500).setMaxHeight(300).build()
        placesClient.fetchPhoto(photoRequest)
            .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                val bitmap = fetchPhotoResponse.bitmap
                imageView.setImageBitmap(bitmap)
            }
    }
    private fun getPhoto(imageView: ImageView, placeId: String){
        val fields = listOf(com.google.android.libraries.places.api.model.Place.Field.PHOTO_METADATAS)
        val placeRequest = FetchPlaceRequest.newInstance(placeId, fields)

        placesClient.fetchPlace(placeRequest)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place
                val metada = place.photoMetadatas
                if (metada == null || metada.isEmpty()) return@addOnSuccessListener
                val photoMetadata = metada.first()
                setPhoto(imageView, photoMetadata)
            }
    }
}

