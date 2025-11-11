package com.example.tripgenie

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveUser(name: String, email: String) {
        prefs.edit().apply {
            putString("user_name", name)
            putString("user_email", email)
            apply()
        }
    }

    fun getUserName(): String? = prefs.getString("user_name", null)
    fun getUserEmail(): String? = prefs.getString("user_email", null)

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
