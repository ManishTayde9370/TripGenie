package com.manish.tripgenie.ui.screens

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manish.tripgenie.BuildConfig
import com.manish.tripgenie.model.EventItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class EventsViewModel : ViewModel() {

    private val client = OkHttpClient()

    // 🚀 SECURED: Keys are now read from BuildConfig
    private val ticketmasterKey = BuildConfig.TICKETMASTER_API_KEY
    private val predictHqToken = BuildConfig.PREDICTHQ_TOKEN

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

            val ticketmasterResults = fetchFromTicketmaster(city)

            if (ticketmasterResults.isNotEmpty()) {
                eventList.addAll(ticketmasterResults)
                isLoading.value = false
            } else {
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

    private suspend fun fetchFromPredictHQ(): List<EventItem> = withContext(Dispatchers.IO) {
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

    private fun mapPredictHQToUnified(json: JSONObject): EventItem {
        val name = json.optString("title", "Unnamed Event")
        val date = json.optString("start", "N/A").take(10)
        val category = json.optString("category", "General").replaceFirstChar { it.uppercase() }
        
        val venue = json.optJSONArray("entities")?.optJSONObject(0)?.optString("name")
            ?: json.optString("location", "India")
            
        val link = "https://www.predicthq.com/events/${json.optString("id")}"
        val placeholderImage = "https://images.unsplash.com/photo-1501281668745-f7f57925c3b4?auto=format&fit=crop&q=80&w=1000"

        return EventItem(name, date, venue, placeholderImage, link, category, "PredictHQ")
    }
}
