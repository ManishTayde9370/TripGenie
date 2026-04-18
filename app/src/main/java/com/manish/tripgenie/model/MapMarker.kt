package com.manish.tripgenie.model

import com.google.android.gms.maps.model.LatLng

enum class MarkerType {
    PLACE, HOTEL, ITINERARY
}

data class MapMarker(
    val id: String,
    val title: String,
    val snippet: String,
    val position: LatLng,
    val type: MarkerType,
    val price: String? = null
)
