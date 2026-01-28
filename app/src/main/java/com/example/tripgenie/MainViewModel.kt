package com.example.tripgenie

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.tripgenie.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager.getInstance(application)

    private val _startDestination = MutableStateFlow<String>(Screen.Splash.route)
    val startDestination: StateFlow<String> = _startDestination

    fun calculateNextDestination() {
        val nextRoute = when {
            !sessionManager.isOnboardingCompleted() -> Screen.Onboarding.route
            !sessionManager.isLoggedIn() -> Screen.Login.route
            else -> Screen.Home.route
        }
        _startDestination.value = nextRoute
    }
}
