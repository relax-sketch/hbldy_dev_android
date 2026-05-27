package com.example.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = QualityPrimary,
    onPrimary = QualityOnPrimary,
    primaryContainer = QualityPrimarySoft,
    onPrimaryContainer = QualityPrimaryDark,
    secondary = QualitySecondary,
    onSecondary = QualityOnPrimary,
    secondaryContainer = QualitySuccessContainer,
    onSecondaryContainer = QualityPrimaryDark,
    tertiary = QualityAccentOrange,
    onTertiary = QualityOnPrimary,
    tertiaryContainer = QualityWarningContainer,
    onTertiaryContainer = QualityAccentOrange,
    error = QualityAccentRed,
    onError = QualityOnPrimary,
    errorContainer = QualityErrorContainer,
    onErrorContainer = QualityAccentRed,
    background = QualityBackground,
    onBackground = QualityTextPrimary,
    surface = QualitySurface,
    onSurface = QualityTextPrimary,
    surfaceVariant = QualitySurfaceAlt,
    onSurfaceVariant = QualityTextSecondary,
    surfaceTint = QualityPrimary,
    outline = QualityBorderStrong,
    outlineVariant = QualityBorder,
    scrim = QualityTextPrimary,
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content,
    )
}
