package com.example.tripgenie.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tripgenie.R
import com.example.tripgenie.ui.theme.GradientStart

data class TravelEvent(
    val title: String,
    val date: String,
    val category: String,
    val imageRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(onBack: () -> Unit) {
    val categories = listOf("All", "Music", "Food", "Cultural")
    var selectedCategory by remember { mutableStateOf("All") }

    val allEvents = listOf(
        TravelEvent("Summer Music Fest", "Aug 15, 2024", "Music", R.drawable.local_events1),
        TravelEvent("Street Food Expo", "Aug 18, 2024", "Food", R.drawable.local_events1),
        TravelEvent("Heritage Walk", "Aug 20, 2024", "Cultural", R.drawable.local_events1),
        TravelEvent("Jazz Night", "Aug 22, 2024", "Music", R.drawable.local_events1),
        TravelEvent("Curry Festival", "Aug 25, 2024", "Food", R.drawable.local_events1),
        TravelEvent("Museum Night", "Aug 28, 2024", "Cultural", R.drawable.local_events1)
    )

    val filteredEvents = if (selectedCategory == "All") allEvents else allEvents.filter { it.category == selectedCategory }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Local Events") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Filter Logic */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
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
            // Category Chips
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory),
                edgePadding = 16.dp,
                containerColor = Color.Transparent,
                contentColor = GradientStart,
                divider = {},
                indicator = {}
            ) {
                categories.forEach { category ->
                    val selected = selectedCategory == category
                    Tab(
                        selected = selected,
                        onClick = { selectedCategory = category },
                        text = {
                            Text(
                                text = category,
                                color = if (selected) GradientStart else Color.Gray,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredEvents) { event ->
                    EventCard(event)
                }
            }
        }
    }
}

@Composable
fun EventCard(event: TravelEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = event.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = event.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = event.date, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* Add to Trip */ },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GradientStart),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add to Trip", fontSize = 12.sp)
                }
            }
        }
    }
}
