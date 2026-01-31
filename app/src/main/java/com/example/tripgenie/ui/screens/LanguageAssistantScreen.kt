package com.example.tripgenie.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tripgenie.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageAssistantScreen(
    onBack: () -> Unit,
    viewModel: LanguageViewModel = viewModel()
) {
    var textToTranslate by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf("French") }
    var showLanguageMenu by remember { mutableStateOf(false) }

    val languages = listOf("French", "Spanish", "German", "Hindi", "Japanese", "Italian", "Mandarin")

    val translatedText by viewModel.translatedText
    val isLoading by viewModel.isLoading
    val error by viewModel.error
    
    val context = LocalContext.current

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Language Assistant") },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Language Selector
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { showLanguageMenu = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Language, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Translate to: $selectedLanguage")
                }
                DropdownMenu(
                    expanded = showLanguageMenu,
                    onDismissRequest = { showLanguageMenu = false }
                ) {
                    languages.forEach { language ->
                        DropdownMenuItem(
                            text = { Text(language) },
                            onClick = {
                                selectedLanguage = language
                                showLanguageMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = textToTranslate,
                onValueChange = { textToTranslate = it },
                label = { Text("Enter text to translate") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { 
                        Toast.makeText(context, "Voice input coming soon!", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Mic, contentDescription = "Speak")
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { 
                    if (textToTranslate.isNotBlank()) {
                        viewModel.translate(textToTranslate, selectedLanguage)
                    } else {
                        Toast.makeText(context, "Please enter some text", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GradientStart),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Translate", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (translatedText.isNotEmpty() || isLoading) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Translation ($selectedLanguage)", fontWeight = FontWeight.Bold, color = GradientStart)
                            IconButton(onClick = { 
                                Toast.makeText(context, "Playing audio...", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Listen")
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 12.dp))
                        if (isLoading) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        } else {
                            Text(
                                text = translatedText,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
