package com.example.tripgenie.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.example.tripgenie.SessionManager

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimary,
    secondary = PurplePrimary,
    tertiary = BlueSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    secondary = PurplePrimary,
    tertiary = BlueSecondary
)

@Composable
fun TripGenieTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = SessionManager.getInstance(context)
    val isUserDarkMode by sessionManager.isDarkMode.collectAsState()
    
    // Fallback to system theme if no user preference set (logic can be expanded)
    val darkTheme = isUserDarkMode

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
