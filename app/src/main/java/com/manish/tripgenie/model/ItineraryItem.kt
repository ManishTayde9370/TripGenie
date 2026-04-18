package com.manish.tripgenie.model

import java.util.UUID

/**
 * A data class representing a single card in the itinerary.
 * The 'id' is crucial for DiffUtil to efficiently update the list.
 */
data class ItineraryItem(
    val title: String,
    val details: String,
    val id: String = UUID.randomUUID().toString(),
    val latitude: Double? = null,
    val longitude: Double? = null
)
