package com.example.project.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R
import com.example.project.models.Checklist
import com.example.project.models.Note
import com.example.project.models.Place
import com.example.project.models.Section
import java.io.Serializable


class TripOverviewSectionsAdapter(private val sections: List<Section>) : RecyclerView.Adapter<SectionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.trip_overview_section, parent, false)
        return SectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val section = sections[position]
        holder.bind(section)
    }

    override fun getItemCount() = sections.size
}
class SectionItemsAdapter(private val items: List<Serializable>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Note -> R.layout.trip_overview_note_item
            is Checklist -> R.layout.trip_overview_checklist
            is Place -> R.layout.place_item
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.trip_overview_note_item -> NotesViewHolder(inflater.inflate(viewType, parent, false))
            R.layout.trip_overview_checklist -> ChecklistViewHolder(inflater.inflate(viewType, parent, false))
            R.layout.place_item -> PlaceViewHolder(inflater.inflate(viewType, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is Note -> (holder as NotesViewHolder).bind(item)
            is Checklist -> (holder as ChecklistViewHolder).bind(item)
            is Place -> (holder as PlaceViewHolder).bind(item)
        }
    }

    override fun getItemCount() = items.size

    // ViewHolders for each item type (Note, Checklist, Place)
    // These should be similar to those you had in TripOverviewSectionsAdapter

    class NotesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)

        fun bind(note: Note) {
            descriptionTextView.text = note.description
        }
    }


    class ChecklistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.checkListTitleTextView)
        private val checklistRecyclerView: RecyclerView = view.findViewById(R.id.checklistRecyclerView)

        fun bind(checklist: Checklist) {
            titleTextView.text = checklist.title
            checklistRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            checklistRecyclerView.adapter = ChecklistItemsAdapter(checklist.items)
        }
    }

    class PlaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        private val imageView: ImageView = view.findViewById(R.id.imageView)
        private val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)

        fun bind(place: Place) {
            titleTextView.text = place.title
            descriptionTextView.text = place.description
            // Load image using an image loading library like Coil or Glide
        }
    }

}


class SectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val titleTextView: TextView = view.findViewById(R.id.sectionTitleTextView)
    private val itemsRecyclerView: RecyclerView = view.findViewById(R.id.itemsRecyclerView)

    fun bind(section: Section) {
        titleTextView.text = section.title

        // Create an adapter for the items within this section
        val itemsAdapter = SectionItemsAdapter(section.items)
        itemsRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
        itemsRecyclerView.adapter = itemsAdapter
    }
}