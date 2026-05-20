package com.example.todoist.screens.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Purple,
    onPrimary = Color.White,
    primaryContainer = PurpleLight,
    onPrimaryContainer = PurpleDark,
    secondary = StatusInProgress,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE9E0),
    onSecondaryContainer = Color(0xFF7A3300),
    tertiary = StatusTodo,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD6EEFF),
    onTertiaryContainer = Color(0xFF003A5E),
    background = White,
    onBackground = TextPrimary,
    surface = White,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceGray,
    onSurfaceVariant = TextSecondary,
    outline = DividerColor,
    outlineVariant = DividerColor,
)

@Composable
fun TodoAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content,
    )
}
