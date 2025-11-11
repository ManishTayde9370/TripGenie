package com.example.tripgenie.model

data class SafetyData(
    val city: String,
    val weatherCondition: String,
    val temperature: Double,
    val crimeRate: Int,
    val accidentRate: Int,
    val safetyScore: Int,
    val safetyLevel: String
)