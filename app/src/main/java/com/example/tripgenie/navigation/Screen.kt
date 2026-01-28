package com.example.tripgenie.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Home : Screen("home")
    object TripPlanner : Screen("trip_planner")
    object Safety : Screen("safety")
    object LanguageAssistant : Screen("language_assistant")
    object Events : Screen("events")
    object GreenTravel : Screen("green_travel")
    object Feedback : Screen("feedback")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}
