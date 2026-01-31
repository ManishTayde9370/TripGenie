package com.example.tripgenie




import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.tripgenie.model.SafetyData
import com.example.tripgenie.model.SheetSafetyData
import com.example.tripgenie.network.ApiClient
import com.example.tripgenie.network.SheetDBClient
import com.example.tripgenie.network.SafetyApiService
import com.example.tripgenie.network.WeatherResponse
import com.example.tripgenie.utils.SafetyCalculator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SafetyActivity : AppCompatActivity() {

    private val apiKey = "f17a1ba424f3ad9a36403d6c2c0607b6"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_safety)

        val inputCity = findViewById<EditText>(R.id.inputCity)
        val btnCheck = findViewById<Button>(R.id.btnCheckSafety)
        val textResult = findViewById<TextView>(R.id.textResult)

        btnCheck.setOnClickListener {
            val city = inputCity.text.toString().trim()
            if (city.isEmpty()) {
                textResult.text = "⚠ Please enter a city name."
                return@setOnClickListener
            }

            textResult.text = "Fetching safety data for $city..."


            ApiClient.instance.getWeather(city, apiKey)
                .enqueue(object : Callback<WeatherResponse> {
                    override fun onResponse(
                        call: Call<WeatherResponse>,
                        response: Response<WeatherResponse>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            val data = response.body()!!
                            val weather = data.weather[0].main
                            val temp = data.main.temp


                            val sheetService = SheetDBClient.retrofit.create(SafetyApiService::class.java)
                            sheetService.getCityData(city)
                                .enqueue(object : Callback<List<SheetSafetyData>> {
                                    override fun onResponse(
                                        call: Call<List<SheetSafetyData>>,
                                        response: Response<List<SheetSafetyData>>
                                    ) {
                                        if (response.isSuccessful && response.body() != null && response.body()!!.isNotEmpty()) {
                                            val sheetData = response.body()!![0]

                                            val crime = sheetData.crime_rate.toIntOrNull()
                                            val accidents = sheetData.accident_rate.toIntOrNull()
                                            if (crime != null && accidents != null) {
                                                calculateAndShowResult(city, weather, temp, crime, accidents, textResult)
                                            } else {
                                                textResult.text = """
                                                    🏙 City: $city
                                                    🌦 Weather: $weather (${temp}°C)
                                                    🚨 Crime Rate: Not Available
                                                    🚗 Accident Rate: Not Available
                                                    📊 Safety Score: Not Available
                                                    🛡 Level: Data Incomplete ⚠
                                                """.trimIndent()
                                            }
                                        } else {
                                            // ⚠ City not found in SheetDB → still show weather
                                            textResult.text = """
                                                🏙 City: $city
                                                🌦 Weather: $weather (${temp}°C)
                                                🚨 Crime Rate: Not Available
                                                🚗 Accident Rate: Not Available
                                                📊 Safety Score: Not Available
                                                🛡 Level: Data Not Available ⚠
                                            """.trimIndent()
                                        }
                                    }

                                    override fun onFailure(call: Call<List<SheetSafetyData>>, t: Throwable) {

                                        textResult.text = """
                                            🏙 City: $city
                                            🌦 Weather: $weather (${temp}°C)
                                            🚨 Crime Rate: Not Available
                                            🚗 Accident Rate: Not Available
                                            📊 Safety Score: Not Available
                                            🛡 Level: Unable to fetch data 🌐
                                        """.trimIndent()
                                    }
                                })

                        } else {
                            textResult.text = "❌ City not found or Weather API error."
                        }
                    }

                    override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                        textResult.text = "🚫 Failed to load weather data: ${t.message}"
                    }
                })
        }
    }

    private fun calculateAndShowResult(
        city: String,
        weather: String,
        temp: Double,
        crime: Int,
        accidents: Int,
        textResult: TextView
    ) {
        val weatherRisk = SafetyCalculator.weatherRisk(weather)
        val riskScore = (0.6 * crime) + (0.25 * accidents) + (0.15 * weatherRisk)
        val safetyScore = (100 - riskScore).toInt().coerceIn(0, 100)
        val level = when (safetyScore) {
            in 75..100 -> "Safe ✅"
            in 40..74 -> "Moderate ⚠"
            else -> "Unsafe ❌"
        }

        val safetyData = SafetyData(
            city = city,
            weatherCondition = weather,
            temperature = temp,
            crimeRate = crime,
            accidentRate = accidents,
            safetyScore = safetyScore,
            safetyLevel = level
        )

        textResult.text = """
            🏙 City: ${safetyData.city}
            🌦 Weather: ${safetyData.weatherCondition} (${safetyData.temperature}°C)
            🚨 Crime Rate: ${safetyData.crimeRate}
            🚗 Accident Rate: ${safetyData.accidentRate}
            🛡 Safety Score: ${safetyData.safetyScore}
            📊 Level: ${safetyData.safetyLevel}
        """.trimIndent()
    }
}