package com.manish.tripgenie.ui.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manish.tripgenie.network.AmadeusService
import com.manish.tripgenie.network.HotelOfferUnified
import com.manish.tripgenie.network.HotelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HotelViewModel : ViewModel() {
    private val amadeusService = AmadeusService()
    private val hotelRepository = HotelRepository(amadeusService)

    val hotelOffers = mutableStateListOf<HotelOfferUnified>()
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)
    
    var selectedHotel: HotelOfferUnified? = null

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
     * Search hotels with dynamic city lookup and fallback images
     */
    fun searchHotels(cityName: String, checkIn: String, checkOut: String, adults: Int) {
        if (cityName.isBlank()) {
            error.value = "Enter a city name."
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            hotelOffers.clear()

            // 🚀 Task 2: Dynamic city code lookup
            val cityCode = hotelRepository.getCityCode(cityName)

            if (cityCode == null) {
                error.value = "Could not find code for $cityName."
                isLoading.value = false
                return@launch
            }

            // 🚀 Task 6: Network call on IO
            val results = withContext(Dispatchers.IO) {
                hotelRepository.searchHotels(cityCode, cityName, checkIn, checkOut, adults)
            }
            
            if (results.isNotEmpty()) {
                // 🚀 Task 6: UI updates on Main (automatic with LiveData/State)
                hotelOffers.addAll(results)
            } else {
                error.value = "No hotels found in $cityName."
            }
            isLoading.value = false
        }
    }
}
