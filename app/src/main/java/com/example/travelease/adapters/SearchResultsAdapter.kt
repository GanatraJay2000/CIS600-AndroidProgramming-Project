package com.example.travelease.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelease.R
import com.google.android.libraries.places.api.model.AutocompletePrediction


class SearchResultsAdapter(
    private val searchResults: List<AutocompletePrediction>,
    private val onClick: (AutocompletePrediction) -> Unit
) : RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val resultTextView: TextView = itemView.findViewById(R.id.resultTextView)

        init {
            itemView.setOnClickListener {
                onClick(searchResults[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val prediction = searchResults[position]
        holder.resultTextView.text = prediction.getFullText(null).toString()
    }

    override fun getItemCount(): Int = searchResults.size
}