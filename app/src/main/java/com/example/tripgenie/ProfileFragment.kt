package com.example.tripgenie

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import android.widget.TextView

class ProfileFragment : Fragment() {

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

        val session = SessionManager(requireContext())

        // Display current user info
        userName.text = session.getUserName() ?: "Traveler"
        userEmail.text = session.getUserEmail() ?: "No Email Found"

        editName.setText(session.getUserName())
        editEmail.setText(session.getUserEmail())

        // Save new info
        btnSaveProfile.setOnClickListener {
            val newName = editName.text.toString().trim()
            val newEmail = editEmail.text.toString().trim()

            if (newName.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill both fields", Toast.LENGTH_SHORT).show()
            } else {
                session.saveUser(newName, newEmail)
                userName.text = newName
                userEmail.text = newEmail
                Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            }
        }

        // Logout workflow
        logoutButton.setOnClickListener {
            session.clearSession()
            Toast.makeText(requireContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return view
    }
}
