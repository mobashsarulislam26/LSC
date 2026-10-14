package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6EE7B7),
    onPrimary = Color(0xFF064E3B),
    primaryContainer = LscEmeraldDark,
    onPrimaryContainer = LscEmeraldLight,
    secondary = Color(0xFFFBBF24),
    onSecondary = Color(0xFF78350F),
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1)
)

private val LightColorScheme = lightColorScheme(
    primary = LscEmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = LscEmeraldLight,
    onPrimaryContainer = LscEmeraldDark,
    secondary = LscGoldSecondary,
    onSecondary = Color.White,
    secondaryContainer = LscGoldLight,
    onSecondaryContainer = LscGoldDark,
    background = Color(0xFFF8FAFC),
    surface = Color.White,
    onBackground = LscSlateDark,
    onSurface = LscSlateDark,
    surfaceVariant = LscSurfaceVariant,
    onSurfaceVariant = LscSlateMedium
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set false to maintain consistent LSC branding
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
