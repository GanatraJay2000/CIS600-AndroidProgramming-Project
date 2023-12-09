package com.example.project.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.project.R
import com.example.project.models.Location

class TopLocationsAdapter(private val locationsList: List<Location>, private val onLocationClicked: (Int) -> Unit) :
    RecyclerView.Adapter<TopLocationsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.locationImageView)
        val nameView: TextView = view.findViewById(R.id.locationNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.dashboard_top_locations_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = locationsList[position]
        holder.nameView.text = location.title
        holder.imageView.load(location.imageUrl) {
            crossfade(true)
            error(R.drawable.image_placeholder)
        }

        holder.itemView.setOnClickListener { onLocationClicked(location.id) }
    }


    override fun getItemCount() = locationsList.size
}

