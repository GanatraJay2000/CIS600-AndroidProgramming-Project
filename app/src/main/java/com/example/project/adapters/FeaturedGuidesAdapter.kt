package com.example.project.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.project.R
import com.example.project.models.Guide
class FeaturedGuidesAdapter(private val guidesList: List<Guide>) :
    RecyclerView.Adapter<FeaturedGuidesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.GuideCardImage)
        val titleView: TextView = view.findViewById(R.id.GuideCardTitle)
        val descView: TextView = view.findViewById(R.id.GuideCardDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.dashboard_featured_guides_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val guide = guidesList[position]
        holder.titleView.text = guide.title
        holder.descView.text = guide.description


        holder.imageView.load(guide.imageUrl) {
            crossfade(true)
            error(R.drawable.image_placeholder) // Placeholder for error situations
        }
    }


    override fun getItemCount() = guidesList.size
}

