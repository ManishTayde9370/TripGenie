package com.example.tripgenie

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.*

class TripFragment : Fragment() {

    private val apiKey = "AIzaSyBx39i-ncFCJvmcjoJR3vfHCLyljZGEdIE" // Your Gemini API Key
    private val client = OkHttpClient()
    private lateinit var rvItinerary: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trip, container, false)

        val etDestination: EditText = view.findViewById(R.id.etDestination)
        val etStartDate: EditText = view.findViewById(R.id.etStartDate)
        val etEndDate: EditText = view.findViewById(R.id.etEndDate)
        val etTravelers: EditText = view.findViewById(R.id.etTravelers)
        val btnGenerate: MaterialButton = view.findViewById(R.id.btnGeneratePlan)
        rvItinerary = view.findViewById(R.id.rvItinerary)

        rvItinerary.layoutManager = LinearLayoutManager(requireContext())

        // Date pickers
        val calendar = Calendar.getInstance()
        etStartDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, day ->
                etStartDate.setText("$day/${month + 1}/$year")
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        etEndDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, day ->
                etEndDate.setText("$day/${month + 1}/$year")
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Button click
        btnGenerate.setOnClickListener {
            val destination = etDestination.text.toString()
            val start = etStartDate.text.toString()
            val end = etEndDate.text.toString()
            val travelers = etTravelers.text.toString()

            if (destination.isEmpty() || start.isEmpty() || end.isEmpty() || travelers.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                generateTripPlan(destination, start, end, travelers)
            }
        }

        return view
    }

    private fun generateTripPlan(destination: String, start: String, end: String, travelers: String) {
        val prompt = "Plan a ${travelers}-person trip to $destination from $start to $end. Include hotels, food, sightseeing, and safety tips in a Day 1, Day 2 format."

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url =
                    "https://generativelanguage.googleapis.com/v1/models/gemini-pro:generateContent?key=$apiKey"

                val jsonBody = """
                {
                  "contents": [
                    {
                      "parts": [
                        {"text": "$prompt"}
                      ]
                    }
                  ]
                }
                """.trimIndent()

                val body = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())

                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                val responseData = response.body?.string()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && responseData != null) {
                        val jsonResponse = JSONObject(responseData)
                        val outputText = jsonResponse
                            .getJSONArray("candidates")
                            .getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text")

                        // Parse into day-wise itinerary
                        val itineraryItems = mutableListOf<ItineraryItem>()
                        val lines = outputText.split("\n")
                        var currentDay = ""
                        val sb = StringBuilder()

                        for (line in lines) {
                            if (line.trim().startsWith("Day")) {
                                if (currentDay.isNotEmpty()) {
                                    itineraryItems.add(
                                        ItineraryItem(
                                            currentDay,
                                            sb.toString().trim()
                                        )
                                    )
                                    sb.clear()
                                }
                                currentDay = line.trim()
                            } else {
                                sb.appendLine(line.trim())
                            }
                        }
                        if (currentDay.isNotEmpty()) {
                            itineraryItems.add(
                                ItineraryItem(
                                    currentDay,
                                    sb.toString().trim()
                                )
                            )
                        }

                        rvItinerary.adapter = ItineraryAdapter(itineraryItems)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed: ${response.message}\n$responseData",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
