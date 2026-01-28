package com.example.tripgenie.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tripgenie.ui.theme.GradientStart

data class DayItinerary(
    val day: String,
    val hotel: String,
    val food: String,
    val sightseeing: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripPlannerScreen(onBack: () -> Unit) {
    var destination by remember { mutableStateOf("") }
    var days by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var showItinerary by remember { mutableStateOf(false) }

    val sampleItinerary = listOf(
        DayItinerary("Day 1", "Grand Plaza Hotel", "Local Street Food", "City Museum & Park"),
        DayItinerary("Day 2", "Grand Plaza Hotel", "Traditional Bistro", "Historic Old Town"),
        DayItinerary("Day 3", "Grand Plaza Hotel", "Seafood Grill", "Beachside Sunset Walk")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trip Planner") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GradientStart,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            if (showItinerary) {
                FloatingActionButton(
                    onClick = { /* Save Plan */ },
                    containerColor = GradientStart,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Save")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (!showItinerary) {
                OutlinedTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    label = { Text("Destination") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = days,
                    onValueChange = { days = it },
                    label = { Text("Number of Days") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = budget,
                    onValueChange = { budget = it },
                    label = { Text("Budget") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { showItinerary = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = GradientStart)
                ) {
                    Text("Generate Plan", fontWeight = FontWeight.Bold)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(sampleItinerary) { item ->
                        ItineraryCard(item)
                    }
                }
            }
        }
    }
}

@Composable
fun ItineraryCard(item: DayItinerary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.day, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = GradientStart)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text(text = "🏨 Hotel: ${item.hotel}", fontSize = 14.sp)
            Text(text = "🍴 Food: ${item.food}", fontSize = 14.sp)
            Text(text = "🏛 Sightseeing: ${item.sightseeing}", fontSize = 14.sp)
        }
    }
}
