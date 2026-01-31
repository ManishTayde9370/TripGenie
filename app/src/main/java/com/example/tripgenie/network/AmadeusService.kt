package com.example.tripgenie.network

import android.util.Log
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

/**
 * Service to interact with Amadeus - industry-standard travel API
 */
class AmadeusService {
    private val client = OkHttpClient()
    private val baseUrl = "https://test.api.amadeus.com"
    private val clientId = "AqXYhFvFYbDpBuC3V2ziaPAmL82u9Yrs"
    private val clientSecret = "LIbtt8Kp2DbtE2RE"

    /**
     * Generate OAuth access token using client credentials flow
     */
    fun fetchAccessToken(): AmadeusTokenResponse? {
        val url = "$baseUrl/v1/security/oauth2/token"
        val requestBody = FormBody.Builder()
            .add("grant_type", "client_credentials")
            .add("client_id", clientId)
            .add("client_secret", clientSecret)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                val body = response.body?.string() ?: return null
                val json = JSONObject(body)
                AmadeusTokenResponse(
                    access_token = json.getString("access_token"),
                    expires_in = json.getLong("expires_in")
                )
            }
        } catch (e: Exception) {
            Log.e("AmadeusService", "Error fetching token", e)
            null
        }
    }

    /**
     * Fetch near real-time flight prices from multiple airlines in INR
     */
    fun getFlightOffers(
        token: String,
        origin: String,
        destination: String,
        date: String,
        adults: Int
    ): String? {
        val url = "$baseUrl/v2/shopping/flight-offers" +
                "?originLocationCode=$origin" +
                "&destinationLocationCode=$destination" +
                "&departureDate=$date" +
                "&adults=$adults" +
                "&currencyCode=INR" +
                "&max=20"
        
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                response.body?.string()
            }
        } catch (e: IOException) {
            Log.e("AmadeusService", "Error fetching flight offers", e)
            null
        }
    }

    /**
     * Fetch hotel list by city code (Amadeus Hotel List API)
     */
    fun getHotelsByCity(token: String, cityCode: String): String? {
        val url = "$baseUrl/v1/reference-data/locations/hotels/by-city?cityCode=$cityCode"
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                response.body?.string()
            }
        } catch (e: IOException) {
            null
        }
    }

    /**
     * Fetch advanced hotel offers/prices in INR with dates (Amadeus Hotel Offers Search API)
     */
    fun getHotelOffers(
        token: String, 
        hotelIds: String,
        checkInDate: String,
        checkOutDate: String,
        adults: Int
    ): String? {
        // MANDATORY: Use currency=INR as requested
        val url = "$baseUrl/v3/shopping/hotel-offers" +
                "?hotelIds=$hotelIds" +
                "&checkInDate=$checkInDate" +
                "&checkOutDate=$checkOutDate" +
                "&adults=$adults" +
                "&currency=INR"

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                response.body?.string()
            }
        } catch (e: IOException) {
            null
        }
    }
}
