package com.example.tripgenie.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tripgenie.SessionManager
import com.example.tripgenie.ui.theme.GradientEnd
import com.example.tripgenie.ui.theme.GradientStart
import kotlinx.coroutines.launch
import java.util.Calendar

data class QuickAction(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToTripPlanner: () -> Unit,
    onNavigateToSafety: () -> Unit,
    onNavigateToLanguage: () -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToGreenTravel: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToTouristDiscovery: () -> Unit,
    onNavigateToFlights: () -> Unit,
    onNavigateToHotels: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    val userName = sessionManager.getUserName() ?: "Traveler"
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableIntStateOf(0) }

    val greeting = remember { getGreetingMessage() }

    val quickActions = listOf(
        QuickAction("Compare Hotels", Icons.Default.Hotel, "hotel_comparison", Color(0xFFE91E63)),
        QuickAction("Compare Flights", Icons.Default.Flight, "flight_comparison", Color(0xFF2196F3)),
        QuickAction("Discover Cities", Icons.Default.TravelExplore, "tourist_discovery", Color(0xFF673AB7)),
        QuickAction("Plan a Trip", Icons.Default.Map, "trip_planner", Color(0xFF42A5F5)),
        QuickAction("Safety Alerts", Icons.Default.Security, "safety", Color(0xFFEF5350)),
        QuickAction("Language Help", Icons.Default.Translate, "language_assistant", Color(0xFF66BB6A)),
        QuickAction("Events Near Me", Icons.Default.Event, "events", Color(0xFFFFA726)),
        QuickAction("Green Travel", Icons.Default.Eco, "green_travel", Color(0xFF26A69A))
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "TripGenie Menu",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = GradientStart
                )
                NavigationDrawerItem(
                    label = { Text("Hotel Comparison") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onNavigateToHotels() 
                    },
                    icon = { Icon(Icons.Default.Hotel, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text("Flight Comparison") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onNavigateToFlights() 
                    },
                    icon = { Icon(Icons.Default.Flight, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text("Tourist Discovery") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onNavigateToTouristDiscovery() 
                    },
                    icon = { Icon(Icons.Default.Explore, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text("Profile") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onNavigateToProfile() 
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onNavigateToSettings() 
                    },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("TripGenie", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { 
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = GradientStart,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = selectedItem == 0,
                        onClick = { selectedItem = 0 }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Hotel, contentDescription = "Hotels") },
                        label = { Text("Hotels") },
                        selected = selectedItem == 1,
                        onClick = { 
                            selectedItem = 1
                            onNavigateToHotels()
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Flight, contentDescription = "Flights") },
                        label = { Text("Flights") },
                        selected = selectedItem == 2,
                        onClick = { 
                            selectedItem = 2
                            onNavigateToFlights()
                        }
                    )
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Header Gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(GradientStart, GradientEnd)
                            ),
                            shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            text = "$greeting, $userName!",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Your smart stay starts here.",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Smart Companion",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(quickActions) { action ->
                        QuickActionCard(action = action) {
                            when (action.route) {
                                "hotel_comparison" -> onNavigateToHotels()
                                "flight_comparison" -> onNavigateToFlights()
                                "tourist_discovery" -> onNavigateToTouristDiscovery()
                                "trip_planner" -> onNavigateToTripPlanner()
                                "safety" -> onNavigateToSafety()
                                "language_assistant" -> onNavigateToLanguage()
                                "events" -> onNavigateToEvents()
                                "green_travel" -> onNavigateToGreenTravel()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionCard(action: QuickAction, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = null,
                tint = action.color,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = action.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

private fun getGreetingMessage(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }
}
