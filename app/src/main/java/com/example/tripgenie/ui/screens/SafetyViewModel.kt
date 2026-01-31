package com.example.tripgenie.ui.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripgenie.network.SafetyApiService
import com.example.tripgenie.network.SheetDBClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SafetyAlertItem(
    val title: String,
    val description: String,
    val severity: String
)

class SafetyViewModel : ViewModel() {
    private val apiService = SheetDBClient.retrofit.create(SafetyApiService::class.java)

    val alerts = mutableStateListOf<SafetyAlertItem>()
    val safetyScore = mutableStateOf(0f)
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    fun fetchSafetyData(city: String) {
        isLoading.value = true
        error.value = null
        alerts.clear()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getCityData(city).execute()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                        val data = response.body()!!
                        val cityData = data[0]
                        
                        // 🧠 Logic: Calculate safety score from available sheet data
                        val crime = cityData.crime_rate.toIntOrNull() ?: 0
                        val accident = cityData.accident_rate.toIntOrNull() ?: 0
                        
                        // Simple inverse calculation: higher rates = lower safety score
                        val calculatedScore = (100 - (crime + accident) / 2).coerceIn(0, 100)
                        safetyScore.value = calculatedScore / 100f
                        
                        val level = when {
                            calculatedScore > 70 -> "High"
                            calculatedScore > 40 -> "Moderate"
                            else -> "Low"
                        }

                        alerts.add(SafetyAlertItem("Safety Level", "The current safety level is rated as $level.", "Medium"))
                        alerts.add(SafetyAlertItem("Crime Statistics", "Reported crime index for this area: $crime", if (crime > 50) "High" else "Medium"))
                        alerts.add(SafetyAlertItem("Road Safety", "Accident frequency index: $accident", if (accident > 50) "High" else "Medium"))

                    } else {
                        error.value = "No data found for $city"
                    }
                    isLoading.value = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    error.value = "Failed to fetch data: ${e.localizedMessage}"
                    isLoading.value = false
                }
            }
        }
    }
}
