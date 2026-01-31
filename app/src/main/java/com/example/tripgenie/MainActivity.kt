package com.example.tripgenie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.tripgenie.navigation.NavGraph
import com.example.tripgenie.ui.theme.TripGenieTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // enableEdgeToEdge() handles status bar and navigation bar padding correctly
        enableEdgeToEdge()
        
        setContent {
            TripGenieTheme {
                // Ensure Surface occupies the full screen and provides a default background
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainContent()
                }
            }
        }
    }
}

@Composable
fun MainContent() {
    val navController = rememberNavController()
    NavGraph(navController = navController)
}
