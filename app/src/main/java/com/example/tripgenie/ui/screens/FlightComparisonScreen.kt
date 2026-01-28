package com.example.tripgenie.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FlightTakeoff
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightComparisonScreen(
    onBack: () -> Unit,
    viewModel: FlightViewModel = viewModel()
) {
    var fromCode by remember { mutableStateOf("DEL") }
    var toCode by remember { mutableStateOf("BOM") }
    var date by remember { mutableStateOf("2024-12-01") }
    var adults by remember { mutableStateOf("1") }

    val flights = viewModel.flightOffers
    val isLoading by viewModel.isLoading
    val error by viewModel.error
    val context = LocalContext.current

    LaunchedEffect(error) {
        error?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
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
            // Search Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = fromCode,
                            onValueChange = { fromCode = it.uppercase() },
                            label = { Text("From (IATA)") },
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            leadingIcon = { Icon(Icons.Default.FlightTakeoff, null) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = toCode,
                            onValueChange = { toCode = it.uppercase() },
                            label = { Text("To (IATA)") },
                            modifier = Modifier.weight(1f),
                            maxLines = 1
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it },
                            label = { Text("Date (YYYY-MM-DD)") },
                            modifier = Modifier.weight(1.5f),
                            leadingIcon = { Icon(Icons.Default.Today, null) }
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
                            viewModel.searchFlights(fromCode, toCode, date, adults.toIntOrNull() ?: 1)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = GradientStart),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(size = 20.dp, color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Find near real-time flight prices")
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
                            "Flight Offers from Industry-Standard Travel API",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(flights) { flight ->
                        FlightCard(flight)
                    }
                }
            }
        }
    }
}

@Composable
fun FlightCard(flight: FlightOfferUnified) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    Text(text = flight.airlineCode, fontSize = 12.sp, color = Color.Gray)
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
                InfoItem("Duration", flight.duration)
                InfoItem("Stops", if (flight.stops == 0) "Non-stop" else "${flight.stops} stop(s)")
                
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
