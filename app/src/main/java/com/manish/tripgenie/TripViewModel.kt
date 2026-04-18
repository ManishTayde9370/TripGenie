package com.manish.tripgenie

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manish.tripgenie.model.ItineraryItem
import com.manish.tripgenie.model.MapMarker
import com.manish.tripgenie.model.MarkerType
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * Production-ready ViewModel for Trip Planning.
 * Uses a robust configuration to avoid SDK-level 404 and Serialization errors.
 */
class TripViewModel : ViewModel() {

    private val _itineraryList = MutableLiveData<List<ItineraryItem>>(emptyList())
    val itineraryList: LiveData<List<ItineraryItem>> = _itineraryList

    private val _mapMarkers = MutableLiveData<List<MapMarker>>(emptyList())
    val mapMarkers: LiveData<List<MapMarker>> = _mapMarkers

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val apiKey = BuildConfig.GEMINI_API_KEY

    // Optimized configuration for concise responses
    private val config = generationConfig {
        temperature = 0.3f // Balanced for expert creativity and accuracy
        topK = 30
        topP = 0.9f
        maxOutputTokens = 4096
    }

    /**
     * Using Gemini 1.5 Flash (fixed from invalid 2.5).
     */
    private val model = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = apiKey,
        generationConfig = config,
        requestOptions = RequestOptions(apiVersion = "v1")
    )

    fun generateTripPlan(
        destination: String,
        days: String,
        budget: String,
        style: String,
        interests: String
    ) {
        if (destination.isBlank()) return

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                _itineraryList.value = emptyList()
                _mapMarkers.value = emptyList()

                if (apiKey.isBlank() || apiKey == "YOUR_API_KEY") {
                    _error.value = "API Key is missing. Please check local.properties."
                    return@launch
                }

                val dayCount = days.toIntOrNull()?.coerceIn(1, 3) ?: 1

                // 🧠 Expert Travel Planner Prompt
                val prompt = """
                    You are an expert travel planner. 
                    Create a $dayCount-day travel itinerary for $destination.
                    Travel Style: $style. Interests: $interests. Budget: $$budget.

                    Rules:
                    1. Maximum 3 places per day.
                    2. Each description MUST be exactly 1 line only.
                    3. Only include important, top-tier attractions.
                    4. Return ONLY a JSON object. No extra text or markdown.

                    JSON Structure:
                    {
                      "itinerary": [
                        {
                          "title": "Day 1: Morning",
                          "details": "[Place Name] - [Short 1-line description]",
                          "latitude": 0.0,
                          "longitude": 0.0
                        }
                      ]
                    }
                """.trimIndent()

                val result = withContext(Dispatchers.IO) {
                    val response = model.generateContent(prompt)
                    val responseText = response.text ?: throw Exception("Genie returned an empty response.")
                    
                    val sanitizedJson = responseText
                        .replace("```json", "")
                        .replace("```", "")
                        .trim()

                    withContext(Dispatchers.Default) {
                        parseJsonResponse(sanitizedJson)
                    }
                }

                _itineraryList.value = result.first
                _mapMarkers.value = result.second

            } catch (e: Exception) {
                handleFailure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun handleFailure(e: Exception) {
        Log.e("TripViewModel", "Trip Generation Failed", e)
        
        val userMessage = when (e) {
            is ServerException -> "The AI service is currently busy. Please try again."
            is GoogleGenerativeAIException -> "AI error: ${e.localizedMessage ?: "Check configuration."}"
            else -> "Failed to generate plan. Please check your connection."
        }
        _error.value = userMessage
    }

    private fun parseJsonResponse(jsonString: String): Pair<List<ItineraryItem>, List<MapMarker>> {
        val itineraryItems = mutableListOf<ItineraryItem>()
        val markers = mutableListOf<MapMarker>()

        try {
            val jsonObject = JSONObject(jsonString)
            val jsonArray = jsonObject.getJSONArray("itinerary")

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val title = item.getString("title")
                val details = item.getString("details")
                val lat = item.optDouble("latitude", 0.0)
                val lng = item.optDouble("longitude", 0.0)

                val itineraryItem = ItineraryItem(
                    title = title,
                    details = details,
                    latitude = lat,
                    longitude = lng
                )
                itineraryItems.add(itineraryItem)

                if (lat != 0.0 && lng != 0.0) {
                    markers.add(
                        MapMarker(
                            id = itineraryItem.id,
                            title = title,
                            snippet = details,
                            position = LatLng(lat, lng),
                            type = MarkerType.ITINERARY
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("TripViewModel", "JSON Parsing error", e)
        }

        return Pair(itineraryItems, markers)
    }
}
