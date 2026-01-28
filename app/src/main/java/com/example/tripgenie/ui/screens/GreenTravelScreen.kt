package com.example.tripgenie.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tripgenie.ui.theme.GradientStart

data class EcoTip(
    val title: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GreenTravelScreen(onBack: () -> Unit) {
    val tips = listOf(
        EcoTip("Use Public Transport", "Buses and trains emit significantly less CO2 per passenger than cars."),
        EcoTip("Pack Reusable Items", "Bring your own water bottle, bags, and straws to reduce plastic waste."),
        EcoTip("Support Local", "Buying local products reduces the carbon footprint of transportation."),
        EcoTip("Save Energy", "Remember to turn off lights and AC when leaving your hotel room.")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Green Travel") },
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Carbon Footprint Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Eco, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Carbon Footprint Summary", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Your average trip footprint is lower than 70% of travelers!", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = 0.3f,
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = Color(0xFF4CAF50),
                        trackColor = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Low Impact", fontSize = 12.sp, color = Color(0xFF2E7D32))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Eco-Friendly Tips", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(tips) { tip ->
                    TipCard(tip)
                }
            }
        }
    }
}

@Composable
fun TipCard(tip: EcoTip) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFFBC02D))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = tip.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = tip.description, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}
