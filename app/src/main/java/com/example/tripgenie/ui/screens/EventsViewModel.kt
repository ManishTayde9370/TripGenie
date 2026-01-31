package com.example.tripgenie.ui.screens

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

/**
 * Unified Event Model for TripGenie
 */
data class EventItem(
    val name: String,
    val date: String,
    val venue: String,
    val imageUrl: String,
    val link: String,
    val category: String,
    val source: String // internal use: "Ticketmaster" or "PredictHQ"
)

class EventsViewModel : ViewModel() {

    private val client = OkHttpClient()

    // Ticketmaster (Primary)
    private val ticketmasterKey = "B2fuVCmAXBGRuuKlK6TYzZb35QIkY4tC"

    // PredictHQ (Fallback)
    private val predictHqToken = "vGrPT5LR5NOTI_JNe0Yx4Wi5nzAhrZJ8EIbd7hCr"
    private val predictHqBaseUrl = "https://api.predicthq.com/v1/events/"

    val eventList = mutableStateListOf<EventItem>()
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    /**
     * Main function to fetch events with fallback logic
     */
    fun fetchEvents(city: String) {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            eventList.clear()

            // 1. Try Primary API: Ticketmaster
            val ticketmasterResults = fetchFromTicketmaster(city)

            if (ticketmasterResults.isNotEmpty()) {
                eventList.addAll(ticketmasterResults)
                isLoading.value = false
            } else {
                // 2. Fallback: Ticketmaster failed or returned empty. Try PredictHQ.
                Log.d("EventsViewModel", "Ticketmaster failed or empty for $city. Falling back to PredictHQ.")
                val predictHqResults = fetchFromPredictHQ()
                
                if (predictHqResults.isNotEmpty()) {
                    eventList.addAll(predictHqResults)
                } else {
                    error.value = "No events found locally or globally."
                }
                isLoading.value = false
            }
        }
    }

    /**
     * Primary API: Ticketmaster Discovery
     */
    private suspend fun fetchFromTicketmaster(city: String): List<EventItem> = withContext(Dispatchers.IO) {
        val url = "https://app.ticketmaster.com/discovery/v2/events.json?city=$city&apikey=$ticketmasterKey"
        try {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val responseData = response.body?.string()

            if (response.isSuccessful && responseData != null) {
                val json = JSONObject(responseData)
                val eventsArray = json.optJSONObject("_embedded")?.optJSONArray("events")
                
                val results = mutableListOf<EventItem>()
                if (eventsArray != null) {
                    for (i in 0 until eventsArray.length()) {
                        val event = eventsArray.getJSONObject(i)
                        results.add(mapTicketmasterToUnified(event))
                    }
                }
                results
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("EventsViewModel", "Ticketmaster API Exception", e)
            emptyList()
        }
    }

    /**
     * Fallback API: PredictHQ
     */
    private suspend fun fetchFromPredictHQ(): List<EventItem> = withContext(Dispatchers.IO) {
        // Querying for India (IN) as requested in the fallback requirement
        val url = "$predictHqBaseUrl?country=IN&limit=10"
        try {
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $predictHqToken")
                .addHeader("Accept", "application/json")
                .build()
            
            val response = client.newCall(request).execute()
            val responseData = response.body?.string()

            if (response.isSuccessful && responseData != null) {
                val json = JSONObject(responseData)
                val resultsArray = json.optJSONArray("results")
                
                val results = mutableListOf<EventItem>()
                if (resultsArray != null) {
                    for (i in 0 until resultsArray.length()) {
                        val event = resultsArray.getJSONObject(i)
                        results.add(mapPredictHQToUnified(event))
                    }
                }
                results
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("EventsViewModel", "PredictHQ API Exception", e)
            emptyList()
        }
    }

    /**
     * Mapping Ticketmaster JSON to Unified Model
     */
    private fun mapTicketmasterToUnified(json: JSONObject): EventItem {
        val name = json.optString("name", "Unnamed Event")
        val date = json.optJSONObject("dates")?.optJSONObject("start")?.optString("localDate", "N/A") ?: "N/A"
        
        val venue = json.optJSONObject("_embedded")?.optJSONArray("venues")?.optJSONObject(0)?.optString("name")
            ?: json.optJSONObject("_embedded")?.optJSONArray("venues")?.optJSONObject(0)?.optJSONObject("city")?.optString("name")
            ?: "Unknown Venue"
            
        val image = json.optJSONArray("images")?.optJSONObject(0)?.optString("url", "") ?: ""
        val link = json.optString("url", "")
        
        val category = json.optJSONArray("classifications")?.optJSONObject(0)
            ?.optJSONObject("segment")?.optString("name", "General") ?: "General"

        return EventItem(name, date, venue, image, link, category, "Ticketmaster")
    }

    /**
     * Mapping PredictHQ JSON to Unified Model
     */
    private fun mapPredictHQToUnified(json: JSONObject): EventItem {
        val name = json.optString("title", "Unnamed Event")
        val date = json.optString("start", "N/A").take(10) // Extract YYYY-MM-DD
        val category = json.optString("category", "General").replaceFirstChar { it.uppercase() }
        
        // PredictHQ venue logic
        val venue = json.optJSONArray("entities")?.optJSONObject(0)?.optString("name")
            ?: json.optString("location", "India")
            
        val link = "https://www.predicthq.com/events/${json.optString("id")}"
        
        // PredictHQ doesn't always provide direct images in basic event results, using a placeholder
        val placeholderImage = "https://images.unsplash.com/photo-1501281668745-f7f57925c3b4?auto=format&fit=crop&q=80&w=1000"

        return EventItem(name, date, venue, placeholderImage, link, category, "PredictHQ")
    }
}
