package com.example.tripgenie.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tripgenie.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafetyScreen(
    onBack: () -> Unit,
    viewModel: SafetyViewModel = viewModel()
) {
    var citySearch by remember { mutableStateOf("") }
    val context = LocalContext.current
    
    val alerts = viewModel.alerts
    val score by viewModel.safetyScore
    val isLoading by viewModel.isLoading
    val error by viewModel.error

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Safety Alerts") },
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
            FloatingActionButton(
                onClick = { 
                    if (citySearch.isNotBlank()) {
                        viewModel.fetchSafetyData(citySearch)
                    } else {
                        Toast.makeText(context, "Search for a city first", Toast.LENGTH_SHORT).show()
                    }
                },
                containerColor = GradientStart,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = citySearch,
                onValueChange = { citySearch = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Enter city (e.g. Mumbai, Delhi)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    if (citySearch.isNotBlank()) {
                        viewModel.fetchSafetyData(citySearch)
                    }
                }),
                shape = RoundedCornerShape(12.dp)
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GradientStart)
                }
            } else if (score > 0 || alerts.isNotEmpty()) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    // Safety Score Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                    ) {
                        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$citySearch Safety Score", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            LinearProgressIndicator(
                                progress = score,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp),
                                color = if (score > 0.7f) Color(0xFF4CAF50) else if (score > 0.4f) Color(0xFFFFA726) else Color(0xFFEF5350),
                                trackColor = Color.LightGray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("${(score * 100).toInt()}/100 - ${if (score > 0.7f) "Safe" else if (score > 0.4f) "Moderate" else "Caution"}", 
                                fontWeight = FontWeight.Medium, 
                                color = if (score > 0.7f) Color(0xFF2E7D32) else if (score > 0.4f) Color(0xFFE65100) else Color(0xFFC62828)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Detailed Alerts", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(alerts) { alert ->
                            AlertCard(alert)
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Search for a city to see safety data", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun AlertCard(alert: SafetyAlertItem) {
    val color = when (alert.severity) {
        "High" -> Color(0xFFEF5350)
        "Medium" -> Color(0xFFFFA726)
        else -> Color(0xFF42A5F5)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = alert.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = alert.description, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}
