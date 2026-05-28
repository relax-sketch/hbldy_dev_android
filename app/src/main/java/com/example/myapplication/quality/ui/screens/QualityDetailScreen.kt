package com.example.myapplication.quality.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.TableRows
import androidx.compose.material.icons.rounded.TipsAndUpdates
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.quality.domain.CheckIssue
import com.example.myapplication.quality.domain.PassedRule
import com.example.myapplication.quality.domain.SkippedRule
import com.example.myapplication.quality.review.DetailFilterCounts
import com.example.myapplication.quality.review.DetailStatusFilter
import com.example.myapplication.quality.review.QualityTableGroup
import com.example.myapplication.quality.review.ReviewedPlotResult
import com.example.myapplication.quality.review.filteredDetail
import com.example.myapplication.quality.rules.RuleSeverity
import com.example.myapplication.quality.ui.QualityCheckUiState
import com.example.myapplication.quality.ui.QualityCheckViewModel
import com.example.myapplication.quality.ui.components.QualityEmptyStateCard
import com.example.myapplication.quality.ui.components.QualityFilterChip
import com.example.myapplication.quality.ui.components.QualityGlassCard
import com.example.myapplication.quality.ui.components.QualityPageScaffold
import com.example.myapplication.quality.ui.components.QualityPrimaryButton
import com.example.myapplication.quality.ui.components.QualitySecondaryButton
import com.example.myapplication.quality.ui.components.QualityStatTile
import com.example.myapplication.quality.ui.components.QualityTagChip
import com.example.myapplication.quality.ui.design.QualityDesignTokens
import com.example.myapplication.quality.ui.severityLabel

