package com.manish.tripgenie.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.manish.tripgenie.TripViewModel
import com.manish.tripgenie.model.ItineraryItem
import com.manish.tripgenie.ui.theme.GradientStart

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

    val itineraryList by tripViewModel.itineraryList.observeAsState(emptyList())
    val isLoading by tripViewModel.isLoading.observeAsState(false)
    val error by tripViewModel.error.observeAsState(null)
    val context = LocalContext.current

    LaunchedEffect(error) { error?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trip Planner") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GradientStart, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background)) {
            if (itineraryList.isEmpty() && !isLoading) {
                Column(Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState())) {
                    Text("Enter Preferences", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    
                    OutlinedTextField(destination, { destination = it }, label = { Text("Destination") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(days, { days = it }, label = { Text("Days (max 3)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(budget, { budget = it }, label = { Text("Budget ($)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))

                    ExposedDropdownMenuBox(expandedStyle, { expandedStyle = !expandedStyle }) {
                        OutlinedTextField(travelStyle, {}, readOnly = true, label = { Text("Style") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedStyle) }, modifier = Modifier.menuAnchor().fillMaxWidth())
                        ExposedDropdownMenu(expandedStyle, { expandedStyle = false }) {
                            listOf("Budget", "Moderate", "Luxury").forEach { DropdownMenuItem(text = { Text(it) }, onClick = { travelStyle = it; expandedStyle = false }) }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(interests, { interests = it }, label = { Text("Interests") }, modifier = Modifier.fillMaxWidth())
                    
                    Spacer(Modifier.height(24.dp))
                    Button({ if(destination.isNotBlank()) tripViewModel.generateTripPlan(destination, days, budget, travelStyle, interests) }, Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(GradientStart)) {
                        Text("Plan My Trip")
                    }
                }
            } else if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = GradientStart) }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item { Text("Plan for $destination", style = MaterialTheme.typography.titleLarge, color = GradientStart, fontWeight = FontWeight.Bold) }
                    items(itineraryList) { DayPlanCard(it) }
                    item { OutlinedButton({ tripViewModel.generateTripPlan(destination, days, budget, travelStyle, interests) }, Modifier.fillMaxWidth()) { Text("Regenerate") } }
                }
            }
        }
    }
}

@Composable
fun DayPlanCard(item: ItineraryItem) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
        Column(Modifier.padding(12.dp)) {
            Text(item.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GradientStart)
            if (item.details.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(item.details, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
