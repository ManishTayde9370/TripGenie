package com.manish.tripgenie

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class SignupActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val name = findViewById<TextInputEditText>(R.id.etSignupName)
        val email = findViewById<TextInputEditText>(R.id.etSignupEmail)
        val password = findViewById<TextInputEditText>(R.id.etSignupPassword)
        val confirmPassword = findViewById<TextInputEditText>(R.id.etSignupConfirmPassword)
        val btnSignup = findViewById<MaterialButton>(R.id.btnSignup)
        val tvLoginRedirect = findViewById<TextView>(R.id.tvLoginRedirect)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        // Use singleton instance
        val sessionManager = SessionManager.getInstance(this)

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnSignup.isEnabled = !isLoading
        }

        viewModel.error.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loginSuccess.observe(this) { success ->
            if (success) {
                val userName = name.text.toString().trim()
                val userEmail = viewModel.user.value?.email ?: ""

                // Keep local session in sync
                sessionManager.saveUser(userName, userEmail)

                Toast.makeText(this, "Signup Successful!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        btnSignup.setOnClickListener {
            val userName = name.text.toString().trim()
            val userEmail = email.text.toString().trim()
            val userPass = password.text.toString().trim()
            val userConfirm = confirmPassword.text.toString().trim()

            when {
                userName.isEmpty() || userEmail.isEmpty() || userPass.isEmpty() || userConfirm.isEmpty() -> {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
                userPass != userConfirm -> {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    viewModel.signup(userEmail, userPass)
                }
            }
        }

        tvLoginRedirect.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
