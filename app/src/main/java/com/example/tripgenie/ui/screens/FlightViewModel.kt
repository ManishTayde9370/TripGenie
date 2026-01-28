package com.example.tripgenie.ui.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripgenie.network.AmadeusService
import com.example.tripgenie.network.FlightOfferUnified
import com.example.tripgenie.network.FlightRepository
import kotlinx.coroutines.launch

class FlightViewModel : ViewModel() {
    private val amadeusService = AmadeusService()
    private val flightRepository = FlightRepository(amadeusService)

    val flightOffers = mutableStateListOf<FlightOfferUnified>()
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    /**
     * Search and compare near real-time flight prices from multiple airlines
     */
    fun searchFlights(origin: String, destination: String, date: String, adults: Int) {
        if (origin.length != 3 || destination.length != 3) {
            error.value = "Please use 3-letter IATA codes (e.g., DEL, BOM)"
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            flightOffers.clear()

            val results = flightRepository.searchFlights(origin, destination, date, adults)
            
            if (results.isNotEmpty()) {
                flightOffers.addAll(results)
            } else {
                error.value = "No flight offers found for these dates."
            }
            isLoading.value = false
        }
    }
}
