package com.example.tripgenie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class ItineraryAdapter(private val items: List<ItineraryItem>) :
    RecyclerView.Adapter<ItineraryAdapter.ItineraryViewHolder>() {

    class ItineraryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayText: TextView = itemView.findViewById(R.id.tvDay)
        val detailText: TextView = itemView.findViewById(R.id.tvDetails)
        val card: MaterialCardView = itemView.findViewById(R.id.cardItinerary)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItineraryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_itinerary, parent, false)
        return ItineraryViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItineraryViewHolder, position: Int) {
        val item = items[position]
        holder.dayText.text = item.day
        holder.detailText.text = item.details
    }

    override fun getItemCount(): Int = items.size
}
