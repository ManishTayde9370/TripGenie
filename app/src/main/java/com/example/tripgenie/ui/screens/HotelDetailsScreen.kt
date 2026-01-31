package com.example.tripgenie.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.tripgenie.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailsScreen(
    onBack: () -> Unit,
    viewModel: HotelViewModel = viewModel()
) {
    val hotel = viewModel.selectedHotel
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stay Details") },
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
        if (hotel == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No stay selected.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
            ) {
                // 1. Hotel Image with Placeholder
                Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
                    AsyncImage(
                        model = hotel.imageUrl ?: "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800",
                        contentDescription = "Hotel Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(bottomStart = 8.dp),
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Text(
                            text = "Images subject to availability",
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Column(modifier = Modifier.padding(24.dp)) {
                    // 2. Hotel Header
                    Text(text = hotel.hotelName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(text = hotel.address, color = Color.Gray, fontSize = 14.sp)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { i ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (i < (hotel.rating.take(1).toIntOrNull() ?: 0)) Color(0xFFFFB300) else Color.LightGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "(${hotel.rating})", fontSize = 14.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 3. Stay Summary (Dates & Meal)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Check-in", fontSize = 12.sp, color = Color.Gray)
                                Text(hotel.checkInDate, fontWeight = FontWeight.Bold)
                            }
                            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.Gray)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Check-out", fontSize = 12.sp, color = Color.Gray)
                                Text(hotel.checkOutDate, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4. Meal & Cancellation
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FeatureBadge(icon = Icons.Default.Restaurant, text = hotel.mealPlan, color = Color(0xFF4CAF50))
                        FeatureBadge(icon = Icons.Default.Cancel, text = "Free Cancellation", color = Color(0xFF2196F3))
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 5. Pricing Details
                    Text("Pricing Details", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    DetailRow("Room Type", hotel.roomType)
                    DetailRow("Price per Night", "${hotel.currency}${hotel.pricePerNight.toInt()}")
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Stay Price", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(
                            text = "${hotel.currency}${hotel.totalPrice.toInt()}", 
                            fontWeight = FontWeight.ExtraBold, 
                            fontSize = 24.sp, 
                            color = GradientStart
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 6. Cancellation Policy Text
                    Text("Policy", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        text = hotel.cancellationPolicy, 
                        fontSize = 13.sp, 
                        color = Color.Gray,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // 7. Disclaimer
                    Surface(
                        color = Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFE65100))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Prices are indicative and subject to change. Booking is not included.",
                                fontSize = 12.sp,
                                color = Color(0xFFE65100)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GradientStart)
                    ) {
                        Text("Back to Selection", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureBadge(icon: ImageVector, text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.height(40.dp)
    ) {
        Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, fontSize = 12.sp, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp)
        Text(text = value, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}
