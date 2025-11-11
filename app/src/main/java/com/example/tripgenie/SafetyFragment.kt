package com.example.tripgenie

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class SafetyFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var tvWeather: TextView
    private lateinit var tvCrime: TextView
    private lateinit var tvAccident: TextView
    private lateinit var tvScore: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnRefresh: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_safety, container, false)

        tvWeather = view.findViewById(R.id.tvWeather)
        tvCrime = view.findViewById(R.id.tvCrime)
        tvAccident = view.findViewById(R.id.tvAccident)
        tvScore = view.findViewById(R.id.tvSafetyScore)
        progressBar = view.findViewById(R.id.progressBar)
        btnRefresh = view.findViewById(R.id.btnRefresh)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        btnRefresh.setOnClickListener {
            fetchSafetyData()
        }

        fetchSafetyData()
        return view
    }

    private fun fetchSafetyData() {
        progressBar.visibility = View.VISIBLE

        // Step 1: Get current location
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            progressBar.visibility = View.GONE
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                showSafetyReport(location)
            } else {
                Toast.makeText(requireContext(), "Unable to get location", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun showSafetyReport(location: Location) {
        // Step 2: Dummy Weather Data (replace with API later)
        val weatherTemp = Random.nextInt(20, 45)
        val weatherDesc = if (weatherTemp > 38) "🔥 Very Hot" else if (weatherTemp < 25) "🌧 Rainy" else "☀️ Pleasant"

        // Step 3: Dummy Crime Dataset (JSON format)
        val crimeData = """
            [
              {"city":"Mumbai","count":120},
              {"city":"Delhi","count":180},
              {"city":"Goa","count":45},
              {"city":"Shillong","count":30}
            ]
        """.trimIndent()
        val crimes = JSONArray(crimeData)
        val selectedCrime = crimes.getJSONObject(Random.nextInt(crimes.length()))
        val crimeCount = selectedCrime.getInt("count")

        // Step 4: Dummy Accident Dataset (JSON format)
        val accidentData = """
            [
              {"city":"Mumbai","severity":60},
              {"city":"Delhi","severity":80},
              {"city":"Goa","severity":20},
              {"city":"Shillong","severity":15}
            ]
        """.trimIndent()
        val accidents = JSONArray(accidentData)
        val selectedAccident = accidents.getJSONObject(Random.nextInt(accidents.length()))
        val severity = selectedAccident.getInt("severity")


        var score = 100
        if (weatherTemp > 38) score -= 20
        score -= (crimeCount / 10)
        score -= (severity / 5)
        score = max(0, min(score, 100))


        progressBar.visibility = View.GONE
        tvWeather.text = "Weather: $weatherDesc ($weatherTemp°C)"
        tvCrime.text = "Crime: ${selectedCrime.getString("city")} - $crimeCount incidents"
        tvAccident.text = "Accidents: ${selectedAccident.getString("city")} - Severity Index $severity"
        tvScore.text = "Safety Score: $score / 100"
    }
}
