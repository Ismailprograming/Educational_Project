package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CustomDarkColorScheme = darkColorScheme(
    primary = AmberAccent,
    onPrimary = Color(0xFF1D1838),
    primaryContainer = Surface2,
    onPrimaryContainer = TextPrimary,
    secondary = ProgressViolet,
    onSecondary = TextPrimary,
    secondaryContainer = Surface3,
    onSecondaryContainer = TextSecondary,
    tertiary = AmberAccent,
    onTertiary = Color(0xFF1D1838),
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = BackgroundDark,
    onSurface = TextPrimary,
    surfaceVariant = Surface1,
    onSurfaceVariant = TextSecondary,
    surfaceTint = AmberAccent,
    outline = SurfaceBorder,
    outlineVariant = Surface3,
    error = MistakesRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CustomDarkColorScheme,
        typography = Typography,
        content = content
    )
}
