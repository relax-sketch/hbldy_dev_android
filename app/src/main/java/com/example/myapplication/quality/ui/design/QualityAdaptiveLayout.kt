package com.example.myapplication.quality.ui.design

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
    val contentMaxWidth: Dp,
    val useTwoPane: Boolean,
)

@Composable
fun rememberQualityLayoutSpec(): QualityLayoutSpec {
    val widthDp = LocalConfiguration.current.screenWidthDp
    return when {
        widthDp >= 960 -> QualityLayoutSpec(
            windowSize = QualityWindowSize.Expanded,
            horizontalPadding = 44.dp,
            verticalSpacing = 24.dp,
            contentMaxWidth = 1180.dp,
            useTwoPane = true,
        )

        widthDp >= 600 -> QualityLayoutSpec(
            windowSize = QualityWindowSize.Medium,
            horizontalPadding = 30.dp,
            verticalSpacing = 20.dp,
            contentMaxWidth = 860.dp,
            useTwoPane = true,
        )

        else -> QualityLayoutSpec(
            windowSize = QualityWindowSize.Compact,
            horizontalPadding = 24.dp,
            verticalSpacing = 18.dp,
            contentMaxWidth = 560.dp,
            useTwoPane = false,
        )
    }
}
