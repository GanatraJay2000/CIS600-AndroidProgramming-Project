package com.example.project.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.project.R
import com.example.project.models.Trip

class TripAdapter(
    private val trips: MutableList<Trip?>,
    private val onTripClicked: (Int) -> Unit,
    private val onTripDelete: (Int) -> Unit
) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    class TripViewHolder(
        view: View,
        private val onTripDelete: (Int) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.imageViewTrip)
        private val titleView: TextView = view.findViewById(R.id.textViewTripTitle)
        private val deleteButton: Button = view.findViewById(R.id.buttonDeleteTrip)

        fun bind(trip: Trip, position: Int) {
            titleView.text = trip.title
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

    override fun getItemCount() = trips.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateTrips(newTrips: List<Trip?>) {
        trips.clear()
        trips.addAll(newTrips)
        notifyDataSetChanged()
    }
}
