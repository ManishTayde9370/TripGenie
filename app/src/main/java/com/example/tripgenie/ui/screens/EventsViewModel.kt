package com.example.tripgenie.ui.screens

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

data class EventItem(
    val name: String,
    val date: String,
    val venue: String,
    val imageUrl: String,
    val link: String,
    val category: String = "General"
)

class EventsViewModel : ViewModel() {
    private val apiKey = "B2fuVCmAXBGRuuKlK6TYzZb35QIkY4tC"
    private val client = OkHttpClient()

    val eventList = mutableStateListOf<EventItem>()
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    fun fetchEvents(city: String) {
        isLoading.value = true
        error.value = null
        val url = "https://app.ticketmaster.com/discovery/v2/events.json?city=$city&apikey=$apiKey"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                val responseData = response.body?.string()

                if (response.isSuccessful && responseData != null) {
                    val json = JSONObject(responseData)
                    val eventsArray = json.optJSONObject("_embedded")?.optJSONArray("events")

                    withContext(Dispatchers.Main) {
                        eventList.clear()
                        if (eventsArray != null && eventsArray.length() > 0) {
                            for (i in 0 until eventsArray.length()) {
                                val event = eventsArray.getJSONObject(i)
                                val name = event.optString("name", "Unnamed Event")
                                val date = event.optJSONObject("dates")
                                    ?.optJSONObject("start")
                                    ?.optString("localDate", "N/A")
                                val venue = event.optJSONObject("_embedded")
                                    ?.optJSONArray("venues")
                                    ?.optJSONObject(0)
                                    ?.optString("name", "Unknown Venue") ?: "Unknown Venue"
                                val image = event.optJSONArray("images")
                                    ?.optJSONObject(0)
                                    ?.optString("url", "")
                                val link = event.optString("url", "")
                                
                                val classifications = event.optJSONArray("classifications")
                                val category = classifications?.optJSONObject(0)
                                    ?.optJSONObject("segment")
                                    ?.optString("name", "General") ?: "General"

                                eventList.add(EventItem(name, date ?: "N/A", venue, image ?: "", link, category))
                            }
                        } else {
                            error.value = "No events found in $city"
                        }
                        isLoading.value = false
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        error.value = "Error: ${response.code}"
                        isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    error.value = "Error: ${e.localizedMessage}"
                    isLoading.value = false
                }
            }
        }
    }
}
