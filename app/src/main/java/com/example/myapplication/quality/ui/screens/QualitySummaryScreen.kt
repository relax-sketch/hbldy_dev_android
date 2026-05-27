package com.example.myapplication.quality.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ViewList
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.PlayCircleOutline
import androidx.compose.material.icons.rounded.TipsAndUpdates
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.quality.review.ReviewedPlotResult
import com.example.myapplication.quality.ui.QualityCheckUiState
import com.example.myapplication.quality.ui.QualityCheckViewModel
import com.example.myapplication.quality.ui.components.QualityButtonPair
import com.example.myapplication.quality.ui.components.QualityEmptyStateCard
import com.example.myapplication.quality.ui.components.QualityIconTile
import com.example.myapplication.quality.ui.components.QualityPageScaffold
import com.example.myapplication.quality.ui.components.QualityPanelCard
import com.example.myapplication.quality.ui.components.QualitySelectableCard
import com.example.myapplication.quality.ui.components.QualityStatTile
import com.example.myapplication.quality.ui.components.QualityTagChip
import com.example.myapplication.quality.ui.design.QualityDesignTokens

@Composable
fun QualitySummaryScreen(
    state: QualityCheckUiState,
    viewModel: QualityCheckViewModel,
) {
    val reviewed = state.reviewedRun
    val summary = reviewed?.summary
    QualityPageScaffold(
        title = "质检结果",
        canNavigateBack = true,
        onNavigateBack = viewModel::navigateBack,
    ) { layout ->
        if (reviewed == null || summary == null) {
            QualityEmptyStateCard(title = "暂无质检结果", message = "完成一次批量质检后，会在这里查看汇总。")
            return@QualityPageScaffold
        }
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (reviewed.sourceRun.cancelled) {
                com.example.myapplication.quality.ui.components.QualityStatusCard(
                    text = "本次质检已取消，以下为取消前完成的样地结果",
                    tone = com.example.myapplication.quality.ui.components.QualityStatusTone.Warning,
                )
            }
            QualityPanelCard {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    QualityStatTile(
                        title = "强制性",
                        value = summary.pendingMandatoryIssues.toString(),
                        icon = Icons.Rounded.ErrorOutline,
                        tint = QualityDesignTokens.mandatoryColor,
                        containerColor = QualityDesignTokens.mandatoryContainer,
                        modifier = Modifier.then(if (layout.useTwoPane) Modifier.weight(1f) else Modifier),
                    )
                    QualityStatTile(
                        title = "提示性",
                        value = summary.pendingAdvisoryIssues.toString(),
                        icon = Icons.Rounded.TipsAndUpdates,
                        tint = QualityDesignTokens.advisoryColor,
                        containerColor = QualityDesignTokens.advisoryContainer,
                        modifier = Modifier.then(if (layout.useTwoPane) Modifier.weight(1f) else Modifier),
                    )
                    QualityStatTile(
                        title = "已忽略",
                        value = summary.ignoredIssues.toString(),
                        icon = Icons.Rounded.VisibilityOff,
                        tint = QualityDesignTokens.ignoredColor,
                        containerColor = QualityDesignTokens.ignoredContainer,
                        modifier = Modifier.then(if (layout.useTwoPane) Modifier.weight(1f) else Modifier),
                    )
                    QualityStatTile(
                        title = "已跳过规则",
                        value = summary.skippedRules.toString(),
                        icon = Icons.Rounded.PlayCircleOutline,
                        tint = QualityDesignTokens.skippedColor,
                        containerColor = QualityDesignTokens.skippedContainer,
                        modifier = Modifier.then(if (layout.useTwoPane) Modifier.weight(1f) else Modifier),
                    )
                    if (state.testMode) {
                        QualityStatTile(
                            title = "已通过规则",
                            value = summary.passedRules.toString(),
                            icon = Icons.Rounded.CheckCircle,
                            tint = QualityDesignTokens.passedColor,
                            containerColor = QualityDesignTokens.passedContainer,
                            modifier = Modifier.then(if (layout.useTwoPane) Modifier.weight(1f) else Modifier),
                        )
                        QualityStatTile(
                            title = "已执行规则",
                            value = summary.executedRules.toString(),
                            icon = Icons.AutoMirrored.Rounded.ViewList,
                            tint = QualityDesignTokens.executedColor,
                            containerColor = QualityDesignTokens.executedContainer,
                            modifier = Modifier.then(if (layout.useTwoPane) Modifier.weight(1f) else Modifier),
                        )
                    }
                }
            }
            LazyColumn(
                modifier = Modifier.weight(1f, fill = true),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                items(
                    items = reviewed.plotResults,
                    key = { "${it.plot.source.uri}|${it.plot.displayPlotId}|${it.plot.plotTable.name}" },
                ) { result ->
                    SummaryPlotCard(result = result, showPassedRules = state.testMode, onClick = { viewModel.showPlotDetails(result) })
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                QualityButtonPair(
                    primaryText = "再次质检",
                    onPrimaryClick = viewModel::recheck,
                    secondaryText = "更改范围",
                    onSecondaryClick = viewModel::showScopeSelection,
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SummaryPlotCard(
    result: ReviewedPlotResult,
    showPassedRules: Boolean,
    onClick: () -> Unit,
) {
    val accent = accentForPlot(result.plot)
    QualitySelectableCard(
        selected = false,
        enabled = true,
        onClick = onClick,
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        com.example.myapplication.quality.ui.components.QualityIconTile(
            icon = Icons.Rounded.Eco,
            tint = accent,
            containerColor = accent.copy(alpha = 0.12f),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "样地 ${result.plot.displayPlotId}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = QualityDesignTokens.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                QualityTagChip(
                    text = result.plot.countyLabel ?: "未知区县",
                    containerColor = accent.copy(alpha = 0.12f),
                    textColor = accent,
                )
                Text(
                    text = "›",
                    style = MaterialTheme.typography.headlineMedium,
                    color = QualityDesignTokens.textSecondary,
                )
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                QualityTagChip("强制 ${result.pendingMandatory.size}", containerColor = QualityDesignTokens.mandatoryContainer, textColor = QualityDesignTokens.mandatoryColor)
                QualityTagChip("提示 ${result.pendingAdvisory.size}", containerColor = QualityDesignTokens.advisoryContainer, textColor = QualityDesignTokens.advisoryColor)
                QualityTagChip("已忽略 ${result.ignored.size}", containerColor = QualityDesignTokens.ignoredContainer, textColor = QualityDesignTokens.ignoredColor)
                QualityTagChip("跳过 ${result.skippedRules.size}", containerColor = QualityDesignTokens.skippedContainer, textColor = QualityDesignTokens.skippedColor)
                if (showPassedRules) {
                    QualityTagChip("通过 ${result.passedRules.size}", containerColor = QualityDesignTokens.passedContainer, textColor = QualityDesignTokens.passedColor)
                }
            }
        }
    }
}
