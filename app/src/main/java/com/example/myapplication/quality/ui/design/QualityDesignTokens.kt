package com.example.myapplication.quality.ui.design

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.QualityAccentBlue
import com.example.myapplication.ui.theme.QualityAccentGray
import com.example.myapplication.ui.theme.QualityAccentOrange
import com.example.myapplication.ui.theme.QualityAccentPurple
import com.example.myapplication.ui.theme.QualityAccentRed
import com.example.myapplication.ui.theme.QualityBackground
import com.example.myapplication.ui.theme.QualityBackgroundWarm
import com.example.myapplication.ui.theme.QualityBorder
import com.example.myapplication.ui.theme.QualityBorderStrong
import com.example.myapplication.ui.theme.QualityErrorContainer
import com.example.myapplication.ui.theme.QualityGlass
import com.example.myapplication.ui.theme.QualityGlassSoft
import com.example.myapplication.ui.theme.QualityGrayContainer
import com.example.myapplication.ui.theme.QualityInfoContainer
import com.example.myapplication.ui.theme.QualityOnPrimary
import com.example.myapplication.ui.theme.QualityOnPrimaryFixed
import com.example.myapplication.ui.theme.QualityOutline
import com.example.myapplication.ui.theme.QualityPrimary
import com.example.myapplication.ui.theme.QualityPrimaryContainer
import com.example.myapplication.ui.theme.QualityPrimaryDark
import com.example.myapplication.ui.theme.QualityPrimaryFixed
import com.example.myapplication.ui.theme.QualityPurpleContainer
import com.example.myapplication.ui.theme.QualitySecondary
import com.example.myapplication.ui.theme.QualitySuccessContainer
import com.example.myapplication.ui.theme.QualitySurface
import com.example.myapplication.ui.theme.QualitySurfaceContainer
import com.example.myapplication.ui.theme.QualitySurfaceContainerLow
import com.example.myapplication.ui.theme.QualityTextPrimary
import com.example.myapplication.ui.theme.QualityTextSecondary
import com.example.myapplication.ui.theme.QualityTextTertiary
import com.example.myapplication.ui.theme.QualityWarningContainer

object QualityDesignTokens {
    val pageBrush: Brush = Brush.verticalGradient(
        listOf(QualityBackground, QualityBackgroundWarm),
    )

    val sourceHeroBrush: Brush = Brush.verticalGradient(
        listOf(Color(0xFF75CBE8), Color(0xFFDDF9E9)),
    )

    val primaryButtonBrush: Brush = Brush.horizontalGradient(
        listOf(QualityPrimary, Color(0xE6369D87)),
    )

    val primaryFixedButtonBrush: Brush = Brush.horizontalGradient(
        listOf(QualityPrimaryFixed, Color(0xFF86EFC8)),
    )

    val glassShape = RoundedCornerShape(12.dp)
    val innerShape = RoundedCornerShape(8.dp)
    val buttonShape = RoundedCornerShape(12.dp)
    val pillShape = RoundedCornerShape(999.dp)
    val dockShape = RoundedCornerShape(0.dp)

    val topBarHeight = 64.dp
    val bottomDockHeight = 80.dp
    val horizontalMargin = 20.dp
    val cardPadding = 16.dp
    val largeCardPadding = 18.dp
    val sectionGap = 12.dp
    val stackGap = 10.dp
    val buttonHeight = 56.dp
    val floatingButtonHeight = 58.dp
    val iconButtonSize = 40.dp
    val listIconSize = 40.dp
    val sourceIconSize = 48.dp

    val textPrimary: Color = QualityTextPrimary
    val textSecondary: Color = QualityTextSecondary
    val textTertiary: Color = QualityTextTertiary
    val onPrimary: Color = QualityOnPrimary
    val onPrimaryFixed: Color = QualityOnPrimaryFixed

    val background: Color = QualityBackground
    val warmBackground: Color = QualityBackgroundWarm
    val surface: Color = QualitySurface
    val glass: Color = QualityGlass
    val glassSoft: Color = QualityGlassSoft
    val surfaceContainer: Color = QualitySurfaceContainer
    val surfaceContainerLow: Color = QualitySurfaceContainerLow
    val border: Color = QualityBorder
    val borderStrong: Color = QualityBorderStrong
    val outline: Color = QualityOutline

    val primary: Color = QualityPrimary
    val primaryDark: Color = QualityPrimaryDark
    val primaryFixed: Color = QualityPrimaryFixed
    val primaryContainer: Color = QualityPrimaryContainer
    val secondary: Color = QualitySecondary

    val mandatoryColor: Color = QualityAccentRed
    val advisoryColor: Color = QualityAccentOrange
    val ignoredColor: Color = QualityAccentGray
    val skippedColor: Color = QualityAccentPurple
    val passedColor: Color = QualityPrimary
    val executedColor: Color = QualityAccentBlue

    val mandatoryContainer: Color = QualityErrorContainer
    val advisoryContainer: Color = QualityWarningContainer
    val ignoredContainer: Color = QualityGrayContainer
    val skippedContainer: Color = QualityPurpleContainer
    val passedContainer: Color = QualitySuccessContainer
    val executedContainer: Color = QualityInfoContainer
}
