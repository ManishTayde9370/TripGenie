package com.example.tripgenie.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tripgenie.ui.theme.GradientStart

data class TouristCity(
    val name: String,
    val imageUrl: String,
    val isMostVisited: Boolean = false,
    val isTrending: Boolean = false,
    val bestTime: String,
    val budget: String, // "Budget", "Medium", "Luxury"
    val safetyLevel: String // "High", "Moderate"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TouristDiscoveryScreen(
    onBack: () -> Unit,
    onCityClick: (String) -> Unit
) {
    val mostVisitedCities = listOf(
        TouristCity("Paris", "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=500", true, false, "Apr - Jun", "Luxury", "High"),
        TouristCity("Tokyo", "https://images.unsplash.com/photo-1540959733332-eab4deabeeaf?w=500", true, false, "Mar - May", "Medium", "High"),
        TouristCity("New York", "https://images.unsplash.com/photo-1496442226666-8d4d0e62e6e9?w=500", true, false, "Sep - Nov", "Luxury", "High"),
        TouristCity("London", "https://images.unsplash.com/photo-1513635269975-59663e0ac1ad?w=500", true, false, "Jun - Aug", "Luxury", "High")
    )

    val trendingCities = listOf(
        TouristCity("Bali", "https://images.unsplash.com/photo-1537996194471-e657df975ab4?w=500", false, true, "Apr - Oct", "Budget", "Moderate"),
        TouristCity("Rome", "https://images.unsplash.com/photo-1552832230-c0197dd311b5?w=500", false, true, "Oct - Apr", "Medium", "High"),
        TouristCity("Mumbai", "https://images.unsplash.com/photo-1529253355930-ddbe423a2ac7?w=500", false, true, "Nov - Feb", "Budget", "Moderate"),
        TouristCity("Dubai", "https://images.unsplash.com/photo-1512453979798-5ea266f8880c?w=500", false, true, "Nov - Mar", "Luxury", "High")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tourist Discovery") },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Section 1: Most Visited
            item {
                SectionHeader("Most Visited Cities")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(mostVisitedCities) { city ->
                        CityCard(city, onCityClick)
                    }
                }
            }

            // Section 2: Trending
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader("Trending Destinations")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(trendingCities) { city ->
                        CityCard(city, onCityClick)
                    }
                }
            }

            // Section 3: Exploration List (Combined)
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader("Explore All")
            }
            
            items(mostVisitedCities + trendingCities) { city ->
                ExploreListItem(city, onCityClick)
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun CityCard(city: TouristCity, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable { onClick(city.name) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = city.imageUrl,
                    contentDescription = city.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentScale = ContentScale.Crop
                )
                if (city.isMostVisited) {
                    Surface(
                        color = Color(0xFFFFD700),
                        shape = RoundedCornerShape(bottomEnd = 8.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, modifier = Modifier.size(12.dp), tint = Color.Black)
                            Text("Most Visited", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = city.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Best: ${city.bestTime}", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Badge(city.budget, Color(0xFFE3F2FD), Color(0xFF1976D2))
                    Badge(city.safetyLevel, Color(0xFFE8F5E9), Color(0xFF2E7D32))
                }
            }
        }
    }
}

@Composable
fun ExploreListItem(city: TouristCity, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick(city.name) },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = city.imageUrl,
                contentDescription = city.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = city.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "Best time to visit: ${city.bestTime}", fontSize = 13.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Badge(city.budget, Color(0xFFF3E5F5), Color(0xFF7B1FA2))
                    Badge("Safe: ${city.safetyLevel}", Color(0xFFFFF3E0), Color(0xFFE65100))
                }
            }
        }
    }
}

@Composable
fun Badge(text: String, bgColor: Color, textColor: Color) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontSize = 10.sp,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}
