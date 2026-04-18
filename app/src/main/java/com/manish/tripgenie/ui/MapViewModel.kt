package com.manish.tripgenie.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manish.tripgenie.model.MapMarker
import com.manish.tripgenie.model.MarkerType
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel : ViewModel() {

    private val _markers = MutableLiveData<List<MapMarker>>(emptyList())
    val markers: LiveData<List<MapMarker>> = _markers

    private val _currentLocation = MutableLiveData<LatLng?>()
    val currentLocation: LiveData<LatLng?> = _currentLocation

    fun fetchTouristPlaces(city: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Simulated API call for tourist places
            val simulatedPlaces = listOf(
                MapMarker("1", "Eiffel Tower", "Iconic iron lattice tower", LatLng(48.8584, 2.2945), MarkerType.PLACE),
                MapMarker("2", "Louvre Museum", "World's largest art museum", LatLng(48.8606, 2.3376), MarkerType.PLACE)
            )
            withContext(Dispatchers.Main) {
                _markers.value = _markers.value?.plus(simulatedPlaces)
            }
        }
    }

    fun fetchHotels(city: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Simulated API call for hotels
            val simulatedHotels = listOf(
                MapMarker("h1", "Hotel Paris", "Luxury Stay", LatLng(48.8566, 2.3522), MarkerType.HOTEL, "$250/night"),
                MapMarker("h2", "City Inn", "Budget Friendly", LatLng(48.8534, 2.3488), MarkerType.HOTEL, "$90/night")
            )
            withContext(Dispatchers.Main) {
                _markers.value = _markers.value?.plus(simulatedHotels)
            }
        }
    }

    fun addItineraryLocations(locations: List<MapMarker>) {
        _markers.value = _markers.value?.plus(locations.map { it.copy(type = MarkerType.ITINERARY) })
    }

    fun updateCurrentLocation(latLng: LatLng) {
        _currentLocation.value = latLng
    }
}
