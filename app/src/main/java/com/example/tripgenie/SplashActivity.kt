package com.example.tripgenie

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Use singleton instance
        val sessionManager = SessionManager.getInstance(this)

        Handler(Looper.getMainLooper()).postDelayed({
            // Improved logic: Check if user is already logged in
            val targetActivity = if (sessionManager.getUserEmail() != null) {
                MainActivity::class.java
            } else {
                WelcomeActivity::class.java
            }
            
            val intent = Intent(this, targetActivity)
            startActivity(intent)
            finish()
        }, 2000) // Reduced delay for better UX
    }
}
