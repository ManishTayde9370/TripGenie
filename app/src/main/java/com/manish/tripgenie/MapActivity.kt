package com.manish.tripgenie

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.manish.tripgenie.databinding.ActivityMapBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var destinationLatLng: LatLng? = null
    private var currentPolyline: Polyline? = null
    private var destinationMarker: Marker? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                enableMyLocation()
            } else {
                Log.w("MapActivity", "Location permission denied by user")
                Toast.makeText(this, "Location permission is required for full features", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("MAP_DEBUG", "onCreate: Package: $packageName")
        Log.d("MAP_DEBUG", "onCreate: Starting MapActivity")

        // 1. Check Google Play Services
        if (!isGooglePlayServicesAvailable()) return

        // 2. Initialize Places API safely
        val apiKey = getString(R.string.google_maps_key)
        if (apiKey.isEmpty() || apiKey.startsWith("YOUR_")) {
            Log.e("MAP_DEBUG", "FATAL: API Key is missing or default in local.properties")
            showFallbackUI("Maps API Key is missing. Check setup.")
        } else {
            try {
                if (!Places.isInitialized()) {
                    Places.initialize(applicationContext, apiKey)
                }
            } catch (e: Exception) {
                Log.e("MAP_DEBUG", "Places initialization failed: ${e.message}")
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_container) as SupportMapFragment?
        
        if (mapFragment != null) {
            mapFragment.getMapAsync(this)
        } else {
            Log.e("MAP_DEBUG", "SupportMapFragment is null")
            showFallbackUI("Internal Error: Map container not found.")
        }

        setupAutocomplete()

        binding.fabGetRoute.setOnClickListener { getRoute() }
        binding.btnRetry.setOnClickListener { 
            Log.d("MAP_DEBUG", "Retry clicked")
            finish()
            startActivity(intent)
        }
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val availability = GoogleApiAvailability.getInstance()
        val status = availability.isGooglePlayServicesAvailable(this)
        if (status != ConnectionResult.SUCCESS) {
            if (availability.isUserResolvableError(status)) {
                availability.getErrorDialog(this, status, 2404)?.show()
            } else {
                Toast.makeText(this, "Google Play Services not supported.", Toast.LENGTH_LONG).show()
                finish()
            }
            return false
        }
        return true
    }

    private fun setupAutocomplete() {
        val autocompleteFragment = supportFragmentManager
            .findFragmentById(R.id.autocomplete_fragment) as? AutocompleteSupportFragment

        autocompleteFragment?.apply {
            setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
            setHint("Search for destination...")
            setOnPlaceSelectedListener(object : PlaceSelectionListener {
                override fun onPlaceSelected(place: Place) {
                    place.latLng?.let { latLng ->
                        updateDestination(latLng, place.name ?: "Destination")
                    }
                }
                override fun onError(status: com.google.android.gms.common.api.Status) {
                    Log.e("MAP_DEBUG", "Autocomplete Error: $status")
                }
            })
        }
    }

    private fun updateDestination(latLng: LatLng, title: String) {
        if (!::mMap.isInitialized) return
        destinationLatLng = latLng
        mMap.apply {
            destinationMarker?.remove()
            destinationMarker = addMarker(MarkerOptions().position(latLng).title(title))
            animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
        currentPolyline?.remove()
        binding.routeDetailsCard.visibility = View.GONE
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("MAP_DEBUG", "Map successfully loaded and ready.")
        mMap = googleMap
        
        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isMyLocationButtonEnabled = true
        }
        
        checkLocationPermission()
        
        // Default position
        val defaultLoc = LatLng(28.6139, 77.2090)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, 10f))
    }

    private fun showFallbackUI(message: String) {
        binding.fallbackLayout.visibility = View.VISIBLE
        binding.tvFallbackMessage.text = message
    }

    private fun getRoute() {
        if (destinationLatLng == null) {
            Toast.makeText(this, "Please search for a destination first", Toast.LENGTH_SHORT).show()
            return
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                fetchDirections(LatLng(location.latitude, location.longitude), destinationLatLng!!)
            } else {
                Toast.makeText(this, "Turn on GPS to get your position", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun fetchDirections(origin: LatLng, dest: LatLng) {
        val apiKey = getString(R.string.google_maps_key)
        val url = "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${dest.latitude},${dest.longitude}&key=$apiKey"

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                val data = response.body?.string()
                withContext(Dispatchers.Main) {
                    if (data != null) parseDirections(data)
                }
            } catch (e: Exception) {
                Log.e("MAP_DEBUG", "Route Fetch Error: ${e.message}")
            }
        }
    }

    private fun parseDirections(jsonData: String) {
        try {
            val root = JSONObject(jsonData)
            if (root.getString("status") != "OK") {
                Log.e("MAP_DEBUG", "Directions status: ${root.getString("status")}")
                return
            }

            val route = root.getJSONArray("routes").getJSONObject(0)
            val leg = route.getJSONArray("legs").getJSONObject(0)
            
            binding.apply {
                tvDistance.text = "Distance: ${leg.getJSONObject("distance").getString("text")}"
                tvDuration.text = "Time: ${leg.getJSONObject("duration").getString("text")}"
                routeDetailsCard.visibility = View.VISIBLE
            }

            val path = PolyUtil.decode(route.getJSONObject("overview_polyline").getString("points"))
            currentPolyline?.remove()
            currentPolyline = mMap.addPolyline(PolylineOptions().addAll(path).width(12f).color(Color.BLUE).geodesic(true))

            val bounds = LatLngBounds.Builder().apply { path.forEach { include(it) } }.build()
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150))
        } catch (e: Exception) {
            Log.e("MAP_DEBUG", "Parse Error: ${e.message}")
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        }
    }
}
