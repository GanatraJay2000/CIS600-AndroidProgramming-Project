package com.example.project.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.project.R
import com.example.project.models.Trip

class TripAdapter(private val trips: MutableList<Trip?>, private val onTripClicked: (Int) -> Unit) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

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
        if (trip != null) {
            holder.bind(trip)
        }

        holder.itemView.setOnClickListener {
            trip?.id?.let { it1 -> onTripClicked(it1) }
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
