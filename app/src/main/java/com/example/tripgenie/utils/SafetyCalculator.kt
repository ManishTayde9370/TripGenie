package com.example.tripgenie.utils

object SafetyCalculator {


    fun weatherRisk(weather: String): Int {
        return when (weather.lowercase()) {
            "storm" -> 80
            "rain" -> 50
            "cloudy" -> 30
            else -> 10
        }
    }
}