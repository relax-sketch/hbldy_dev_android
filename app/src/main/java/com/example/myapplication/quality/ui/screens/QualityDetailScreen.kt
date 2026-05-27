package com.example.myapplication.quality.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.RemoveCircle
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.quality.domain.CheckIssue
import com.example.myapplication.quality.domain.CheckScope
import com.example.myapplication.quality.domain.PassedRule
import com.example.myapplication.quality.domain.SkippedRule
import com.example.myapplication.quality.review.DetailFilterCounts
import com.example.myapplication.quality.review.DetailStatusFilter
import com.example.myapplication.quality.review.QualityTableGroup
import com.example.myapplication.quality.review.filteredDetail
import com.example.myapplication.quality.rules.RuleSeverity
import com.example.myapplication.quality.ui.QualityCheckUiState
import com.example.myapplication.quality.ui.QualityCheckViewModel
import com.example.myapplication.quality.ui.severityLabel
import com.example.myapplication.quality.ui.components.QualityButtonPair
import com.example.myapplication.quality.ui.components.QualityEmptyStateCard
import com.example.myapplication.quality.ui.components.QualityFilterChip
import com.example.myapplication.quality.ui.components.QualityIconTile
import com.example.myapplication.quality.ui.components.QualityPageScaffold
import com.example.myapplication.quality.ui.components.QualityPanelCard
import com.example.myapplication.quality.ui.components.QualityTagChip
import com.example.myapplication.quality.ui.design.QualityDesignTokens

