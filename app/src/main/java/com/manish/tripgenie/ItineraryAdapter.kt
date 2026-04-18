package com.manish.tripgenie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.manish.tripgenie.model.ItineraryItem
import com.google.android.material.card.MaterialCardView

class ItineraryAdapter : ListAdapter<ItineraryItem, ItineraryAdapter.ItineraryViewHolder>(ItineraryDiffCallback()) {

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
        val item = getItem(position)
        holder.dayTitle.text = item.title
        holder.dayDetails.text = item.details
    }

    class ItineraryDiffCallback : DiffUtil.ItemCallback<ItineraryItem>() {
        override fun areItemsTheSame(oldItem: ItineraryItem, newItem: ItineraryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ItineraryItem, newItem: ItineraryItem): Boolean {
            return oldItem == newItem
        }
    }
}
