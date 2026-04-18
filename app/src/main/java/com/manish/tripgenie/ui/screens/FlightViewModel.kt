package com.manish.tripgenie.ui.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manish.tripgenie.network.AmadeusService
import com.manish.tripgenie.network.FlightOfferUnified
import com.manish.tripgenie.network.FlightRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FlightViewModel : ViewModel() {
    private val amadeusService = AmadeusService()
    private val flightRepository = FlightRepository(amadeusService)

    val flightOffers = mutableStateListOf<FlightOfferUnified>()
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)
    
    var selectedFlight: FlightOfferUnified? = null

    /**
     * Returns a list of common city names for selection.
     */
    fun getCityNames(): List<String> {
        return listOf(
            "Delhi", "Mumbai", "Bangalore", "Hyderabad", "Chennai", 
            "Kolkata", "Goa", "Pune", "Ahmedabad", "Jaipur", 
            "New York", "London", "Dubai", "Singapore", "Paris"
        )
    }

    /**
     * Search and compare near real-time flight prices with dynamic city to IATA lookup
     */
    fun searchFlights(fromCity: String, toCity: String, date: String, adults: Int) {
        if (fromCity.isBlank() || toCity.isBlank()) {
            error.value = "Please enter both origin and destination."
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            flightOffers.clear()

            // 🚀 Task 5: Automatic City -> IATA conversion
            val originIata = flightRepository.getIataCode(fromCity)
            val destinationIata = flightRepository.getIataCode(toCity)

            if (originIata == null || destinationIata == null) {
                error.value = "Invalid city selection or location not found."
                isLoading.value = false
                return@launch
            }

            // 🚀 Task 6: Network call on IO
            val results = withContext(Dispatchers.IO) {
                flightRepository.searchFlights(
                    originIata, fromCity,
                    destinationIata, toCity,
                    date, adults
                )
            }
            
            if (results.isNotEmpty()) {
                flightOffers.addAll(results)
            } else {
                error.value = "No flight offers found for these dates."
            }
            isLoading.value = false
        }
    }
}
