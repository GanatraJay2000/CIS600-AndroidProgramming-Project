package com.example.project.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.project.R
import com.example.project.models.Trip

class TripAdapter(private val trips: List<Trip>, private val onTripClicked: (Int) -> Unit) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    class TripViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.imageViewTrip)
        private val titleView: TextView = view.findViewById(R.id.textViewTripTitle)

        fun bind(trip: Trip) {
            titleView.text = trip.title
            imageView.load(trip.imageUrl) {
                placeholder(R.drawable.image_placeholder)
                error(R.drawable.profile_placeholder)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trip_item, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = trips[position]
        holder.bind(trip)

        holder.itemView.setOnClickListener { onTripClicked(trip.id) }
    }

    override fun getItemCount() = trips.size

}
