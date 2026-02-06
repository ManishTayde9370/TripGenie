package com.example.tripgenie.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tripgenie.TripViewModel
import com.example.tripgenie.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripPlannerScreen(
    initialCity: String? = null,
    onBack: () -> Unit,
    tripViewModel: TripViewModel = viewModel()
) {
    var destination by remember { mutableStateOf(initialCity ?: "") }
    var days by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var travelStyle by remember { mutableStateOf("Moderate") }
    var interests by remember { mutableStateOf("") }
    
    var expandedStyle by remember { mutableStateOf(false) }
    val styles = listOf("Budget", "Moderate", "Luxury")

    val itineraryList by tripViewModel.itineraryList.observeAsState(emptyList())
    val isLoading by tripViewModel.isLoading.observeAsState(false)
    val error by tripViewModel.error.observeAsState(null)
    
    val context = LocalContext.current

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Plan Your Trip") },
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
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (itineraryList.isEmpty() && !isLoading) {
                // Input Form
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Trip Details",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Enter your preferences for a custom plan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 1. Destination
                    OutlinedTextField(
                        value = destination,
                        onValueChange = { destination = it },
                        label = { Text("Destination") },
                        placeholder = { Text("e.g., Paris, Tokyo, New York") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Place, null) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Days
                    OutlinedTextField(
                        value = days,
                        onValueChange = { days = it },
                        label = { Text("Number of Days") },
                        placeholder = { Text("e.g., 3") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Icon(Icons.Default.Today, null) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. Budget
                    OutlinedTextField(
                        value = budget,
                        onValueChange = { budget = it },
                        label = { Text("Budget ($)") },
                        placeholder = { Text("e.g., 1000") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Icon(Icons.Default.AttachMoney, null) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4. Travel Style Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedStyle,
                        onExpandedChange = { expandedStyle = !expandedStyle }
                    ) {
                        OutlinedTextField(
                            value = travelStyle,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Travel Style") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStyle) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = { Icon(Icons.Default.Hiking, null) }
                        )
                        ExposedDropdownMenu(
                            expanded = expandedStyle,
                            onDismissRequest = { expandedStyle = false }
                        ) {
                            styles.forEach { style ->
                                DropdownMenuItem(
                                    text = { Text(style) },
                                    onClick = {
                                        travelStyle = style
                                        expandedStyle = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 5. Interests
                    OutlinedTextField(
                        value = interests,
                        onValueChange = { interests = it },
                        label = { Text("Interests (comma separated)") },
                        placeholder = { Text("e.g., museums, food, nightlife, nature") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Favorite, null) }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Generate Button
                    Button(
                        onClick = {
                            if (destination.isNotBlank() && days.isNotBlank() && budget.isNotBlank() && interests.isNotBlank()) {
                                tripViewModel.generateTripPlan(destination, days, budget, travelStyle, interests)
                            } else {
                                Toast.makeText(context, "Please fill in all mandatory fields", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GradientStart),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Generate Trip Plan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = GradientStart)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Genie is crafting your personalized trip...", color = Color.Gray)
                    }
                }
            } else {
                // Itinerary List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text(
                                text = "Plan for $destination",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = GradientStart,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { /* Save Plan Action */ }) {
                                Icon(Icons.Default.Save, contentDescription = "Save", tint = GradientStart)
                            }
                        }
                    }
                    items(itineraryList) { item ->
                        DayPlanCard(item)
                    }
                    item {
                        OutlinedButton(
                            onClick = { tripViewModel.generateTripPlan(destination, days, budget, travelStyle, interests) },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Regenerate Plan")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayPlanCard(content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val lines = content.split("\n")
            if (lines.isNotEmpty()) {
                // Heading
                Text(
                    text = lines[0],
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GradientStart
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Details
                lines.drop(1).forEach { line ->
                    if (line.isNotBlank()) {
                        val parts = line.split(":", limit = 2)
                        if (parts.size == 2) {
                            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                Text(
                                    text = "${parts[0]}:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = parts[1].trim(),
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            Text(
                                text = line.trim(),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
