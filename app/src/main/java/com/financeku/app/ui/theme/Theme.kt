package com.financeku.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.financeku.app.data.local.datastore.TokenDataStore

val LocalDarkMode = staticCompositionLocalOf { mutableStateOf(true) }

private val DarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = DarkBackground,
    primaryContainer = CyanAccentDark,
    onPrimaryContainer = TextPrimary,
    secondary = BlueAccent,
    onSecondary = TextPrimary,
    secondaryContainer = NavBarIndicator,
    onSecondaryContainer = BlueAccentLight,
    tertiary = PurpleIndicator,
    onTertiary = TextPrimary,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkCardBorder,
    outlineVariant = Color(0xFF374151),
    error = RedIndicator,
    onError = TextPrimary,
)

private val LightColorScheme = lightColorScheme(
    primary = CyanAccentDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCFFAFE),
    onPrimaryContainer = Color(0xFF042F2E),
    secondary = BlueAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDBEAFE),
    onSecondaryContainer = Color(0xFF1E3A5F),
    tertiary = PurpleIndicator,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightCardBorder,
    outlineVariant = Color(0xFFCBD5E1),
    error = RedIndicator,
    onError = Color.White,
)

@Composable
fun FinanceKuTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val isDarkState = remember { mutableStateOf(darkTheme) }

    LaunchedEffect(darkTheme) {
        isDarkState.value = darkTheme
    }

    val colorScheme = if (isDarkState.value) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalDarkMode provides isDarkState) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = FinanceKuTypography,
            content = content
        )
    }
}
