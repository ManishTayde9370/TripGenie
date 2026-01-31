package com.example.tripgenie.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tripgenie.R
import com.example.tripgenie.SessionManager
import com.example.tripgenie.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    
    var userName by remember { mutableStateOf(sessionManager.getUserName() ?: "Guest User") }
    var userEmail by remember { mutableStateOf(sessionManager.getUserEmail() ?: "guest@example.com") }
    
    var showEditDialog by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }
    var showExplorerDialog by remember { mutableStateOf(false) }
    var showEcoDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        EditProfileDialog(
            currentName = userName,
            currentEmail = userEmail,
            onDismiss = { showEditDialog = false },
            onSave = { newName, newEmail ->
                sessionManager.saveUser(newName, newEmail)
                userName = newName
                userEmail = newEmail
                showEditDialog = false
                Toast.makeText(context, "Profile Updated!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showSupportDialog) {
        SupportDialog(
            onDismiss = { showSupportDialog = false }
        )
    }

    if (showExplorerDialog) {
        BadgeDetailDialog(
            title = "Explorer Badge",
            description = "Congratulations! You have explored 5+ cities with TripGenie. Your passion for discovery makes you a true modern-day nomad. Keep uncovering the hidden gems of the world!",
            icon = Icons.Default.Explore,
            color = Color(0xFF2196F3),
            onDismiss = { showExplorerDialog = false }
        )
    }

    if (showEcoDialog) {
        BadgeDetailDialog(
            title = "Eco Traveler Badge",
            description = "You are a champion of the planet! By choosing sustainable travel options and supporting local communities, you've reduced your carbon footprint significantly. Together, we travel green.",
            icon = Icons.Default.Eco,
            color = Color(0xFF4CAF50),
            onDismiss = { showEcoDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = Color.White)
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image
            Image(
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(4.dp, GradientStart, CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(userName, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Text(userEmail, color = Color.Gray, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(32.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStat("12", "Saved Trips")
                ProfileStat("5", "Badges")
                ProfileStat("24", "Reviews")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "My Badges",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BadgeItem("Eco Traveler", Icons.Default.Eco, Color(0xFF4CAF50)) {
                    showEcoDialog = true
                }
                BadgeItem("Explorer", Icons.Default.Explore, Color(0xFF2196F3)) {
                    showExplorerDialog = true
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action List
            Column(modifier = Modifier.fillMaxWidth()) {
                ProfileActionItem(Icons.Default.History, "Travel History") {
                    Toast.makeText(context, "History feature coming soon!", Toast.LENGTH_SHORT).show()
                }
                ProfileActionItem(Icons.Default.Payment, "Payment Methods") {
                    Toast.makeText(context, "Payments feature coming soon!", Toast.LENGTH_SHORT).show()
                }
                ProfileActionItem(Icons.AutoMirrored.Filled.Help, "Support") {
                    showSupportDialog = true
                }
                ProfileActionItem(Icons.AutoMirrored.Filled.ExitToApp, "Logout", Color.Red) {
                    sessionManager.clearSession()
                    onLogout()
                }
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    currentName: String,
    currentEmail: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var email by remember { mutableStateOf(currentEmail) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(name, email) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SupportDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("TripGenie Support", fontWeight = FontWeight.Bold, color = GradientStart) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text("Need help? TeamLeo is here for you!", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                
                Text("FAQs", fontWeight = FontWeight.Bold, color = GradientStart)
                Text("• How do I plan a trip?\n  Go to 'Plan a Trip' from home.")
                Text("• Are safety alerts real-time?\n  Yes, they are updated based on city search.")
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Contact Us", fontWeight = FontWeight.Bold, color = GradientStart)
                Text("Email: support@teamleo.com")
                Text("Working Hours: 9 AM - 6 PM (IST)")
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Report an Issue", fontWeight = FontWeight.Bold, color = GradientStart)
                Text("If you find a bug, please email us with screenshots. We appreciate your feedback!")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun BadgeDetailDialog(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = title, fontWeight = FontWeight.Bold, color = color)
            }
        },
        text = {
            Text(text = description, textAlign = TextAlign.Center, lineHeight = 20.sp)
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Awesome!")
            }
        }
    )
}

@Composable
fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = GradientStart)
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun RowScope.BadgeItem(name: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.weight(1f).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = name, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = color)
        }
    }
}

@Composable
fun ProfileActionItem(
    icon: ImageVector,
    title: String,
    color: Color = Color.Black,
    onClick: () -> Unit = {}
) {
    Surface(onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontSize = 16.sp, color = color, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}
