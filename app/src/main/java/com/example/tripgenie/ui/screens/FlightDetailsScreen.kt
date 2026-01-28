package com.example.tripgenie.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tripgenie.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightDetailsScreen(
    onBack: () -> Unit,
    viewModel: FlightViewModel = viewModel()
) {
    val flight = viewModel.selectedFlight

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flight Details") },
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
        if (flight == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No flight data found.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Airline Header
                Icon(
                    imageVector = Icons.Default.AirplanemodeActive,
                    contentDescription = null,
                    tint = GradientStart,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = flight.airlineName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(text = "Flight ID: ${flight.id}", color = Color.Gray, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(32.dp))

                // Price Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Total Fare (INR)", fontSize = 14.sp)
                        Text(
                            text = "${flight.currency} ${flight.price}",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GradientStart
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Journey Info
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            JourneyStop(time = flight.departureTime, city = flight.originCity)
                            
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = flight.duration, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Divider(modifier = Modifier.width(60.dp).padding(vertical = 4.dp))
                                Text(text = if (flight.stops == 0) "Non-stop" else "${flight.stops} stop(s)", fontSize = 10.sp, color = Color.Gray)
                            }

                            JourneyStop(time = flight.arrivalTime, city = flight.destinationCity)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Disclaimer
                Surface(
                    color = Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFE65100))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Prices are indicative and subject to change. This is a near real-time offer from multiple airlines.",
                            fontSize = 12.sp,
                            color = Color(0xFFE65100),
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GradientStart)
                ) {
                    Text("Back to Search", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun JourneyStop(time: String, city: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = time, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        Text(text = city, fontSize = 14.sp, color = Color.Gray)
    }
}
