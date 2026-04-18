package com.manish.tripgenie

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ProfileFragment : Fragment() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val userName = view.findViewById<TextView>(R.id.userName)
        val userEmail = view.findViewById<TextView>(R.id.userEmail)
        val editName = view.findViewById<TextInputEditText>(R.id.etEditName)
        val editEmail = view.findViewById<TextInputEditText>(R.id.etEditEmail)
        val btnSaveProfile = view.findViewById<MaterialButton>(R.id.btnSaveProfile)
        val logoutButton = view.findViewById<MaterialButton>(R.id.logoutButton)

        // Use singleton instance
        val session = SessionManager.getInstance(requireContext())

        // Display current user info (Prioritize Firebase)
        val currentUser = authViewModel.user.value
        if (currentUser != null) {
            userName.text = currentUser.displayName ?: session.getUserName() ?: "Traveler"
            userEmail.text = currentUser.email ?: session.getUserEmail() ?: "No Email Found"
        } else {
            userName.text = session.getUserName() ?: "Traveler"
            userEmail.text = session.getUserEmail() ?: "No Email Found"
        }

        editName.setText(userName.text)
        editEmail.setText(userEmail.text)

        // Save new info
        btnSaveProfile.setOnClickListener {
            val newName = editName.text.toString().trim()
            val newEmail = editEmail.text.toString().trim()

            if (newName.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill both fields", Toast.LENGTH_SHORT).show()
            } else {
                // Keep local session in sync
                session.saveUser(newName, newEmail)
                userName.text = newName
                userEmail.text = newEmail
                Toast.makeText(requireContext(), "Profile updated locally!", Toast.LENGTH_SHORT).show()
            }
        }

        // Logout workflow
        logoutButton.setOnClickListener {
            authViewModel.logout()
            session.clearSession()
            Toast.makeText(requireContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return view
    }
}
