package com.example.tripgenie

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Optimized SessionManager using standard SharedPreferences for high performance.
 */
class SessionManager private constructor(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    // Observable theme state
    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("dark_mode", false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    companion object {
        @Volatile
        private var INSTANCE: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SessionManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    fun saveUser(name: String, email: String) {
        prefs.edit().apply {
            putString("user_name", name)
            putString("user_email", email)
            apply()
        }
    }

    fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean("dark_mode", enabled).apply()
        _isDarkMode.value = enabled
    }

    fun setLanguage(language: String) {
        prefs.edit().putString("language", language).apply()
    }

    fun getLanguage(): String = prefs.getString("language", "English") ?: "English"

    fun getUserName(): String? = prefs.getString("user_name", null)
    fun getUserEmail(): String? = prefs.getString("user_email", null)

    fun isLoggedIn(): Boolean = getUserEmail() != null

    fun setOnboardingCompleted() {
        prefs.edit().putBoolean("onboarding_done", true).apply()
    }

    fun isOnboardingCompleted(): Boolean = prefs.getBoolean("onboarding_done", false)

    fun clearSession() {
        prefs.edit().remove("user_name").remove("user_email").apply()
    }
}
