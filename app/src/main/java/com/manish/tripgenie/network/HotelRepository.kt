package com.manish.tripgenie.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/**
 * Repository to manage advanced hotel search using industry-standard Amadeus APIs
 * and Unsplash fallback for images.
 */
class HotelRepository(private val amadeusService: AmadeusService) {

    private val client = OkHttpClient()
    private val unsplashAccessKey = "YOUR_UNSPLASH_ACCESS_KEY" // Should also be in BuildConfig in real app

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
     * Get city code dynamically from city name
     */
    suspend fun getCityCode(cityName: String): String? = withContext(Dispatchers.IO) {
        val token = getValidToken() ?: return@withContext null
        val response = amadeusService.searchLocations(token, cityName) ?: return@withContext null

        try {
            val root = JSONObject(response)
            val data = root.getJSONArray("data")
            if (data.length() > 0) {
                data.getJSONObject(0).getString("iataCode")
            } else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Search hotels available for specific dates and guest count in INR
     */
    suspend fun searchHotels(
        cityCode: String,
        cityName: String,
        checkInDate: String,
        checkOutDate: String,
        adults: Int
    ): List<HotelOfferUnified> = withContext(Dispatchers.IO) {
        val token = getValidToken() ?: return@withContext emptyList()
        
        // 1. Get hotels in the city
        val hotelsJson = amadeusService.getHotelsByCity(token, cityCode) ?: return@withContext emptyList()
        val hotelIds = try {
            val root = JSONObject(hotelsJson)
            val data = root.getJSONArray("data")
            val ids = mutableListOf<String>()
            for (i in 0 until minOf(data.length(), 10)) {
                ids.add(data.getJSONObject(i).getString("hotelId"))
            }
            ids.joinToString(",")
        } catch (e: Exception) { "" }

        if (hotelIds.isEmpty()) return@withContext emptyList()

        // 2. Get offers for those hotels with specific dates
        val offersJson = amadeusService.getHotelOffers(token, hotelIds, checkInDate, checkOutDate, adults) 
            ?: return@withContext emptyList()
        
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
                
                val mealPlan = firstOffer.optJSONObject("boardFoodPlan")?.optString("type", "Room Only")
                    ?: "Standard Plan"
                
                val policies = firstOffer.optJSONObject("policies")
                val cancellation = policies?.optJSONArray("cancellations")?.optJSONObject(0)?.optString("description", "Non-refundable") 
                    ?: "Check details at check-in"

                val hotelName = hotel.getString("name")

                // Fetch fallback image if null (simulated here since Amadeus test often returns null)
                val imageUrl = fetchFallbackImage(hotelName, cityName)

                offers.add(
                    HotelOfferUnified(
                        hotelId = hotel.getString("hotelId"),
                        hotelName = hotelName,
                        address = "City: $cityName",
                        pricePerNight = price.optDouble("total", 0.0),
                        currency = "₹",
                        rating = hotel.optString("rating", "N/A"),
                        amenities = firstOffer.optJSONArray("amenities")?.toString() ?: "Basic Amenities",
                        roomType = room?.optJSONObject("typeEstimated")?.optString("category", "Standard Room") ?: "Standard Room",
                        totalPrice = price.getDouble("total"),
                        cityCode = cityCode,
                        checkInDate = checkInDate,
                        checkOutDate = checkOutDate,
                        mealPlan = if (mealPlan.contains("BREAKFAST", true)) "Breakfast included" else "Room Only",
                        cancellationPolicy = cancellation,
                        imageUrl = imageUrl
                    )
                )
            }
            offers
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Fallback image logic using Unsplash
     */
    private suspend fun fetchFallbackImage(hotelName: String, cityName: String): String? = withContext(Dispatchers.IO) {
        val query = "hotel $cityName"
        val url = "https://api.unsplash.com/search/photos?query=$query&client_id=$unsplashAccessKey&per_page=1"

        val request = Request.Builder().url(url).build()
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val body = response.body?.string() ?: return@withContext null
                val json = JSONObject(body)
                val results = json.getJSONArray("results")
                if (results.length() > 0) {
                    results.getJSONObject(0).getJSONObject("urls").getString("regular")
                } else null
            }
        } catch (e: IOException) {
            null
        }
    }
}
