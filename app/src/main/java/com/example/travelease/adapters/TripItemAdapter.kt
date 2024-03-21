package com.example.travelease.adapters

import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.travelease.R
import com.example.travelease.helper.DatabaseHelper
import com.example.travelease.model.ChecklistItem
import com.example.travelease.model.Note
import com.example.travelease.model.Place
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.button.MaterialButton

class NotesAdapter(val notes: MutableList<Note>, private val dbHelper: DatabaseHelper) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    class NotesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val noteEditText: EditText = view.findViewById(R.id.noteEditText)
        val deleteNoteButton: MaterialButton = view.findViewById(R.id.deleteNoteButton)
        val updateNoteButton: MaterialButton = view.findViewById(R.id.updateNoteButton)

        fun bind(note: Note) {
            noteEditText.text = Editable.Factory.getInstance().newEditable(note.description)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.trip_overview_note_item, parent, false)
        return NotesViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note)

        holder.noteEditText.isEnabled = false


        holder.deleteNoteButton.setOnClickListener {
            dbHelper.deleteNoteWithTripIdAndTextAs(note.tripId, note.description)
            notes.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
        }

        holder.updateNoteButton.setOnClickListener {
            // Enable editing for the note
            if (holder.noteEditText.isEnabled) {
                // Update note in the database
                val updatedNote = note.copy(description = holder.noteEditText.text.toString())
                dbHelper.updateNote(updatedNote)
                // Update note in the adapter
                notes[holder.adapterPosition] = updatedNote
                notifyItemChanged(holder.adapterPosition)
                holder.noteEditText.isEnabled = false
                holder.updateNoteButton.icon = ContextCompat.getDrawable(holder.itemView.context, R.drawable.edit)
            } else {
                holder.noteEditText.isEnabled = true
                holder.updateNoteButton.icon = ContextCompat.getDrawable(holder.itemView.context, R.drawable.done)
            }
        }
    }

    override fun getItemCount() = notes.size
}


class ChecklistsAdapter(val checklists: MutableList<ChecklistItem>, private val dbHelper: DatabaseHelper) : RecyclerView.Adapter<ChecklistsAdapter.ChecklistViewHolder>() {

    class ChecklistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.checkBox)
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)

        fun bind(checklistItem: ChecklistItem) {
            checkBox.isChecked = checklistItem.isCompleted
            titleTextView.text = checklistItem.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.trip_overview_checklist_item, parent, false)
        return ChecklistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        val checklistItem = checklists[position]
        holder.bind(checklistItem)

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            // Update checklist item in the database
            val updatedChecklistItem = checklistItem.copy(isCompleted = isChecked)
            dbHelper.updateChecklistItem(updatedChecklistItem)
            // Update checklist item in the adapter
            checklists[holder.adapterPosition] = updatedChecklistItem
        }
    }

    override fun getItemCount() = checklists.size
}


class PlacesAdapter(private val context: Context, private val places: List<Place>)  : RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder>() {
    private lateinit var placesClient: PlacesClient
    init {
        Places.initialize(context, "AIzaSyBvQIUByA2GmXPnNMZ51hNtVHDhBLMAvoI")
        placesClient = Places.createClient(context)
    }

    inner class PlaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        private val addressTextView: TextView = view.findViewById(R.id.addressTextView)
        private val imageView: ImageView = view.findViewById(R.id.imageView)

        fun bind(place: Place) {
            titleTextView.text = place.title
            addressTextView.text = place.address
            getPhoto(imageView, place.id)
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