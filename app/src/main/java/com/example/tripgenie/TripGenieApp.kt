package com.example.tripgenie

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class TripGenieApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        
        // Warm up SessionManager (and EncryptedSharedPreferences) in a background thread
        // to avoid blocking the main thread during the first frame of MainActivity.
        applicationScope.launch {
            try {
                SessionManager.getInstance(this@TripGenieApp)
            } catch (e: Exception) {
                // Log or handle initialization error
            }
        }
    }
}
