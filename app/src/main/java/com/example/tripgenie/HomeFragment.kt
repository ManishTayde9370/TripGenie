package com.example.tripgenie

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.eventapi.EventsFragment
import com.google.android.material.button.MaterialButton
import java.util.Calendar

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val btnEvents = view.findViewById<MaterialButton>(R.id.btnEvents)
        val btnSafety = view.findViewById<MaterialButton>(R.id.btnSafety)
        val welcomeText = view.findViewById<TextView>(R.id.welcomeText)

        // 🧠 Fetch user name
        val session = SessionManager(requireContext())
        val userName = session.getUserName() ?: "Traveler"

        // ⏰ Get time-based greeting
        val greeting = getGreetingMessage()
        welcomeText.text = "$greeting, $userName!"

//        btnSafety.setOnClickListener {
//            parentFragmentManager.beginTransaction()
//                .setCustomAnimations(
//                    R.anim.slide_in_right,
//                    R.anim.slide_out_left,
//                    R.anim.slide_in_left,
//                    R.anim.slide_out_right
//                )
//                .replace(R.id.fragmentContainer, SafetyActivity())
//                .addToBackStack(null)
//                .commit()
//        }
        btnSafety.setOnClickListener {
            val intent = Intent(requireContext(), SafetyActivity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }


        btnEvents.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.fragmentContainer, EventsFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun getGreetingMessage(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }
}
