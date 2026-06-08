package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PinkPrimary,
    onPrimary = PinkOnPrimary,
    primaryContainer = PinkPrimaryContainer,
    onPrimaryContainer = PinkOnPrimaryContainer,
    secondary = PinkSecondary,
    onSecondary = PinkOnSecondary,
    secondaryContainer = PinkSecondaryContainer,
    onSecondaryContainer = PinkOnSecondaryContainer,
    tertiary = PinkTertiary,
    onTertiary = PinkOnTertiary,
    tertiaryContainer = PinkTertiaryContainer,
    onTertiaryContainer = PinkOnTertiaryContainer,
    error = PinkError,
    onError = PinkOnError,
    errorContainer = PinkErrorContainer,
    onErrorContainer = PinkOnErrorContainer,
    background = PinkBackground,
    onBackground = PinkOnBackground,
    surface = PinkSurface,
    onSurface = PinkOnSurface,
    surfaceVariant = PinkSurfaceVariant,
    onSurfaceVariant = PinkOnSurfaceVariant,
    outline = PinkOutline,
    outlineVariant = PinkOutlineVariant,
    inverseSurface = PinkInverseSurface,
    inverseOnSurface = PinkInverseOnSurface,
    inversePrimary = PinkInversePrimary,
    scrim = PinkScrim
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPinkPrimary,
    onPrimary = DarkPinkOnPrimary,
    primaryContainer = DarkPinkPrimaryContainer,
    onPrimaryContainer = DarkPinkOnPrimaryContainer,
    secondary = DarkPinkSecondary,
    onSecondary = DarkPinkOnSecondary,
    secondaryContainer = DarkPinkSecondaryContainer,
    onSecondaryContainer = DarkPinkOnSecondaryContainer,
    tertiary = DarkPinkTertiary,
    onTertiary = DarkPinkOnTertiary,
    tertiaryContainer = DarkPinkTertiaryContainer,
    onTertiaryContainer = DarkPinkOnTertiaryContainer,
    error = DarkPinkError,
    onError = DarkPinkOnError,
    errorContainer = DarkPinkErrorContainer,
    onErrorContainer = DarkPinkOnErrorContainer,
    background = DarkPinkBackground,
    onBackground = DarkPinkOnBackground,
    surface = DarkPinkSurface,
    onSurface = DarkPinkOnSurface,
    surfaceVariant = DarkPinkSurfaceVariant,
    onSurfaceVariant = DarkPinkOnSurfaceVariant,
    outline = DarkPinkOutline,
    outlineVariant = DarkPinkOutlineVariant,
    inverseSurface = DarkPinkInverseSurface,
    inverseOnSurface = DarkPinkInverseOnSurface,
    inversePrimary = DarkPinkInversePrimary,
    scrim = DarkPinkScrim
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
