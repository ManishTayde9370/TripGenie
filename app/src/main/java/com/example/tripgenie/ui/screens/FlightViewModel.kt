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
    
    // For navigation argument passing (or simple state holder)
    var selectedFlight: FlightOfferUnified? = null

    // MANDATORY: Local mapping of city names to IATA codes
    private val cityToIataMap = mapOf(
        "Delhi" to "DEL",
        "Mumbai" to "BOM",
        "Paris" to "CDG",
        "Dubai" to "DXB",
        "London" to "LHR",
        "Tokyo" to "HND",
        "New York" to "JFK",
        "Singapore" to "SIN",
        "Bangalore" to "BLR",
        "Chennai" to "MAA"
    )

    fun getCityNames() = cityToIataMap.keys.toList()

    /**
     * Search and compare near real-time flight prices in INR from multiple airlines
     */
    fun searchFlights(fromCity: String, toCity: String, date: String, adults: Int) {
        val originIata = cityToIataMap[fromCity]
        val destinationIata = cityToIataMap[toCity]

        if (originIata == null || destinationIata == null) {
            error.value = "Invalid city selection."
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            flightOffers.clear()

            val results = flightRepository.searchFlights(
                originIata, fromCity, 
                destinationIata, toCity, 
                date, adults
            )
            
            if (results.isNotEmpty()) {
                flightOffers.addAll(results)
            } else {
                error.value = "No flight offers found for these dates."
            }
            isLoading.value = false
        }
    }
}
