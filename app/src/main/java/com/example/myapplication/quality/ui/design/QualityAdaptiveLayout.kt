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
    val verticalSpacing: Dp,
    val topPadding: Dp,
    val topBarHeight: Dp,
    val contentMaxWidth: Dp,
    val useTwoPane: Boolean,
    val isShortScreen: Boolean,
    val isLandscape: Boolean,
)

@Composable
fun rememberQualityLayoutSpec(): QualityLayoutSpec {
    val configuration = LocalConfiguration.current
    val widthDp = configuration.screenWidthDp
    val heightDp = configuration.screenHeightDp
    val isLandscape = widthDp > heightDp
    val isShortScreen = heightDp in 1..739
    val isWideLandscape = isLandscape && heightDp < 560
    fun widthFraction(fraction: Float): Dp = (widthDp * fraction).roundToInt().dp

    return when {
        widthDp >= 960 -> QualityLayoutSpec(
            windowSize = QualityWindowSize.Expanded,
            horizontalPadding = if (isShortScreen) 28.dp else 34.dp,
            verticalSpacing = if (isShortScreen) 16.dp else 18.dp,
            topPadding = if (isShortScreen) 10.dp else 14.dp,
            topBarHeight = if (isShortScreen) 72.dp else 76.dp,
            contentMaxWidth = minOf(widthFraction(0.92f), 1080.dp),
            useTwoPane = !isWideLandscape,
            isShortScreen = isShortScreen,
            isLandscape = isLandscape,
        )

        widthDp >= 600 -> QualityLayoutSpec(
            windowSize = QualityWindowSize.Medium,
            horizontalPadding = if (isShortScreen) 22.dp else 26.dp,
            verticalSpacing = if (isShortScreen) 14.dp else 16.dp,
            topPadding = if (isShortScreen) 8.dp else 12.dp,
            topBarHeight = if (isShortScreen) 68.dp else 72.dp,
            contentMaxWidth = minOf(widthFraction(0.92f), 820.dp),
            useTwoPane = !isWideLandscape && heightDp >= 560,
            isShortScreen = isShortScreen,
            isLandscape = isLandscape,
        )

        else -> QualityLayoutSpec(
            windowSize = QualityWindowSize.Compact,
            horizontalPadding = if (isShortScreen) 16.dp else 18.dp,
            verticalSpacing = if (isShortScreen) 12.dp else 14.dp,
            topPadding = if (isShortScreen) 6.dp else 10.dp,
            topBarHeight = if (isShortScreen) 64.dp else 68.dp,
            contentMaxWidth = minOf(widthFraction(0.92f), 520.dp),
            useTwoPane = false,
            isShortScreen = isShortScreen,
            isLandscape = isLandscape,
        )
    }
}
