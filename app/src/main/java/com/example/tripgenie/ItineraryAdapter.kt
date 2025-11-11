package com.example.tripgenie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class ItineraryAdapter(private val items: List<String>) :
    RecyclerView.Adapter<ItineraryAdapter.ItineraryViewHolder>() {

    class ItineraryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: MaterialCardView = view.findViewById(R.id.itineraryCard)
        val dayTitle: TextView = view.findViewById(R.id.dayTitle)
        val dayDetails: TextView = view.findViewById(R.id.dayDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItineraryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_itinerary, parent, false)
        return ItineraryViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItineraryViewHolder, position: Int) {
        val item = items[position]

        // Split text into title and details
        val parts = item.split("\n", limit = 2)
        holder.dayTitle.text = parts.getOrNull(0) ?: "Day ${position + 1}"
        holder.dayDetails.text = parts.getOrNull(1) ?: ""
    }

    override fun getItemCount() = items.size
}
