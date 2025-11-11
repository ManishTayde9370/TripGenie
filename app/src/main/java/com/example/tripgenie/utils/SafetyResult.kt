package com.example.tripgenie.utils


data class SafetyResult(
    val temperature: Float,
    val crimeRate: Double,
    val accidentRate: Double,
    val safetyScore: Int,
    val safetyLevel: String
)