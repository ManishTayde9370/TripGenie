package com.example.tripgenie.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.Duration

/**
 * Repository to manage near real-time flight prices from multiple airlines
 */
class FlightRepository(private val amadeusService: AmadeusService) {

    private var cachedToken: String? = null
    private var tokenExpiryTime: Long = 0

    /**
     * Ensures a valid OAuth token is available
     */
    private suspend fun getValidToken(): String? = withContext(Dispatchers.IO) {
        if (cachedToken != null && System.currentTimeMillis() < tokenExpiryTime) {
            return@withContext cachedToken
        }

        val response = amadeusService.fetchAccessToken()
        if (response != null) {
            cachedToken = response.access_token
            tokenExpiryTime = System.currentTimeMillis() + (response.expires_in * 1000) - 60000 // 1 min buffer
            cachedToken
        } else {
            null
        }
    }

    /**
     * Fetch and compare flight offers using industry-standard travel API
     */
    suspend fun searchFlights(
        origin: String,
        destination: String,
        date: String,
        adults: Int
    ): List<FlightOfferUnified> = withContext(Dispatchers.IO) {
        val token = getValidToken() ?: return@withContext emptyList()
        val jsonString = amadeusService.getFlightOffers(token, origin, destination, date, adults) ?: return@withContext emptyList()

        try {
            val root = JSONObject(jsonString)
            val dataArray = root.getJSONArray("data")
            val dictionaries = root.getJSONObject("dictionaries")
            val carriers = dictionaries.getJSONObject("carriers")

            val offers = mutableListOf<FlightOfferUnified>()

            for (i in 0 until dataArray.length()) {
                val offerJson = dataArray.getJSONObject(i)
                val itineraries = offerJson.getJSONArray("itineraries").getJSONObject(0)
                val durationStr = itineraries.getString("duration") // e.g. PT2H30M
                val segments = itineraries.getJSONArray("segments")
                val carrierCode = segments.getJSONObject(0).getString("carrierCode")
                val airlineName = carriers.optString(carrierCode, carrierCode)
                
                val priceObj = offerJson.getJSONObject("price")
                val totalPrice = priceObj.getDouble("total")
                val currency = priceObj.getString("currency")
                
                val stops = segments.length() - 1
                val durationMinutes = parseDurationToMinutes(durationStr)

                offers.add(
                    FlightOfferUnified(
                        id = offerJson.getString("id"),
                        airlineCode = carrierCode,
                        airlineName = airlineName,
                        price = totalPrice,
                        currency = currency,
                        stops = stops,
                        duration = formatDuration(durationStr),
                        durationMinutes = durationMinutes
                    )
                )
            }

            applyComparisonLogic(offers)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Identifies cheapest, fastest, and best value options
     */
    private fun applyComparisonLogic(offers: List<FlightOfferUnified>): List<FlightOfferUnified> {
        if (offers.isEmpty()) return offers

        val cheapest = offers.minByOrNull { it.price }
        val fastest = offers.minByOrNull { it.durationMinutes }
        
        // Best value: balancing price and duration (normalized simple score)
        val bestValue = offers.minByOrNull { 
            (it.price / (cheapest?.price ?: 1.0)) + (it.durationMinutes.toDouble() / (fastest?.durationMinutes ?: 1))
        }

        return offers.map { offer ->
            val labels = mutableListOf<String>()
            if (offer.id == cheapest?.id) labels.add("💰 Cheapest")
            if (offer.id == fastest?.id) labels.add("⚡ Fastest")
            if (offer.id == bestValue?.id && labels.isEmpty()) labels.add("⭐ Best Value")
            
            offer.copy(label = labels.firstOrNull())
        }
    }

    private fun parseDurationToMinutes(duration: String): Int {
        return try {
            // Basic parsing for "PT2H30M"
            val hMatch = Regex("(\\d+)H").find(duration)
            val mMatch = Regex("(\\d+)M").find(duration)
            val hours = hMatch?.groupValues?.get(1)?.toInt() ?: 0
            val minutes = mMatch?.groupValues?.get(1)?.toInt() ?: 0
            (hours * 60) + minutes
        } catch (e: Exception) {
            0
        }
    }

    private fun formatDuration(duration: String): String {
        return duration.removePrefix("PT").replace("H", "h ").replace("M", "m").lowercase()
    }
}
