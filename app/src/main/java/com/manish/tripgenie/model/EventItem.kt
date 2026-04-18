package com.manish.tripgenie.model

/**
 * Unified Event Model for TripGenie
 */
data class EventItem(
    val name: String,
    val date: String,
    val venue: String,
    val imageUrl: String,
    val link: String,
    val category: String = "General",
    val source: String = "Ticketmaster"
)