@Composable
fun QualityDetailScreen(
    state: QualityCheckUiState,
    viewModel: QualityCheckViewModel,
) {
    val result = state.detailPlot
    QualityPageScaffold(
        title = "样地详情",
        canNavigateBack = true,
        onNavigateBack = viewModel::navigateBack,
    ) { layout ->
        if (result == null) {
            QualityEmptyStateCard(title = "未选择样地结果", message = "请先从质检结果中选择一个样地。")
            return@QualityPageScaffold
        }
        val filtered = result.filteredDetail(
            statusFilter = state.detailStatusFilter,
            tableGroup = state.detailTableGroup,
            includePassed = state.testMode,
        )
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            QualityPanelCard {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    QualityIconTile(
                        icon = Icons.Rounded.Eco,
                        tint = accentForPlot(result.plot),
                        containerColor = accentForPlot(result.plot).copy(alpha = 0.12f),
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                        Text(
                            text = "样地 ${result.plot.displayPlotId}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = QualityDesignTokens.textPrimary,
                        )
                        Text(
                            text = result.detailCountText,
                            style = MaterialTheme.typography.titleMedium,
                            color = QualityDesignTokens.textSecondary,
                        )
                        HorizontalDivider(color = QualityDesignTokens.border)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        ) {
                            QualityTagChip(
                                text = result.plot.countyLabel ?: "未知区县",
                                containerColor = accentForPlot(result.plot).copy(alpha = 0.12f),
                                textColor = accentForPlot(result.plot),
                            )
                            QualityTagChip(
                                text = "已执行规则 ${result.executedRuleCount}",
                                containerColor = QualityDesignTokens.surfaceSoftGreen,
                                textColor = QualityDesignTokens.passedColor,
                                borderColor = QualityDesignTokens.passedColor.copy(alpha = 0.25f),
                            )
                        }
                    }
                }
            }
            DetailFilterRow(
                counts = filtered.counts,
                selected = state.detailStatusFilter,
                onSelected = viewModel::setDetailStatusFilter,
            )
            TableGroupFilterRow(
                selected = state.detailTableGroup,
                onSelected = viewModel::setDetailTableGroup,
            )
            LazyColumn(
                modifier = Modifier.weight(1f, fill = true),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                if (filtered.visibleCount == 0) {
                    item {
                        QualityEmptyStateCard(
                            title = "当前筛选无结果",
                            message = "调整状态筛选或表分组后再查看。",
                        )
                    }
                }
                issueSection(
                    heading = "待处理强制性问题",
                    icon = Icons.Rounded.ErrorOutline,
                    tint = QualityDesignTokens.mandatoryColor,
                    issues = if (state.detailStatusFilter in listOf(DetailStatusFilter.ALL, DetailStatusFilter.FAILED, DetailStatusFilter.MANDATORY)) filtered.pendingMandatory else emptyList(),
                    emptyText = "无强制性问题",
                    onIgnore = { viewModel.setIgnored(it.fingerprint, true) },
                )
                issueSection(
                    heading = "待处理提示性问题",
                    icon = Icons.Rounded.WarningAmber,
                    tint = QualityDesignTokens.advisoryColor,
                    issues = if (state.detailStatusFilter in listOf(DetailStatusFilter.ALL, DetailStatusFilter.FAILED, DetailStatusFilter.ADVISORY)) filtered.pendingAdvisory else emptyList(),
                    emptyText = "无提示性问题",
                    onIgnore = { viewModel.setIgnored(it.fingerprint, true) },
                )
                if (filtered.skippedRules.isNotEmpty()) {
                    item { SectionTitle("无法执行的规则", Icons.Rounded.Info, QualityDesignTokens.ignoredColor, filtered.skippedRules.size.toString()) }
                    items(filtered.skippedRules, key = { it.ruleId }) { skipped -> SkippedRuleCard(skipped) }
                }
                if (filtered.passedRules.isNotEmpty()) {
                    item { SectionTitle("已通过规则（测试模式）", Icons.Rounded.CheckCircle, QualityDesignTokens.passedColor, filtered.passedRules.size.toString()) }
                    items(filtered.passedRules, key = { it.ruleId }) { passed -> PassedRuleCard(passed) }
                }
                if (filtered.ignored.isNotEmpty()) {
                    item { SectionTitle("已忽略（视为通过）", Icons.Rounded.RemoveCircle, QualityDesignTokens.ignoredColor, filtered.ignored.size.toString()) }
                    items(filtered.ignored, key = { it.fingerprint }) { issue ->
                        IssueCard(issue = issue, actionLabel = "取消忽略") {
                            viewModel.setIgnored(issue.fingerprint, false)
                        }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                QualityButtonPair(
                    primaryText = "再次质检",
                    onPrimaryClick = viewModel::recheck,
                    secondaryText = if (state.reviewedRun?.sourceRun?.scope is CheckScope.Single) "返回范围" else "返回汇总",
                    onSecondaryClick = viewModel::navigateBack,
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun DetailFilterRow(
    counts: DetailFilterCounts,
    selected: DetailStatusFilter,
    onSelected: (DetailStatusFilter) -> Unit,
) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        DetailStatusFilter.values().forEach { filter ->
            val count = when (filter) {
                DetailStatusFilter.ALL -> counts.all
                DetailStatusFilter.FAILED -> counts.failed
                DetailStatusFilter.MANDATORY -> counts.mandatory
                DetailStatusFilter.ADVISORY -> counts.advisory
                DetailStatusFilter.IGNORED -> counts.ignored
            }
            QualityFilterChip(
                text = "${filter.label} $count",
                selected = selected == filter,
                onClick = { onSelected(filter) },
            )
        }
    }
}

@Composable
private fun TableGroupFilterRow(
    selected: QualityTableGroup,
    onSelected: (QualityTableGroup) -> Unit,
) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        QualityTableGroup.values().forEach { group ->
            QualityFilterChip(
                text = group.label,
                selected = selected == group,
                onClick = { onSelected(group) },
            )
        }
    }
}

private fun LazyListScope.issueSection(
    heading: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    issues: List<CheckIssue>,
    emptyText: String,
    onIgnore: (CheckIssue) -> Unit,
) {
    item { SectionTitle(heading, icon, tint, issues.size.toString()) }
    if (issues.isEmpty()) {
        item {
            Text(
                text = emptyText,
                style = MaterialTheme.typography.bodyMedium,
                color = QualityDesignTokens.textSecondary,
            )
        }
    } else {
        items(issues, key = { it.fingerprint }) { issue ->
            IssueCard(issue = issue, actionLabel = "标注忽略") { onIgnore(issue) }
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    count: String,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = tint)
        Text(title, style = MaterialTheme.typography.titleLarge, color = QualityDesignTokens.textPrimary)
        QualityTagChip(
            text = count,
            containerColor = Color.White.copy(alpha = 0.7f),
            textColor = QualityDesignTokens.textSecondary,
        )
    }
}

