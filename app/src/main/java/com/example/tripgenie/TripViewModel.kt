package com.example.tripgenie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class TripViewModel : ViewModel() {

    private val _itineraryList = MutableLiveData<List<String>>(emptyList())
    val itineraryList: LiveData<List<String>> = _itineraryList

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val apiKey = "AIzaSyAbYJWDPqpYoXrxcpRInN51DygIGyzxvEE"
    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    fun generateTripPlan(place: String, days: String, travelers: String) {
        val prompt = """
            Create a day-wise itinerary only (no tips, no budget, no hotels) 
            for $travelers travelers visiting $place for $days days.
            Use the format:
            Day 1: ...
            Day 2: ...
        """.trimIndent()

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val response = model.generateContent(prompt)
                val text = response.text ?: "No response received."

                val lines = text.split("\n").filter { it.isNotBlank() }
                var currentDay = ""
                val formatted = mutableListOf<String>()

                for (line in lines) {
                    if (line.startsWith("Day", ignoreCase = true)) {
                        if (currentDay.isNotEmpty()) formatted.add(currentDay)
                        currentDay = line
                    } else {
                        currentDay += "\n$line"
                    }
                }
                if (currentDay.isNotEmpty()) formatted.add(currentDay)

                _itineraryList.value = formatted
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
