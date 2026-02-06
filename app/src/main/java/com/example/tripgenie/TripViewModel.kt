package com.example.tripgenie

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.*
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for generating trip itineraries and recommendations using Google Gemini.
 * Uses verified model: models/gemini-flash-latest with optimized prompt.
 */
class TripViewModel : ViewModel() {

    private val _itineraryList = MutableLiveData<List<String>>(emptyList())
    val itineraryList: LiveData<List<String>> = _itineraryList

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // 🔐 API Key is securely retrieved from BuildConfig
    private val apiKey = BuildConfig.GEMINI_API_KEY

    private val config = generationConfig {
        temperature = 0.7f
        topK = 40
        topP = 0.95f
        maxOutputTokens = 2048
    }

    /**
     * VERIFIED MODEL CONFIGURATION:
     * Using 'gemini-flash-latest' as verified by user testing.
     */
    private val model = GenerativeModel(
        modelName = "gemini-flash-latest",
        apiKey = apiKey,
        generationConfig = config
    )

    /**
     * Generates a personalized trip plan using a strict, token-efficient prompt.
     */
    fun generateTripPlan(
        destination: String,
        days: String,
        budget: String,
        style: String,
        interests: String
    ) {
        val prompt = """
            You are a smart travel planning assistant. 
            Keep the response concise and token-efficient. Use bullet points only.
            NO markdown. NO emojis. NO extra explanations. NO repetition. NO long sentences.

            USER INPUT:
            Destination: $destination
            Number of days: $days
            Budget: $budget
            Travel style: $style
            Interests: $interests

            OUTPUT FORMAT (STRICT):

            1. TRIP SUMMARY
            - Destination
            - Total days
            - Travel style
            - Budget range

            2. DAY-WISE PLAN
            Day 1:
            - Morning: [1 activity, max 8 words]
            - Afternoon: [1 activity, max 8 words]
            - Evening: [1 activity, max 8 words]

            3. HOTEL SUGGESTIONS
            - [Name], [Area], [Price range], [Reason max 6 words]

            4. FOOD TIP
            - [One local food suggestion]
        """.trimIndent()

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                _itineraryList.value = emptyList()

                if (apiKey.isBlank()) {
                    Log.e("TripViewModel", "Missing GEMINI_API_KEY in BuildConfig")
                    _error.value = "Unable to generate trip right now. Please try again later."
                    return@launch
                }

                Log.d("TripViewModel", "Invoking Gemini (gemini-flash-latest) for $destination")
                
                val response = model.generateContent(prompt)
                
                val text = try {
                    response.text
                } catch (serializationException: Exception) {
                    Log.e("TripViewModel", "Serialization Error: ${serializationException.message}")
                    null
                }

                if (text.isNullOrBlank()) {
                    Log.w("TripViewModel", "Received empty or invalid response from Gemini")
                    _error.value = "Unable to generate trip right now. Please try again later."
                } else {
                    _itineraryList.value = parseItinerary(text)
                }

            } catch (e: Exception) {
                handleSafeFailure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Generates tourist place and hotel recommendations using a strict, token-efficient prompt.
     */
    fun generateRecommendations(
        destination: String,
        style: String,
        budget: String
    ) {
        val prompt = """
            You are a travel recommendation assistant.
            Keep the response concise and token-efficient. Use bullet points only.
            NO markdown. NO emojis. NO extra explanations. NO repetition. NO long sentences.

            INPUT:
            Destination: $destination
            Travel style: $style
            Budget: $budget

            OUTPUT FORMAT (STRICT):

            1. TOURIST PLACES (MAX 5)
            - Place name – short reason (max 6 words)

            2. HOTEL RECOMMENDATIONS (MAX 3)
            - [Hotel name], [Area/location], [Price range (Low / Medium / High)]

            RULES:
            - Use real, well-known tourist places
            - Use realistic hotel names
            - Keep lines short
            - No explanations
            - No paragraphs
            - No extra sections
        """.trimIndent()

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                _itineraryList.value = emptyList()

                if (apiKey.isBlank()) {
                    Log.e("TripViewModel", "Missing GEMINI_API_KEY in BuildConfig")
                    _error.value = "Unable to generate recommendations right now."
                    return@launch
                }

                Log.d("TripViewModel", "Generating recommendations for $destination")
                
                val response = model.generateContent(prompt)
                val text = try {
                    response.text
                } catch (e: Exception) {
                    null
                }

                if (text.isNullOrBlank()) {
                    _error.value = "Unable to fetch recommendations. Please try again later."
                } else {
                    _itineraryList.value = parseRecommendations(text)
                }

            } catch (e: Exception) {
                handleSafeFailure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Maps technical exceptions to user-friendly messages.
     */
    private fun handleSafeFailure(e: Exception) {
        Log.e("TripViewModel", "Gemini AI Failure: ${e.message}", e)

        val userMessage = when {
            e.message?.contains("404") == true || e.message?.contains("not found") == true ->
                "AI service is temporarily updating. Please try again later."
            
            e is ResponseStoppedException -> "Content generation was stopped for safety reasons."
            e is ServerException -> "AI service is temporarily unavailable. Please try again later."
            e is PromptBlockedException -> "This request cannot be fulfilled due to safety restrictions."
            else -> "Unable to generate trip right now. Please try again later."
        }

        _error.value = userMessage
    }

    /**
     * Optimized parsing for the trip plan structured sections.
     */
    private fun parseItinerary(text: String): List<String> {
        return try {
            val regex = Regex("(?i)(1\\. TRIP SUMMARY|Day \\d+:|3\\. HOTEL SUGGESTIONS|4\\. FOOD TIP)")
            val markers = regex.findAll(text).map { it.range.first }.toList()
            
            if (markers.isEmpty()) return listOf(text)

            val parts = mutableListOf<String>()
            for (i in markers.indices) {
                val start = markers[i]
                val end = if (i + 1 < markers.size) markers[i + 1] else text.length
                parts.add(text.substring(start, end).trim())
            }
            parts
        } catch (e: Exception) {
            Log.e("TripViewModel", "Error parsing itinerary text", e)
            listOf(text)
        }
    }

    /**
     * Optimized parsing for the recommendations structured sections.
     */
    private fun parseRecommendations(text: String): List<String> {
        return try {
            val regex = Regex("(?i)(1\\. TOURIST PLACES|2\\. HOTEL RECOMMENDATIONS)")
            val markers = regex.findAll(text).map { it.range.first }.toList()
            
            if (markers.isEmpty()) return listOf(text)

            val parts = mutableListOf<String>()
            for (i in markers.indices) {
                val start = markers[i]
                val end = if (i + 1 < markers.size) markers[i + 1] else text.length
                parts.add(text.substring(start, end).trim())
            }
            parts
        } catch (e: Exception) {
            Log.e("TripViewModel", "Error parsing recommendations", e)
            listOf(text)
        }
    }
}
