package com.example.tripgenie.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tripgenie.ui.theme.GradientStart

data class SafetyAlert(
    val title: String,
    val description: String,
    val severity: String // "High", "Medium", "Low"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafetyScreen(onBack: () -> Unit) {
    val alerts = listOf(
        SafetyAlert("Weather Warning", "Heavy rain expected in the downtown area tomorrow.", "Medium"),
        SafetyAlert("Public Transport Strike", "Limited bus services on Route 42 due to local strikes.", "High"),
        SafetyAlert("Event Crowd", "Expect large crowds near the stadium tonight.", "Low")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Safety Alerts") },
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
            FloatingActionButton(
                onClick = { /* Refresh Data */ },
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
                .padding(16.dp)
        ) {
            // Safety Score Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Current Location Safety Score", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = 0.85f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp),
                        color = Color(0xFF4CAF50),
                        trackColor = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("85/100 - Very Safe", fontWeight = FontWeight.Medium, color = Color(0xFF2E7D32))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Live Alerts", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(alerts) { alert ->
                    AlertCard(alert)
                }
            }
        }
    }
}

@Composable
fun AlertCard(alert: SafetyAlert) {
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
