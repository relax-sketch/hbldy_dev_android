package com.example.myapplication.quality.ui.design

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

enum class QualityWindowSize {
    Compact,
    Medium,
    Expanded,
}

@Immutable
data class QualityLayoutSpec(
    val windowSize: QualityWindowSize,
    val horizontalPadding: Dp,
    val contentMaxWidth: Dp,
    val verticalSpacing: Dp,
    val useTwoPane: Boolean,
    val isLandscape: Boolean,
    val isShortScreen: Boolean,
)

@Composable
fun rememberQualityLayoutSpec(): QualityLayoutSpec {
    val configuration = LocalConfiguration.current
    val widthDp = configuration.screenWidthDp
    val heightDp = configuration.screenHeightDp
    val isLandscape = widthDp > heightDp
    val isShort = heightDp in 1..739
    fun widthFraction(fraction: Float): Dp = (widthDp * fraction).roundToInt().dp

    return when {
        widthDp >= 960 -> QualityLayoutSpec(
            windowSize = QualityWindowSize.Expanded,
            horizontalPadding = 28.dp,
            contentMaxWidth = minOf(widthFraction(0.9f), 1120.dp),
            verticalSpacing = 20.dp,
            useTwoPane = true,
            isLandscape = isLandscape,
            isShortScreen = isShort,
        )

        widthDp >= 600 -> QualityLayoutSpec(
            windowSize = QualityWindowSize.Medium,
            horizontalPadding = 24.dp,
            contentMaxWidth = minOf(widthFraction(0.92f), 820.dp),
            verticalSpacing = 18.dp,
            useTwoPane = !isShort,
            isLandscape = isLandscape,
            isShortScreen = isShort,
        )

        else -> QualityLayoutSpec(
            windowSize = QualityWindowSize.Compact,
            horizontalPadding = QualityDesignTokens.horizontalMargin,
            contentMaxWidth = 520.dp,
            verticalSpacing = QualityDesignTokens.sectionGap,
            useTwoPane = false,
            isLandscape = isLandscape,
            isShortScreen = isShort,
        )
    }
}
