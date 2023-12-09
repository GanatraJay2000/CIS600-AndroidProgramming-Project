package com.example.project.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R
import com.example.project.models.ChecklistItem
import com.example.project.models.Note
import com.example.project.models.Place

class NotesAdapter(private val notes: List<Note>) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    class NotesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)

        fun bind(note: Note) {
            descriptionTextView.text = note.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.trip_overview_note_item, parent, false)
        return NotesViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount() = notes.size
}


class ChecklistsAdapter(private val checklists: List<ChecklistItem>) : RecyclerView.Adapter<ChecklistsAdapter.ChecklistViewHolder>() {

    class ChecklistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        // Other views...

        fun bind(checklist: ChecklistItem) {
            titleTextView.text = checklist.title
            // Bind other views...
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.trip_overview_checklist_item, parent, false)
        return ChecklistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        holder.bind(checklists[position])
    }

    override fun getItemCount() = checklists.size
}


class PlacesAdapter(private val places: List<Place>)  : RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder>() {

    class PlaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        private val addressTextView: TextView = view.findViewById(R.id.addressTextView)
        private val imageView: ImageView = view.findViewById(R.id.imageView)
        // Other views...

        fun bind(place: Place) {
            // Bind other views...
            titleTextView.text = place.title
            addressTextView.text = place.address
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.place_item, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(places[position])
    }

    override fun getItemCount() = places.size
}