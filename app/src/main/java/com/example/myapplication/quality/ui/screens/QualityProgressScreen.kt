package com.example.myapplication.quality.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.quality.ui.QualityCheckUiState
import com.example.myapplication.quality.ui.QualityCheckViewModel
import com.example.myapplication.quality.ui.components.QualityDivider
import com.example.myapplication.quality.ui.components.QualityIconTile
import com.example.myapplication.quality.ui.components.QualityPageScaffold
import com.example.myapplication.quality.ui.components.QualityPanelCard
import com.example.myapplication.quality.ui.components.QualitySecondaryButton
import com.example.myapplication.quality.ui.components.QualityStatusCard
import com.example.myapplication.quality.ui.components.QualityStatusTone
import com.example.myapplication.quality.ui.design.QualityDesignTokens

@Composable
fun QualityProgressScreen(
    state: QualityCheckUiState,
    viewModel: QualityCheckViewModel,
) {
    val progress = state.progress
    QualityPageScaffold(
        title = "质检进行中",
        canNavigateBack = false,
        onNavigateBack = {},
    ) { layout ->
        val completionRatio = if (progress == null || progress.totalPlots == 0) 0f
        else progress.completedPlots.toFloat() / progress.totalPlots
        val completedPreview = emptyList<com.example.myapplication.quality.review.ReviewedPlotResult>()
        if (layout.useTwoPane) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(
                    modifier = Modifier.weight(0.48f),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    ProgressHeroCard(progress = progress, completionRatio = completionRatio)
                    QualityStatusCard(text = "正在以只读方式查询 ZDB 数据", tone = QualityStatusTone.Success)
                    QualityStatusCard(text = "取消后会保留已完成样地的结果", tone = QualityStatusTone.Warning)
                    Spacer(Modifier.weight(1f))
                    QualitySecondaryButton(
                        text = "取消本次质检",
                        onClick = viewModel::cancelCheck,
                        accentColor = QualityDesignTokens.mandatoryColor,
                    )
                }
                Column(
                    modifier = Modifier.weight(0.52f),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    RecentCompletedCard(completedPreview)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                ProgressHeroCard(progress = progress, completionRatio = completionRatio)
                QualityStatusCard(text = "正在以只读方式查询 ZDB 数据", tone = QualityStatusTone.Success)
                QualityStatusCard(text = "取消后会保留已完成样地的结果", tone = QualityStatusTone.Warning)
                RecentCompletedCard(completedPreview)
                Spacer(Modifier.weight(1f))
                QualitySecondaryButton(
                    text = "取消本次质检",
                    onClick = viewModel::cancelCheck,
                    accentColor = QualityDesignTokens.mandatoryColor,
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ProgressHeroCard(
    progress: com.example.myapplication.quality.check.CheckProgress?,
    completionRatio: Float,
) {
    QualityPanelCard {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            QualityIconTile(
                icon = Icons.Rounded.Description,
                tint = QualityDesignTokens.passedColor,
                containerColor = QualityDesignTokens.passedContainer,
            )
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = androidx.compose.ui.Alignment.Bottom) {
                    Text(
                        text = "已完成",
                        style = MaterialTheme.typography.headlineMedium,
                        color = QualityDesignTokens.textPrimary,
                    )
                    Text(
                        text = "${progress?.completedPlots ?: 0}",
                        style = MaterialTheme.typography.displayLarge,
                        color = QualityDesignTokens.passedColor,
                    )
                    Text(
                        text = "/ ${progress?.totalPlots ?: 0}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = QualityDesignTokens.textPrimary,
                    )
                }
            }
        }
        LinearProgressIndicator(
            progress = { completionRatio },
            modifier = Modifier.fillMaxWidth().height(10.dp),
            color = QualityDesignTokens.passedColor,
            trackColor = QualityDesignTokens.border,
        )
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text(
                text = "${(completionRatio * 100).toInt()}%",
                style = MaterialTheme.typography.headlineMedium,
                color = QualityDesignTokens.passedColor,
            )
        }
        progress?.currentPlot?.let {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                QualityIconTile(
                    icon = Icons.Rounded.Place,
                    tint = QualityDesignTokens.passedColor,
                    containerColor = QualityDesignTokens.passedContainer,
                    modifier = Modifier.padding(0.dp),
                )
                Text(
                    text = "当前样地：样地 ${it.displayPlotId}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = QualityDesignTokens.textPrimary,
                )
            }
        }
    }
}

@Composable
private fun RecentCompletedCard(
    completedPreview: List<com.example.myapplication.quality.review.ReviewedPlotResult>,
) {
    QualityPanelCard {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("最近完成", style = MaterialTheme.typography.titleLarge, color = QualityDesignTokens.textSecondary)
            Text("共 ${completedPreview.size} 条", style = MaterialTheme.typography.titleMedium, color = QualityDesignTokens.textTertiary)
        }
        if (completedPreview.isEmpty()) {
            Text(
                text = "当前进度状态暂未提供最近完成明细，完成后仍可查看完整结果。",
                style = MaterialTheme.typography.bodyMedium,
                color = QualityDesignTokens.textSecondary,
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(completedPreview) { result ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    ) {
                        QualityIconTile(
                            icon = Icons.Rounded.Eco,
                            tint = accentForPlot(result.plot),
                            containerColor = accentForPlot(result.plot).copy(alpha = 0.12f),
                        )
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "样地 ${result.plot.displayPlotId}",
                                style = MaterialTheme.typography.titleLarge,
                                color = QualityDesignTokens.textPrimary,
                            )
                            Text(
                                text = "已完成",
                                style = MaterialTheme.typography.bodyMedium,
                                color = QualityDesignTokens.textSecondary,
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            androidx.compose.material3.Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = QualityDesignTokens.passedColor)
                            Text("已完成", style = MaterialTheme.typography.titleMedium, color = QualityDesignTokens.textPrimary)
                        }
                    }
                    if (result != completedPreview.last()) {
                        QualityDivider()
                    }
                }
            }
        }
    }
}
