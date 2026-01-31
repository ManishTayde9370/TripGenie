package com.example.tripgenie.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
    var travelers by remember { mutableStateOf("1") }
    
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
                title = { Text("AI Trip Planner") },
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
        },
        floatingActionButton = {
            if (itineraryList.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { Toast.makeText(context, "Itinerary saved to your profile!", Toast.LENGTH_SHORT).show() },
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
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (itineraryList.isEmpty() && !isLoading) {
                // Input Form
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Plan Your Adventure", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Let our AI create the perfect schedule for you.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    OutlinedTextField(
                        value = destination,
                        onValueChange = { destination = it },
                        label = { Text("Where to?") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = days,
                        onValueChange = { days = it },
                        label = { Text("How many days?") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = travelers,
                        onValueChange = { travelers = it },
                        label = { Text("How many travelers?") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = {
                            if (destination.isNotBlank() && days.isNotBlank()) {
                                tripViewModel.generateTripPlan(destination, days, travelers)
                            } else {
                                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GradientStart),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Generate My Plan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = GradientStart)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Genie is crafting your trip...", color = Color.Gray)
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
                        Text(
                            text = "Your Itinerary for $destination",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = GradientStart,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(itineraryList) { item ->
                        DetailedItineraryCard(item)
                    }
                    item {
                        OutlinedButton(
                            onClick = { tripViewModel.generateTripPlan(destination, days, travelers) },
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
fun DetailedItineraryCard(content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val lines = content.split("\n")
            if (lines.isNotEmpty()) {
                Text(
                    text = lines[0], // Assuming "Day X:"
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = GradientStart
                )
                if (lines.size > 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = lines.drop(1).joinToString("\n"),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}
