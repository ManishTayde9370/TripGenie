package com.example.tripgenie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class TripFragment : Fragment() {

    private lateinit var placeInput: EditText
    private lateinit var daysInput: EditText
    private lateinit var budgetInput: EditText
    private lateinit var travelersInput: EditText
    private lateinit var generateButton: Button
    private lateinit var itineraryRecyclerView: RecyclerView
    private lateinit var adapter: ItineraryAdapter
    private val itineraryList = mutableListOf<String>()

    private val apiKey = "AIzaSyAbYJWDPqpYoXrxcpRInN51DygIGyzxvEE"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trip, container, false)

        placeInput = view.findViewById(R.id.placeInput)
        daysInput = view.findViewById(R.id.daysInput)
        budgetInput = view.findViewById(R.id.budgetInput)
        travelersInput = view.findViewById(R.id.travelersInput)
        generateButton = view.findViewById(R.id.generateButton)
        itineraryRecyclerView = view.findViewById(R.id.itineraryRecyclerView)

        adapter = ItineraryAdapter(itineraryList)
        itineraryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        itineraryRecyclerView.adapter = adapter

        generateButton.setOnClickListener {
            val place = placeInput.text.toString().trim()
            val days = daysInput.text.toString().trim()
            val travelers = travelersInput.text.toString().trim()
            val budget = budgetInput.text.toString().trim() // still collected if you show it in UI later

            if (place.isEmpty() || days.isEmpty() || budget.isEmpty() || travelers.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                generateTripPlan(place, days, travelers)
            }
        }

        return view
    }

    private fun generateTripPlan(place: String, days: String, travelers: String) {
        val model = GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = apiKey
        )

        val prompt = """
            Create a day-wise itinerary only (no tips, no budget, no hotels) 
            for $travelers travelers visiting $place for $days days.
            Use the format:
            Day 1: ...
            Day 2: ...
        """.trimIndent()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                itineraryList.clear()
                adapter.notifyDataSetChanged()

                Toast.makeText(requireContext(), "Generating itinerary...", Toast.LENGTH_SHORT).show()

                val response = model.generateContent(prompt)
                val text = response.text ?: "No response received."

                val lines = text.split("\n").filter { it.isNotBlank() }
                var currentDay = ""
                val formatted = mutableListOf<String>()

                for (line in lines) {
                    if (line.startsWith("Day", ignoreCase = true)) {
                        if (currentDay.isNotEmpty()) formatted.add(currentDay)
                        currentDay = line
                    } else {
                        currentDay += "\n$line"
                    }
                }
                if (currentDay.isNotEmpty()) formatted.add(currentDay)

                itineraryList.addAll(formatted)
                adapter.notifyDataSetChanged()

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
