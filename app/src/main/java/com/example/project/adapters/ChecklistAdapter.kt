package com.example.project.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R
import com.example.project.models.ChecklistItem

class ChecklistItemsAdapter(private val checklistItems: List<ChecklistItem>) : RecyclerView.Adapter<ChecklistItemsAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        private val checkBox: CheckBox = view.findViewById(R.id.checkBox)

        fun bind(checklistItem: ChecklistItem) {
            titleTextView.text = checklistItem.title
            checkBox.isChecked = checklistItem.isCompleted
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.trip_overview_checklist_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(checklistItems[position])
    }

    override fun getItemCount() = checklistItems.size
}
