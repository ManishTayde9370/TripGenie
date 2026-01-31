package com.example.tripgenie.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tripgenie.network.FlightOfferUnified
import com.example.tripgenie.ui.theme.GradientStart
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightComparisonScreen(
    onBack: () -> Unit,
    onNavigateToDetails: (FlightOfferUnified) -> Unit,
    viewModel: FlightViewModel = viewModel()
) {
    val cityNames = viewModel.getCityNames()
    
    var fromCity by remember { mutableStateOf("Delhi") }
    var toCity by remember { mutableStateOf("Mumbai") }
    var date by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var adults by remember { mutableStateOf("1") }

    var showFromMenu by remember { mutableStateOf(false) }
    var showToMenu by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis() - 86400000 // Block past dates
            }
        }
    )

    val flights = viewModel.flightOffers
    val isLoading by viewModel.isLoading
    val error by viewModel.error
    val context = LocalContext.current

    LaunchedEffect(error) {
        error?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        date = sdf.format(Date(millis))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compare Flights") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GradientStart,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // User-Friendly Search Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        // From City Selector
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = fromCity,
                                onValueChange = {},
                                label = { Text("From") },
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = { Icon(Icons.Default.FlightTakeoff, null) },
                                trailingIcon = {
                                    IconButton(onClick = { showFromMenu = true }) {
                                        Icon(Icons.Default.KeyboardArrowDown, null)
                                    }
                                }
                            )
                            DropdownMenu(
                                expanded = showFromMenu,
                                onDismissRequest = { showFromMenu = false }
                            ) {
                                cityNames.forEach { name ->
                                    DropdownMenuItem(
                                        text = { Text(name) },
                                        onClick = {
                                            fromCity = name
                                            showFromMenu = false
                                        }
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))

                        // To City Selector
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = toCity,
                                onValueChange = {},
                                label = { Text("To") },
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    IconButton(onClick = { showToMenu = true }) {
                                        Icon(Icons.Default.KeyboardArrowDown, null)
                                    }
                                }
                            )
                            DropdownMenu(
                                expanded = showToMenu,
                                onDismissRequest = { showToMenu = false }
                            ) {
                                cityNames.forEach { name ->
                                    DropdownMenuItem(
                                        text = { Text(name) },
                                        onClick = {
                                            toCity = name
                                            showToMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Date Picker Field
                        OutlinedTextField(
                            value = date,
                            onValueChange = {},
                            label = { Text("Departure Date") },
                            readOnly = true,
                            modifier = Modifier
                                .weight(1.5f)
                                .clickable { showDatePicker = true },
                            leadingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(Icons.Default.Today, null)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = adults,
                            onValueChange = { adults = it },
                            label = { Text("Adults") },
                            modifier = Modifier.weight(0.5f),
                            leadingIcon = { Icon(Icons.Default.Person, null) }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { 
                            viewModel.searchFlights(fromCity, toCity, date, adults.toIntOrNull() ?: 1)
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GradientStart),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Compare near real-time flight prices")
                        }
                    }
                }
            }

            // Results Section
            if (flights.isEmpty() && !isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Search to compare multiple airlines", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            "Flight Offers in INR (₹) from industry-standard travel API",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(flights) { flight ->
                        FlightCard(flight) {
                            viewModel.selectedFlight = flight
                            onNavigateToDetails(flight)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlightCard(flight: FlightOfferUnified, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = flight.airlineName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(text = "${flight.originCity} → ${flight.destinationCity}", fontSize = 12.sp, color = Color.Gray)
                }
                Text(
                    text = "${flight.currency} ${flight.price}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = GradientStart
                )
            }
            
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem("Departure", flight.departureTime)
                InfoItem("Duration", flight.duration)
                
                flight.label?.let {
                    Surface(
                        color = when {
                            it.contains("Cheapest") -> Color(0xFFE8F5E9)
                            it.contains("Fastest") -> Color(0xFFE3F2FD)
                            else -> Color(0xFFFFF3E0)
                        },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = it,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                it.contains("Cheapest") -> Color(0xFF2E7D32)
                                it.contains("Fastest") -> Color(0xFF1976D2)
                                else -> Color(0xFFE65100)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 11.sp, color = Color.Gray)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}
