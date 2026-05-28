package com.example.myapplication.quality.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.PlayCircleOutline
import androidx.compose.material.icons.rounded.TipsAndUpdates
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.quality.review.ReviewedPlotResult
import com.example.myapplication.quality.ui.QualityCheckUiState
import com.example.myapplication.quality.ui.QualityCheckViewModel
import com.example.myapplication.quality.ui.components.QualityButtonPair
import com.example.myapplication.quality.ui.components.QualityDockTab
import com.example.myapplication.quality.ui.components.QualityEmptyStateCard
import com.example.myapplication.quality.ui.components.QualityIconTile
import com.example.myapplication.quality.ui.components.QualityPageScaffold
import com.example.myapplication.quality.ui.components.QualitySelectableCard
import com.example.myapplication.quality.ui.components.QualityStatTile
import com.example.myapplication.quality.ui.components.QualityStatusCard
import com.example.myapplication.quality.ui.components.QualityStatusTone
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
        showDock = true,
        selectedDockTab = QualityDockTab.INSPECT,
        onNavigateBack = viewModel::navigateBack,
        onDockTabClick = { tab ->
            when (tab) {
                QualityDockTab.PLOTS -> viewModel.showScopeSelection()
                QualityDockTab.INSPECT,
                QualityDockTab.STATS,
                -> Unit
                QualityDockTab.SETTINGS -> viewModel.showSettings()
            }
        },
        floatingAction = {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                QualityButtonPair(
                    primaryText = "改变范围",
                    onPrimaryClick = viewModel::showScopeSelection,
                    secondaryText = "再次质检",
                    onSecondaryClick = viewModel::recheck,
                )
            }
        },
        bottomPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 118.dp),
    ) { layout ->
        if (reviewed == null || summary == null) {
            QualityEmptyStateCard(
                title = "暂无质检结果",
                message = "完成一次质检后，可在这里查看汇总和样地明细。",
            )
            return@QualityPageScaffold
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 24.dp, bottom = 14.dp),
        ) {
            if (reviewed.sourceRun.cancelled) {
                item {
                    QualityStatusCard(
                        text = "本次质检已取消，下方展示取消前完成的结果。",
                        tone = QualityStatusTone.Warning,
                    )
                }
            }
            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    maxItemsInEachRow = if (layout.useTwoPane) 4 else 2,
                ) {
                    val tileModifier = Modifier.weight(1f)
                    QualityStatTile(
                        title = "强制性",
                        value = summary.pendingMandatoryIssues.toString(),
                        subtitle = "待处理问题",
                        icon = Icons.Rounded.ErrorOutline,
                        tint = QualityDesignTokens.mandatoryColor,
                        modifier = tileModifier,
                    )
                    QualityStatTile(
                        title = "提示性",
                        value = summary.pendingAdvisoryIssues.toString(),
                        subtitle = "需要复核",
                        icon = Icons.Rounded.TipsAndUpdates,
                        tint = QualityDesignTokens.advisoryColor,
                        modifier = tileModifier,
                    )
                    QualityStatTile(
                        title = "已忽略",
                        value = summary.ignoredIssues.toString(),
                        subtitle = "视为通过",
                        icon = Icons.Rounded.VisibilityOff,
                        tint = QualityDesignTokens.ignoredColor,
                        modifier = tileModifier,
                    )
                    QualityStatTile(
                        title = "已跳过",
                        value = summary.skippedRules.toString(),
                        subtitle = "未执行规则",
                        icon = Icons.Rounded.PlayCircleOutline,
                        tint = QualityDesignTokens.skippedColor,
                        modifier = tileModifier,
                    )
                    if (state.testMode) {
                        QualityStatTile(
                            title = "已通过",
                            value = summary.passedRules.toString(),
                            subtitle = "通过规则",
                            icon = Icons.Rounded.CheckCircle,
                            tint = QualityDesignTokens.passedColor,
                            modifier = tileModifier,
                        )
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("样地汇总", style = MaterialTheme.typography.titleLarge, color = QualityDesignTokens.textPrimary)
                    Text("${reviewed.plotResults.size} 个样地", style = MaterialTheme.typography.labelLarge, color = QualityDesignTokens.textSecondary)
                }
            }
            items(
                items = reviewed.plotResults,
                key = { "${it.plot.source.uri}|${it.plot.displayPlotId}|${it.plot.plotTable.name}" },
            ) { result ->
                SummaryPlotCard(
                    result = result,
                    showPassedRules = state.testMode,
                    onClick = { viewModel.showPlotDetails(result) },
                )
            }
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
    QualitySelectableCard(selected = false, enabled = true, onClick = onClick) {
        QualityIconTile(
            icon = Icons.Rounded.CheckCircle,
            tint = accent,
            containerColor = accent.copy(alpha = 0.12f),
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "ID：${result.plot.displayPlotId}",
                style = MaterialTheme.typography.titleMedium,
                color = QualityDesignTokens.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "区县：${result.plot.countyLabel ?: "未知区县"}",
                style = MaterialTheme.typography.titleLarge,
                color = QualityDesignTokens.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QualityTagChip("强制 ${result.pendingMandatory.size}", containerColor = QualityDesignTokens.mandatoryContainer, textColor = QualityDesignTokens.mandatoryColor)
                QualityTagChip("提示 ${result.pendingAdvisory.size}", containerColor = QualityDesignTokens.advisoryContainer, textColor = QualityDesignTokens.advisoryColor)
                QualityTagChip("忽略 ${result.ignored.size}", containerColor = QualityDesignTokens.ignoredContainer, textColor = QualityDesignTokens.ignoredColor)
                QualityTagChip("跳过 ${result.skippedRules.size}", containerColor = QualityDesignTokens.skippedContainer, textColor = QualityDesignTokens.skippedColor)
                if (showPassedRules) {
                    QualityTagChip("通过 ${result.passedRules.size}", containerColor = QualityDesignTokens.passedContainer, textColor = QualityDesignTokens.passedColor)
                }
            }
        }
        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = QualityDesignTokens.textTertiary)
    }
}
