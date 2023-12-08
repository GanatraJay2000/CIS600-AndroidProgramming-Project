package com.example.project.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R
import com.google.android.libraries.places.api.model.Place

class PopularPlacesAdapter(private val places: List<Place>) : RecyclerView.Adapter<PopularPlacesAdapter.ViewHolder>() {

    // ViewHolder class to hold the views
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeImageView: ImageView = itemView.findViewById(R.id.placeImageView)
        val placeNameTextView: TextView = itemView.findViewById(R.id.placeNameTextView)
        val placeDescriptionTextView: TextView = itemView.findViewById(R.id.placeDescriptionTextView)
        val button1: Button = itemView.findViewById(R.id.button1)
        val button2: Button = itemView.findViewById(R.id.button2)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_popular_places, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = places[position]
        holder.placeNameTextView.text = place.name
        holder.placeDescriptionTextView.text = place.address
        holder.placeDescriptionTextView.text = "Description goes here" // Set description text

        holder.button1.setOnClickListener {  }

        holder.button2.setOnClickListener {
            // Handle button2 click
        }
        holder.placeImageView.setImageResource(R.drawable.switzerland)

    }

    override fun getItemCount(): Int {
        return places.size
    }
}