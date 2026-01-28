package com.example.tripgenie.network

/**
 * Unified model for Amadeus hotel offers
 */
data class HotelOfferUnified(
    val hotelId: String,
    val hotelName: String,
    val address: String,
    val pricePerNight: Double,
    val currency: String,
    val rating: String,
    val amenities: String,
    val roomType: String,
    val totalPrice: Double,
    val cityCode: String
)
