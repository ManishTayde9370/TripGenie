package com.example.tripgenie.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * Repository to manage hotel price comparison using Amadeus APIs
 */
class HotelRepository(private val amadeusService: AmadeusService) {

    private var cachedToken: String? = null
    private var tokenExpiryTime: Long = 0

    private suspend fun getValidToken(): String? = withContext(Dispatchers.IO) {
        if (cachedToken != null && System.currentTimeMillis() < tokenExpiryTime) {
            return@withContext cachedToken
        }
        val response = amadeusService.fetchAccessToken()
        if (response != null) {
            cachedToken = response.access_token
            tokenExpiryTime = System.currentTimeMillis() + (response.expires_in * 1000) - 60000
            cachedToken
        } else null
    }

    /**
     * Search hotels and their prices in INR for a given city
     */
    suspend fun searchHotels(cityCode: String): List<HotelOfferUnified> = withContext(Dispatchers.IO) {
        val token = getValidToken() ?: return@withContext emptyList()
        
        // 1. Get hotels in the city
        val hotelsJson = amadeusService.getHotelsByCity(token, cityCode) ?: return@withContext emptyList()
        val hotelIds = try {
            val root = JSONObject(hotelsJson)
            val data = root.getJSONArray("data")
            val ids = mutableListOf<String>()
            for (i in 0 until minOf(data.length(), 10)) { // Limit to 10 hotels for speed
                ids.add(data.getJSONObject(i).getString("hotelId"))
            }
            ids.joinToString(",")
        } catch (e: Exception) { "" }

        if (hotelIds.isEmpty()) return@withContext emptyList()

        // 2. Get offers for those hotels
        val offersJson = amadeusService.getHotelOffers(token, hotelIds) ?: return@withContext emptyList()
        
        try {
            val root = JSONObject(offersJson)
            val dataArray = root.getJSONArray("data")
            val offers = mutableListOf<HotelOfferUnified>()

            for (i in 0 until dataArray.length()) {
                val item = dataArray.getJSONObject(i)
                val hotel = item.getJSONObject("hotel")
                val hotelOffers = item.getJSONArray("offers")
                if (hotelOffers.length() == 0) continue
                
                val firstOffer = hotelOffers.getJSONObject(0)
                val price = firstOffer.getJSONObject("price")
                val room = firstOffer.optJSONObject("room")
                
                offers.add(
                    HotelOfferUnified(
                        hotelId = hotel.getString("hotelId"),
                        hotelName = hotel.getString("name"),
                        address = "City: ${hotel.getString("cityCode")}",
                        pricePerNight = price.getDouble("total"),
                        currency = "₹", // Amadeus returns INR as requested
                        rating = hotel.optString("rating", "N/A"),
                        amenities = firstOffer.optJSONArray("amenities")?.toString() ?: "Standard Amenities",
                        roomType = room?.optJSONObject("typeEstimated")?.optString("category", "Standard Room") ?: "Standard Room",
                        totalPrice = price.getDouble("total"),
                        cityCode = cityCode
                    )
                )
            }
            offers
        } catch (e: Exception) {
            emptyList()
        }
    }
}
