package com.example.tripgenie.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Home : Screen("home")
    object TripPlanner : Screen("trip_planner?city={city}") {
        fun createRoute(city: String? = null) = if (city != null) "trip_planner?city=$city" else "trip_planner"
    }
    object Safety : Screen("safety")
    object LanguageAssistant : Screen("language_assistant")
    object Events : Screen("events")
    object GreenTravel : Screen("green_travel")
    object Feedback : Screen("feedback")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object TouristDiscovery : Screen("tourist_discovery")
    object FlightComparison : Screen("flight_comparison")
    object FlightDetails : Screen("flight_details")
    object HotelComparison : Screen("hotel_comparison")
    object HotelDetails : Screen("hotel_details")
}