@Composable
private fun IssueCard(
    issue: CheckIssue,
    actionLabel: String,
    onAction: () -> Unit,
) {
    val accent = when {
        issue.ignored -> QualityDesignTokens.ignoredColor
        issue.severity == RuleSeverity.MANDATORY -> QualityDesignTokens.mandatoryColor
        else -> QualityDesignTokens.advisoryColor
    }
    val containerColor = when {
        issue.ignored -> QualityDesignTokens.ignoredContainer.copy(alpha = 0.45f)
        issue.severity == RuleSeverity.MANDATORY -> QualityDesignTokens.mandatoryContainer.copy(alpha = 0.4f)
        else -> QualityDesignTokens.advisoryContainer.copy(alpha = 0.35f)
    }
    QualityPanelCard(
        containerColor = QualityDesignTokens.surface,
        borderColor = accent.copy(alpha = 0.34f),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.Top,
        ) {
            QualityTagChip(
                text = severityLabel(issue.severity),
                containerColor = containerColor,
                textColor = accent,
            )
            DetailActionChip(
                text = actionLabel,
                tint = accent,
                onClick = onAction,
            )
        }
        Text(issue.title, style = MaterialTheme.typography.titleLarge, color = QualityDesignTokens.textPrimary)
        Text("规则 ${issue.ruleId} · 表 ${issue.tableName}", style = MaterialTheme.typography.bodyMedium, color = QualityDesignTokens.textSecondary)
        Text("说明：${issue.explanation}", style = MaterialTheme.typography.bodyMedium, color = QualityDesignTokens.textSecondary)
        ValueBlock("定位值", issue.locationValues)
        ValueBlock("实际值", issue.actualValues)
    }
}

@Composable
private fun SkippedRuleCard(skipped: SkippedRule) {
    QualityPanelCard(borderColor = QualityDesignTokens.borderStrong) {
        Text(skipped.title, style = MaterialTheme.typography.titleLarge, color = QualityDesignTokens.textPrimary)
        Text("规则 ${skipped.ruleId} · 表 ${skipped.tableName}", style = MaterialTheme.typography.bodyMedium, color = QualityDesignTokens.textSecondary)
        Text(skipped.reason, style = MaterialTheme.typography.bodyMedium, color = QualityDesignTokens.textSecondary)
    }
}

@Composable
private fun PassedRuleCard(passed: PassedRule) {
    QualityPanelCard(
        containerColor = QualityDesignTokens.passedContainer.copy(alpha = 0.45f),
        borderColor = QualityDesignTokens.passedColor.copy(alpha = 0.28f),
    ) {
        Text(passed.title, style = MaterialTheme.typography.titleLarge, color = QualityDesignTokens.textPrimary)
        Text("规则 ${passed.ruleId} · 表 ${passed.tableName}", style = MaterialTheme.typography.bodyMedium, color = QualityDesignTokens.textSecondary)
        Text(passed.explanation, style = MaterialTheme.typography.bodyMedium, color = QualityDesignTokens.textSecondary)
    }
}

@Composable
private fun DetailActionChip(
    text: String,
    tint: Color,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        color = Color.White,
        contentColor = tint,
        shape = QualityDesignTokens.buttonShape,
        border = androidx.compose.foundation.BorderStroke(1.dp, tint.copy(alpha = 0.6f)),
        shadowElevation = 0.dp,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
            color = tint,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun ValueBlock(label: String, values: Map<String, String?>) {
    Text("$label：", style = MaterialTheme.typography.labelLarge, color = QualityDesignTokens.textSecondary)
    if (values.isEmpty()) {
        Text("无", style = MaterialTheme.typography.bodyMedium, color = QualityDesignTokens.textSecondary)
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            values.forEach { (key, value) ->
                Text(
                    text = "$key：${value?.ifBlank { "(空)" } ?: "(空)"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = QualityDesignTokens.textSecondary,
                )
            }
        }
    }
}
