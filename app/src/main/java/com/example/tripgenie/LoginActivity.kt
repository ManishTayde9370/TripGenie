package com.example.tripgenie

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val email = findViewById<TextInputEditText>(R.id.etLoginEmail)
        val password = findViewById<TextInputEditText>(R.id.etLoginPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val tvSignupRedirect = findViewById<TextView>(R.id.tvSignupRedirect)

        val sessionManager = SessionManager(this)

        btnLogin.setOnClickListener {
            val userEmail = email.text.toString().trim()
            val userPassword = password.text.toString().trim()

            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                // ✅ (Temporary local logic) Assume login success
                // Later replace with Firebase/Backend verification

                // Get previously signed-up name, or fallback
                val userName = sessionManager.getUserName() ?: "Traveler"

                // ✅ Save active session again to ensure consistency
                sessionManager.saveUser(userName, userEmail)

                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        tvSignupRedirect.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}
