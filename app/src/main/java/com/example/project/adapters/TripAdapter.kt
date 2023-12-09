package com.example.project.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.project.R
import com.example.project.databinding.PlaceItemBinding
import com.example.project.models.Trip
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient

class TripAdapter(private val context: Context, private val trips: List<Trip>, private val onTripClicked: (Int) -> Unit) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {
    private lateinit var placesClient: PlacesClient

class TripAdapter(
    private val trips: MutableList<Trip?>,
    private val onTripClicked: (Int) -> Unit,
    private val onTripDelete: (Int) -> Unit
) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {
    init {
        Places.initialize(context, "AIzaSyBvQIUByA2GmXPnNMZ51hNtVHDhBLMAvoI")
        placesClient = Places.createClient(context)
    }

    class TripViewHolder(
        view: View,
        private val onTripDelete: (Int) -> Unit
    ) : RecyclerView.ViewHolder(view) {

    inner class TripViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.imageViewTrip)
        private val titleView: TextView = view.findViewById(R.id.textViewTripTitle)
        private val deleteButton: Button = view.findViewById(R.id.buttonDeleteTrip)

        fun bind(trip: Trip, position: Int) {
            titleView.text = trip.title

            getPhoto(imageView, trip.placeId.toString())
            imageView.load(trip.imageUrl) {
                placeholder(R.drawable.image_placeholder)
                error(R.drawable.profile_placeholder)
            }

            deleteButton.setOnClickListener {
                onTripDelete(trip.id) // Assuming trip.id is the unique identifier for a trip
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trip_item, parent, false)
        return TripViewHolder(view, onTripDelete)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        trips[position]?.let { trip ->
            holder.bind(trip, position)
        }

        holder.itemView.setOnClickListener {
            trips[position]?.id?.let { tripId ->
                onTripClicked(tripId)
            }
        }
    }

    override fun getItemCount() : Int {
        return trips.size
    }



    fun getPhoto(imageView: ImageView, placeId: String){

        val fields = listOf(Place.Field.PHOTO_METADATAS)

// Get a Place object (this example uses fetchPlace(), but you can also use findCurrentPlace())
        val placeRequest = FetchPlaceRequest.newInstance(placeId, fields)

        placesClient.fetchPlace(placeRequest)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place

                // Get the photo metadata.
                val metada = place.photoMetadatas
                if (metada == null || metada.isEmpty()) {

                    return@addOnSuccessListener
                }
                val photoMetadata = metada.first()

                // Get the attribution text.
                val attributions = photoMetadata?.attributions

                // Create a FetchPhotoRequest.
                val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(500) // Optional.
                    .setMaxHeight(300) // Optional.
                    .build()
                placesClient.fetchPhoto(photoRequest)
                    .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                        val bitmap = fetchPhotoResponse.bitmap
                        imageView.setImageBitmap(bitmap)
                    }.addOnFailureListener { exception: Exception ->
                        if (exception is ApiException) {

                            val statusCode = exception.statusCode
                            // Handle error with given status code.
                        }
                    }
            }

    }


    @SuppressLint("NotifyDataSetChanged")
    fun updateTrips(newTrips: List<Trip?>) {
        trips.clear()
        trips.addAll(newTrips)
        notifyDataSetChanged()
    }
}
