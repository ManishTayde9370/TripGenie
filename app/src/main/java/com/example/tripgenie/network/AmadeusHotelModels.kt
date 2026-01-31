package com.example.tripgenie.network

/**
 * Enhanced model for Amadeus hotel offers
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
    val cityCode: String,
    val checkInDate: String,
    val checkOutDate: String,
    val mealPlan: String, // e.g. "Breakfast included"
    val cancellationPolicy: String,
    val imageUrl: String? = null
)