@Composable
fun QualityDetailScreen(
    state: QualityCheckUiState,
    viewModel: QualityCheckViewModel,
) {
    val result = state.detailPlot
    QualityPageScaffold(
        title = result?.let { "样地详情 - ${it.plot.displayPlotId}" } ?: "样地详情",
        canNavigateBack = true,
        showDock = false,
        onNavigateBack = viewModel::navigateBack,
        bottomPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 0.dp),
    ) {
        if (result == null) {
            QualityEmptyStateCard(title = "未选择样地结果", message = "请先从质检结果中选择一个样地。")
            return@QualityPageScaffold
        }
        val filtered = result.filteredDetail(
            statusFilter = state.detailStatusFilter,
            tableGroup = state.detailTableGroup,
            includePassed = state.testMode,
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 14.dp, bottom = 32.dp),
        ) {
            item {
                DetailStats(result)
            }
            item {
                DetailStatusFilterRow(
                    counts = filtered.counts,
                    selected = state.detailStatusFilter,
                    onSelected = viewModel::setDetailStatusFilter,
                )
            }
            item {
                TableGroupFilterRow(
                    selected = state.detailTableGroup,
                    onSelected = viewModel::setDetailTableGroup,
                )
            }
            if (filtered.visibleCount == 0) {
                item {
                    QualityEmptyStateCard(
                        title = "当前筛选无结果",
                        message = "调整状态筛选或表分组后再查看。",
                    )
                }
            }
            item {
                IssueSection(
                    title = "待处理强制性问题",
                    icon = Icons.Rounded.ErrorOutline,
                    tint = QualityDesignTokens.mandatoryColor,
                    count = filtered.pendingMandatory.size,
                ) {
                    filtered.pendingMandatory.forEach { issue ->
                        IssueCard(issue = issue, actionLabel = "忽略", onAction = { viewModel.setIgnored(issue.fingerprint, true) })
                    }
                }
            }
            item {
                IssueSection(
                    title = "待处理提示性问题",
                    icon = Icons.Rounded.TipsAndUpdates,
                    tint = QualityDesignTokens.advisoryColor,
                    count = filtered.pendingAdvisory.size,
                ) {
                    filtered.pendingAdvisory.forEach { issue ->
                        IssueCard(issue = issue, actionLabel = "忽略", onAction = { viewModel.setIgnored(issue.fingerprint, true) })
                    }
                }
            }
            item {
                IssueSection(
                    title = "已忽略",
                    icon = Icons.Rounded.VisibilityOff,
                    tint = QualityDesignTokens.ignoredColor,
                    count = filtered.ignored.size,
                ) {
                    filtered.ignored.forEach { issue ->
                        IssueCard(issue = issue, actionLabel = "恢复", ignored = true, onAction = { viewModel.setIgnored(issue.fingerprint, false) })
                    }
                }
            }
            item {
                IssueSection(
                    title = "无法执行的规则",
                    icon = Icons.Rounded.TableRows,
                    tint = QualityDesignTokens.skippedColor,
                    count = filtered.skippedRules.size,
                ) {
                    filtered.skippedRules.forEach { rule ->
                        SkippedRuleCard(rule)
                    }
                }
            }
            if (state.testMode) {
                item {
                    IssueSection(
                        title = "已通过规则",
                        icon = Icons.Rounded.CheckCircle,
                        tint = QualityDesignTokens.passedColor,
                        count = filtered.passedRules.size,
                    ) {
                        filtered.passedRules.forEach { rule ->
                            PassedRuleCard(rule)
                        }
                    }
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    QualityPrimaryButton(
                        text = "重新质检",
                        onClick = viewModel::recheck,
                        leadingIcon = Icons.Rounded.Refresh,
                    )
                    QualitySecondaryButton(
                        text = "返回结果",
                        onClick = viewModel::navigateBack,
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailStats(result: ReviewedPlotResult) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        QualityStatTile(
            title = "强制性",
            value = result.pendingMandatory.size.toString(),
            icon = Icons.Rounded.ErrorOutline,
            tint = QualityDesignTokens.mandatoryColor,
            modifier = Modifier.weight(1f),
        )
        QualityStatTile(
            title = "提示性",
            value = result.pendingAdvisory.size.toString(),
            icon = Icons.Rounded.TipsAndUpdates,
            tint = QualityDesignTokens.advisoryColor,
            modifier = Modifier.weight(1f),
        )
        QualityStatTile(
            title = "已忽略",
            value = result.ignored.size.toString(),
            icon = Icons.Rounded.VisibilityOff,
            tint = QualityDesignTokens.ignoredColor,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DetailStatusFilterRow(
    counts: DetailFilterCounts,
    selected: DetailStatusFilter,
    onSelected: (DetailStatusFilter) -> Unit,
) {
    QualityGlassCard(contentPadding = 8.dp) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
}

@Composable
private fun TableGroupFilterRow(
    selected: QualityTableGroup,
    onSelected: (QualityTableGroup) -> Unit,
) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        QualityTableGroup.values().forEach { group ->
            QualityFilterChip(
                text = group.label,
                selected = selected == group,
                onClick = { onSelected(group) },
                selectedContainer = QualityDesignTokens.primaryContainer,
                selectedText = QualityDesignTokens.primary,
            )
        }
    }
}

@Composable
private fun IssueSection(
    title: String,
    icon: ImageVector,
    tint: Color,
    count: Int,
    content: @Composable () -> Unit,
) {
    if (count == 0) return
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        content()
    }
}

@Composable
private fun IssueCard(
    issue: CheckIssue,
    actionLabel: String,
    ignored: Boolean = false,
    onAction: () -> Unit,
) {
    val tint = if (issue.severity == RuleSeverity.MANDATORY) QualityDesignTokens.mandatoryColor else QualityDesignTokens.advisoryColor
    QualityGlassCard(
        containerColor = QualityDesignTokens.glass,
        contentPadding = 14.dp,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            QualityTagChip(
                text = severityLabel(issue.severity),
                containerColor = tint.copy(alpha = 0.13f),
                textColor = tint,
            )
            Text(issue.ruleId, style = MaterialTheme.typography.bodyMedium, color = QualityDesignTokens.textSecondary)
            QualityTagChip(
                text = issue.tableName,
                containerColor = Color.White.copy(alpha = 0.45f),
                textColor = QualityDesignTokens.textSecondary,
                modifier = Modifier.weight(1f),
            )
        }
        Text(
            text = issue.title,
            style = MaterialTheme.typography.titleLarge,
            color = QualityDesignTokens.textPrimary,
            textDecoration = if (ignored) TextDecoration.LineThrough else TextDecoration.None,
        )
        Text(
            text = issue.explanation,
            style = MaterialTheme.typography.bodyMedium,
            color = QualityDesignTokens.textSecondary,
        )
        HorizontalDivider(color = QualityDesignTokens.borderStrong.copy(alpha = 0.4f))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("发现值", style = MaterialTheme.typography.labelLarge, color = QualityDesignTokens.textSecondary)
                Text(
                    text = issue.actualValues.values.firstOrNull().orEmpty().ifBlank { "NULL" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = tint,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(
                modifier = Modifier.clickable(onClick = onAction).padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    if (ignored) Icons.Rounded.Restore else Icons.Rounded.VisibilityOff,
                    contentDescription = actionLabel,
                    tint = QualityDesignTokens.primary,
                )
                Text(actionLabel, style = MaterialTheme.typography.titleMedium, color = QualityDesignTokens.primary)
            }
        }
    }
}

@Composable
private fun SkippedRuleCard(rule: SkippedRule) {
    RuleCard(
        label = "跳过",
        tint = QualityDesignTokens.skippedColor,
        ruleId = rule.ruleId,
        tableName = rule.tableName,
        title = rule.title,
        description = rule.reason,
    )
}

@Composable
private fun PassedRuleCard(rule: PassedRule) {
    RuleCard(
        label = "通过",
        tint = QualityDesignTokens.passedColor,
        ruleId = rule.ruleId,
        tableName = rule.tableName,
        title = rule.title,
        description = rule.explanation,
    )
}

@Composable
private fun RuleCard(
    label: String,
    tint: Color,
    ruleId: String,
    tableName: String,
    title: String,
    description: String,
) {
    QualityGlassCard(contentPadding = 14.dp) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            QualityTagChip(label, containerColor = tint.copy(alpha = 0.12f), textColor = tint)
            Text(ruleId, style = MaterialTheme.typography.bodyMedium, color = QualityDesignTokens.textSecondary)
            QualityTagChip(tableName, containerColor = Color.White.copy(alpha = 0.45f), textColor = QualityDesignTokens.textSecondary)
        }
        Text(title, style = MaterialTheme.typography.titleLarge, color = QualityDesignTokens.textPrimary)
        Text(description, style = MaterialTheme.typography.bodyMedium, color = QualityDesignTokens.textSecondary)
    }
}
