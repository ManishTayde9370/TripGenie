package com.example.tripgenie

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.launch

class TripViewModel : ViewModel() {

    private val _itineraryList = MutableLiveData<List<String>>(emptyList())
    val itineraryList: LiveData<List<String>> = _itineraryList

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // 🚀 Consider moving this to local.properties and accessing via BuildConfig for security
    private val apiKey = "AIzaSyAdzKGYC1P4Ox6Qyi166HV3j9yoNcF7oHs"

    private val config = generationConfig {
        temperature = 0.7f
        topK = 40
        topP = 0.95f
        maxOutputTokens = 2048
    }

    // Updated to use "gemini-1.5-flash" as requested
    private val model = GenerativeModel(
        modelName = "models/gemini-flash-latest",
        apiKey = apiKey,
        generationConfig = config
    )


    fun generateTripPlan(place: String, days: String, travelers: String) {
        val prompt = """
            Generate a concise day-wise travel itinerary for $travelers travelers visiting $place for $days days.
            Only provide the day-by-day plan.
            Format:
            Day 1: [Activities]
            Day 2: [Activities]
        """.trimIndent()

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Log.d("TripViewModel", "Requesting: $place using gemini-1.5-flash")
                _itineraryList.value = emptyList()

                val response = model.generateContent(prompt)
                val text = response.text

                if (text.isNullOrBlank()) {
                    _error.value = "AI returned no text."
                } else {
                    _itineraryList.value = parseItinerary(text)
                }

            } catch (e: Exception) {
                Log.e("TripViewModel", "Request failed", e)

                // Fallback to gemini-pro if gemini-1.5-flash is unavailable
                if (e.localizedMessage?.contains("404") == true || e.localizedMessage?.contains("not found") == true) {
                    tryFallback(prompt)
                } else {
                    _error.value = "Error: ${e.localizedMessage}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun tryFallback(prompt: String) {
        try {
            // Using "gemini-pro" as a fallback model
            val fallbackModel = GenerativeModel(modelName = "gemini-pro", apiKey = apiKey, generationConfig = config)
            val response = fallbackModel.generateContent(prompt)
            val text = response.text
            if (!text.isNullOrBlank()) {
                _itineraryList.value = parseItinerary(text)
                _error.value = null
            } else {
                _error.value = "AI Error: Fallback model returned no text."
            }
        } catch (e: Exception) {
            _error.value = "AI Error: Model not found. Please verify your API Key and Model permissions in Google AI Studio."
        }
    }

    private fun parseItinerary(text: String): List<String> {
        val lines = text.split("\n").filter { it.isNotBlank() }
        val formatted = mutableListOf<String>()
        var currentDay = StringBuilder()

        for (line in lines) {
            val trimmedLine = line.trim()
            if (trimmedLine.startsWith("Day", ignoreCase = true) &&
                (trimmedLine.contains(":") || trimmedLine.contains(" "))) {

                if (currentDay.isNotEmpty()) {
                    formatted.add(currentDay.toString().trim())
                }
                currentDay = StringBuilder(trimmedLine)
            } else {
                if (currentDay.isNotEmpty()) {
                    currentDay.append("\n").append(trimmedLine)
                } else if (trimmedLine.isNotEmpty()) {
                    currentDay.append(trimmedLine)
                }
            }
        }

        if (currentDay.isNotEmpty()) {
            formatted.add(currentDay.toString().trim())
        }

        return if (formatted.isEmpty()) listOf(text) else formatted
    }
}
