package com.example.tripgenie.network

/**
 * Unified model for Amadeus flight offers
 */
data class FlightOfferUnified(
    val id: String,
    val airlineCode: String,
    val airlineName: String,
    val price: Double,
    val currency: String,
    val stops: Int,
    val duration: String, // ISO 8601 duration
    val durationMinutes: Int, // Total duration in minutes for comparison
    val label: String? = null // "Cheapest", "Fastest", "Best Value"
)

data class AmadeusTokenResponse(
    val access_token: String,
    val expires_in: Long
)
