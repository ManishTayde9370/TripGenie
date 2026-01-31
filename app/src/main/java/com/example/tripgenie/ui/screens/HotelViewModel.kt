package com.example.tripgenie.ui.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripgenie.network.AmadeusService
import com.example.tripgenie.network.HotelOfferUnified
import com.example.tripgenie.network.HotelRepository
import kotlinx.coroutines.launch

class HotelViewModel : ViewModel() {
    private val amadeusService = AmadeusService()
    private val hotelRepository = HotelRepository(amadeusService)

    val hotelOffers = mutableStateListOf<HotelOfferUnified>()
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)
    
    var selectedHotel: HotelOfferUnified? = null

    // MANDATORY: Local mapping of city names to codes
    private val cityToCodeMap = mapOf(
        "Delhi" to "DEL",
        "Mumbai" to "BOM",
        "Goa" to "GOI",
        "Paris" to "PAR",
        "Dubai" to "DXB"
    )

    fun getCityNames() = cityToCodeMap.keys.toList()

    /**
     * Search hotels with specific dates and guests in INR using Amadeus APIs
     */
    fun searchHotels(cityName: String, checkIn: String, checkOut: String, adults: Int) {
        val cityCode = cityToCodeMap[cityName]
        if (cityCode == null) {
            error.value = "Invalid city selection."
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            hotelOffers.clear()

            val results = hotelRepository.searchHotels(cityCode, checkIn, checkOut, adults)
            
            if (results.isNotEmpty()) {
                hotelOffers.addAll(results)
            } else {
                error.value = "No hotels found for these dates in $cityName."
            }
            isLoading.value = false
        }
    }
}
