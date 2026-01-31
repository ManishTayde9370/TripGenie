package com.example.tripgenie.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.tripgenie.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    onBack: () -> Unit,
    viewModel: EventsViewModel = viewModel()
) {
    var citySearch by remember { mutableStateOf("") }
    val context = LocalContext.current
    
    val events = viewModel.eventList
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.error

    val categories = listOf("All", "Music", "Sports", "Arts", "Film", "Miscellaneous")
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredEvents = if (selectedCategory == "All") {
        events
    } else {
        events.filter { it.category.contains(selectedCategory, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Local Events") },
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
        ) {
            // Search Bar
            OutlinedTextField(
                value = citySearch,
                onValueChange = { citySearch = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search city (e.g. London, New York)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    if (citySearch.isNotBlank()) {
                        viewModel.fetchEvents(citySearch)
                    }
                }),
                shape = RoundedCornerShape(12.dp)
            )

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

            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = GradientStart
                    )
                } else if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = Color.Red
                    )
                } else if (events.isEmpty()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Search for events in any city", color = Color.Gray)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredEvents) { event ->
                            EventCard(event) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                                context.startActivity(intent)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(event: EventItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = event.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 2,
                    lineHeight = 16.sp,
                    minLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "📅 ${event.date}", fontSize = 11.sp, color = Color.Gray)
                Text(text = "📍 ${event.venue}", fontSize = 11.sp, color = Color.Gray, maxLines = 1)
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = GradientStart.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = event.category,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        color = GradientStart,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
