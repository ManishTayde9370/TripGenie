package com.example.tripgenie.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tripgenie.SessionManager
import com.example.tripgenie.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    
    // Connect to global reactive theme state
    val darkMode by sessionManager.isDarkMode.collectAsState()
    
    var notificationsEnabled by remember { mutableStateOf(true) }
    var selectedLanguage by remember { mutableStateOf(sessionManager.getLanguage()) }
    var showLanguageMenu by remember { mutableStateOf(false) }
    
    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    val languages = listOf("English", "French", "Spanish", "German", "Hindi")

    // Updated About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About TripGenie", fontWeight = FontWeight.Bold, color = GradientStart) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Owned by: TeamLeo", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Version: 1.0.0", fontWeight = FontWeight.Bold, color = Color.Gray)
                    
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    
                    Text("TripGenie is your all-in-one AI travel assistant. Our mission is to make every journey seamless, safe, and memorable by leveraging cutting-edge Gemini AI technology.", fontSize = 14.sp)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text("Key Features:", fontWeight = FontWeight.Bold)
                    Text("• AI-Powered Itineraries\n• Real-time Safety Alerts\n• Intelligent Language Help\n• Eco-friendly Travel Insights")
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text("Developed by TeamLeo with a vision to revolutionize the modern travel experience.", fontSize = 12.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Dismiss")
                }
            }
        )
    }

    // Privacy Policy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy & Norms") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("1. Data Collection", fontWeight = FontWeight.Bold, color = GradientStart)
                    Text("We collect your name and email to personalize your travel experience. Trip preferences and search history are used solely for generating AI itineraries.")
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text("2. AI Usage", fontWeight = FontWeight.Bold, color = GradientStart)
                    Text("TripGenie uses Google Gemini AI to process your travel requests. No sensitive personal data is shared with the AI models beyond what is required for the itinerary.")
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text("3. Data Security", fontWeight = FontWeight.Bold, color = GradientStart)
                    Text("Your session data is stored locally on your device. We prioritize your privacy and do not sell your data to third parties.")
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text("4. Your Rights", fontWeight = FontWeight.Bold, color = GradientStart)
                    Text("You can clear your local session and personal data at any time through the Profile section.")
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text("5. External APIs", fontWeight = FontWeight.Bold, color = GradientStart)
                    Text("We use third-party APIs (Ticketmaster, PredictHQ) to fetch events. These services have their own privacy norms.")
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("I Understand")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
                .padding(16.dp)
        ) {
            Text("General Settings", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = GradientStart)
            Spacer(modifier = Modifier.height(16.dp))

            // Dark Mode Toggle
            SettingToggleItem(
                icon = Icons.Default.Brightness4,
                title = "Dark Mode",
                description = "Enable dark theme for the app",
                checked = darkMode,
                onCheckedChange = { sessionManager.setDarkMode(it) }
            )

            // Notifications Toggle
            SettingToggleItem(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                description = "Receive travel alerts and updates",
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("Preferences", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = GradientStart)
            Spacer(modifier = Modifier.height(16.dp))

            // Language Selection
            Box {
                SettingClickableItem(
                    icon = Icons.Default.Language,
                    title = "Language",
                    value = selectedLanguage,
                    onClick = { showLanguageMenu = true }
                )
                DropdownMenu(
                    expanded = showLanguageMenu,
                    onDismissRequest = { showLanguageMenu = false }
                ) {
                    languages.forEach { language ->
                        DropdownMenuItem(
                            text = { Text(language) },
                            onClick = {
                                selectedLanguage = language
                                sessionManager.setLanguage(language)
                                showLanguageMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Privacy & Security", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = GradientStart)
            Spacer(modifier = Modifier.height(16.dp))

            // 1. Permissions: Open System Settings
            SettingClickableItem(
                icon = Icons.Default.Lock, 
                title = "Permissions", 
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }
            )

            // 2. Privacy Policy: Show Internal Dialog
            SettingClickableItem(
                icon = Icons.Default.PrivacyTip, 
                title = "Privacy Policy", 
                onClick = { showPrivacyDialog = true }
            )

            // 3. About App: Show Dialog
            SettingClickableItem(
                icon = Icons.Default.Info, 
                title = "About App", 
                onClick = { showAboutDialog = true }
            )
        }
    }
}

@Composable
fun SettingToggleItem(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.Gray)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(text = description, fontSize = 12.sp, color = Color.Gray)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SettingClickableItem(
    icon: ImageVector,
    title: String,
    value: String? = null,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.Gray)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.weight(1f))
            if (value != null) {
                Text(text = value, fontSize = 14.sp, color = GradientStart, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}
