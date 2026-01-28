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
                        // Assuming the first match is the relevant city data
                        val cityData = data[0]
                        
                        // Example mapping logic
                        safetyScore.value = (cityData.safety_score ?: "0").toFloat() / 100f
                        
                        // Mocking alerts based on score or additional sheet fields if they exist
                        // For now, we use the sheet data to populate specific alerts
                        alerts.add(SafetyAlertItem("Local Safety Level", "City: ${cityData.city}, Level: ${cityData.safety_level}", "Medium"))
                        
                        if ((cityData.safety_score?.toInt() ?: 100) < 50) {
                            alerts.add(SafetyAlertItem("Caution Advised", "Safety score is currently low for this area.", "High"))
                        }
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
