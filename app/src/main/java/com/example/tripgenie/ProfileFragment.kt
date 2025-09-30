package com.example.tripgenie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Example: Logout button click
        val logoutButton: Button = view.findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            Toast.makeText(requireContext(), "Logged out!", Toast.LENGTH_SHORT).show()
            // TODO: Clear user session & navigate to login screen
        }

        return view
    }
}
