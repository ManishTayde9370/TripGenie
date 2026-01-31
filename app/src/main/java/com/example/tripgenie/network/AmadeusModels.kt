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
    val duration: String,
    val durationMinutes: Int,
    val departureTime: String,
    val arrivalTime: String,
    val originCity: String,
    val destinationCity: String,
    val label: String? = null
)

data class AmadeusTokenResponse(
    val access_token: String,
    val expires_in: Long
)
