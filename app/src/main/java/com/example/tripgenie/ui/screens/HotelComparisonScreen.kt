package com.example.tripgenie.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.tripgenie.network.HotelOfferUnified
import com.example.tripgenie.ui.theme.GradientStart
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelComparisonScreen(
    onBack: () -> Unit,
    onNavigateToDetails: (HotelOfferUnified) -> Unit,
    viewModel: HotelViewModel = viewModel()
) {
    val context = LocalContext.current
    val cityNames = viewModel.getCityNames()
    
    var selectedCity by remember { mutableStateOf("Delhi") }
    var checkIn by remember { mutableStateOf(getFutureDate(1)) }
    var checkOut by remember { mutableStateOf(getFutureDate(3)) }
    var guests by remember { mutableStateOf("1") }

    var showCityMenu by remember { mutableStateOf(false) }
    var showCheckInPicker by remember { mutableStateOf(false) }
    var showCheckOutPicker by remember { mutableStateOf(false) }

    val hotels = viewModel.hotelOffers
    val isLoading by viewModel.isLoading
    val error by viewModel.error

    LaunchedEffect(error) {
        error?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }

    // Check-in Date Picker
    if (showCheckInPicker) {
        DatePickerModal(
            onDismiss = { showCheckInPicker = false },
            onDateSelected = { checkIn = it }
        )
    }

    // Check-out Date Picker
    if (showCheckOutPicker) {
        DatePickerModal(
            onDismiss = { showCheckOutPicker = false },
            onDateSelected = { checkOut = it }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Hotels") },
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
            // Advanced Search Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // City Selector
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedCity,
                            onValueChange = {},
                            label = { Text("Select City") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Business, null) },
                            trailingIcon = {
                                IconButton(onClick = { showCityMenu = true }) {
                                    Icon(Icons.Default.KeyboardArrowDown, null)
                                }
                            }
                        )
                        DropdownMenu(expanded = showCityMenu, onDismissRequest = { showCityMenu = false }) {
                            cityNames.forEach { name ->
                                DropdownMenuItem(text = { Text(name) }, onClick = {
                                    selectedCity = name
                                    showCityMenu = false
                                })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Date Row
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = checkIn,
                            onValueChange = {},
                            label = { Text("Check-in") },
                            readOnly = true,
                            modifier = Modifier.weight(1f).clickable { showCheckInPicker = true },
                            leadingIcon = { Icon(Icons.Default.Today, null) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = checkOut,
                            onValueChange = {},
                            label = { Text("Check-out") },
                            readOnly = true,
                            modifier = Modifier.weight(1f).clickable { showCheckOutPicker = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Guests
                    OutlinedTextField(
                        value = guests,
                        onValueChange = { guests = it },
                        label = { Text("Guests") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Person, null) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.searchHotels(selectedCity, checkIn, checkOut, guests.toIntOrNull() ?: 1) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GradientStart),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Check Availability & Pricing (₹)")
                        }
                    }
                }
            }

            // Results List
            if (hotels.isEmpty() && !isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No stays found for these criteria.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(hotels) { hotel ->
                        AdvancedHotelCard(hotel) {
                            viewModel.selectedHotel = hotel
                            onNavigateToDetails(hotel)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(onDismiss: () -> Unit, onDateSelected: (String) -> Unit) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    onDateSelected(sdf.format(Date(it)))
                }
                onDismiss()
            }) { Text("OK") }
        }
    ) { DatePicker(state = datePickerState) }
}

@Composable
fun AdvancedHotelCard(hotel: HotelOfferUnified, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.height(120.dp)) {
            // Image Placeholder (Amadeus test usually lacks real URLs)
            AsyncImage(
                model = "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=400",
                contentDescription = null,
                modifier = Modifier.width(120.dp).fillMaxHeight(),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp).weight(1f)) {
                Text(text = hotel.hotelName, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
                Text(text = hotel.address, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(4.dp)) {
                        Text(text = hotel.mealPlan, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                    }
                    Text(text = "${hotel.currency}${hotel.pricePerNight.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = GradientStart)
                }
            }
        }
    }
}

private fun getFutureDate(days: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, days)
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
}
