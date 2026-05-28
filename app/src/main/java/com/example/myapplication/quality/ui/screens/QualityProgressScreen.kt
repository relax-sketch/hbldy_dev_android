package com.example.myapplication.quality.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.quality.check.CheckProgress
import com.example.myapplication.quality.ui.QualityCheckUiState
import com.example.myapplication.quality.ui.QualityCheckViewModel
import com.example.myapplication.quality.ui.components.QualityPageScaffold
import com.example.myapplication.quality.ui.components.QualitySecondaryButton
import com.example.myapplication.quality.ui.design.QualityDesignTokens

@Composable
fun QualityProgressScreen(
    state: QualityCheckUiState,
    viewModel: QualityCheckViewModel,
) {
    val progress = state.progress
    val ratio = if (progress == null || progress.totalPlots == 0) {
        0f
    } else {
        progress.completedPlots.toFloat() / progress.totalPlots
    }
    QualityPageScaffold(
        title = "",
        showTopBar = false,
        showDock = false,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            ProgressAmbient()
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(64.dp),
            ) {
                Text(
                    text = "正在质检样地...",
                    style = MaterialTheme.typography.headlineLarge,
                    color = QualityDesignTokens.textPrimary,
                )
                LiquidProgress(progress = progress, ratio = ratio)
                QualitySecondaryButton(
                    text = "取消",
                    onClick = viewModel::cancelCheck,
                    leadingIcon = Icons.Rounded.Close,
                    accentColor = QualityDesignTokens.mandatoryColor,
                    pill = true,
                    modifier = Modifier.size(width = 160.dp, height = 56.dp),
                )
            }
        }
    }
}

@Composable
private fun LiquidProgress(
    progress: CheckProgress?,
    ratio: Float,
) {
    Box(
        modifier = Modifier.size(288.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 14.dp.toPx()
            drawCircle(
                color = QualityDesignTokens.glass,
                radius = size.minDimension / 2f,
                center = center,
            )
            drawCircle(
                color = QualityDesignTokens.borderStrong,
                radius = size.minDimension / 2f - stroke / 2f,
                center = center,
                style = Stroke(width = stroke),
            )
            drawArc(
                color = QualityDesignTokens.primaryFixed,
                startAngle = 160f,
                sweepAngle = 220f * ratio.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = Offset(stroke / 2f, stroke / 2f),
                size = androidx.compose.ui.geometry.Size(size.width - stroke, size.height - stroke),
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            drawCircle(
                color = QualityDesignTokens.primaryFixed.copy(alpha = 0.45f),
                radius = size.minDimension * 0.31f,
                center = Offset(center.x, center.y + size.minDimension * 0.22f),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "${progress?.completedPlots ?: 0}",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = QualityDesignTokens.textPrimary,
                )
                Text(
                    text = "/${progress?.totalPlots ?: 0}",
                    style = MaterialTheme.typography.titleLarge,
                    color = QualityDesignTokens.textTertiary,
                    modifier = Modifier.padding(bottom = 5.dp),
                )
            }
            Row(
                modifier = Modifier
                    .background(QualityDesignTokens.glass, QualityDesignTokens.pillShape)
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Icons.Rounded.QrCodeScanner, contentDescription = null, tint = QualityDesignTokens.primary, modifier = Modifier.size(18.dp))
                Text(
                    text = "当前：${progress?.currentPlot?.displayPlotId ?: "--"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = QualityDesignTokens.textSecondary,
                )
            }
        }
    }
}

@Composable
private fun ProgressAmbient() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color = QualityDesignTokens.primaryContainer.copy(alpha = 0.38f),
            radius = size.minDimension * 0.68f,
            center = Offset(size.width * 0.5f, size.height * 0.22f),
        )
        drawCircle(
            color = Color(0xFFFFEB9E).copy(alpha = 0.42f),
            radius = size.minDimension * 0.58f,
            center = Offset(size.width * 0.5f, size.height * 0.88f),
        )
    }
}
