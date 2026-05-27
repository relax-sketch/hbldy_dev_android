package com.example.myapplication.quality.ui.design

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.QualityAccentGray
import com.example.myapplication.ui.theme.QualityAccentOrange
import com.example.myapplication.ui.theme.QualityAccentPurple
import com.example.myapplication.ui.theme.QualityAccentRed
import com.example.myapplication.ui.theme.QualityBackground
import com.example.myapplication.ui.theme.QualityBackgroundEnd
import com.example.myapplication.ui.theme.QualityBorder
import com.example.myapplication.ui.theme.QualityBorderStrong
import com.example.myapplication.ui.theme.QualityErrorContainer
import com.example.myapplication.ui.theme.QualityGrayContainer
import com.example.myapplication.ui.theme.QualityInfoContainer
import com.example.myapplication.ui.theme.QualityOnPrimary
import com.example.myapplication.ui.theme.QualityPrimary
import com.example.myapplication.ui.theme.QualityPrimaryDark
import com.example.myapplication.ui.theme.QualityPrimarySoft
import com.example.myapplication.ui.theme.QualityPurpleContainer
import com.example.myapplication.ui.theme.QualitySecondary
import com.example.myapplication.ui.theme.QualitySuccessContainer
import com.example.myapplication.ui.theme.QualitySurface
import com.example.myapplication.ui.theme.QualitySurfaceAlt
import com.example.myapplication.ui.theme.QualitySurfaceMuted
import com.example.myapplication.ui.theme.QualityTextPrimary
import com.example.myapplication.ui.theme.QualityTextSecondary
import com.example.myapplication.ui.theme.QualityTextTertiary
import com.example.myapplication.ui.theme.QualityWarningContainer

object QualityDesignTokens {
    val backgroundBrush: Brush = Brush.linearGradient(
        colors = listOf(QualityBackground, QualityBackgroundEnd),
    )

    val primaryButtonBrush: Brush = Brush.linearGradient(
        colors = listOf(QualityPrimary, QualityPrimaryDark),
    )

    val pageShape = RoundedCornerShape(34.dp)
    val cardShape = RoundedCornerShape(30.dp)
    val buttonShape = RoundedCornerShape(24.dp)
    val chipShape = RoundedCornerShape(18.dp)
    val iconTileShape = RoundedCornerShape(22.dp)

    val largeCardPadding = 26.dp
    val mediumCardPadding = 20.dp
    val smallCardPadding = 14.dp

    val buttonHeight = 78.dp
    val secondaryButtonHeight = 68.dp
    val iconTileSize = 72.dp

    val textPrimary: Color = QualityTextPrimary
    val textSecondary: Color = QualityTextSecondary
    val textTertiary: Color = QualityTextTertiary
    val onPrimary: Color = QualityOnPrimary

    val surface: Color = QualitySurface
    val surfaceAlt: Color = QualitySurfaceAlt
    val surfaceMuted: Color = QualitySurfaceMuted
    val surfaceSoftGreen: Color = QualityPrimarySoft
    val border: Color = QualityBorder
    val borderStrong: Color = QualityBorderStrong

    val mandatoryColor: Color = QualityAccentRed
    val advisoryColor: Color = QualityAccentOrange
    val ignoredColor: Color = QualityAccentGray
    val skippedColor: Color = QualityAccentPurple
    val passedColor: Color = QualityPrimaryDark
    val executedColor: Color = QualitySecondary

    val mandatoryContainer: Color = QualityErrorContainer
    val advisoryContainer: Color = QualityWarningContainer
    val ignoredContainer: Color = QualityGrayContainer
    val skippedContainer: Color = QualityPurpleContainer
    val passedContainer: Color = QualitySuccessContainer
    val executedContainer: Color = QualityInfoContainer
}
