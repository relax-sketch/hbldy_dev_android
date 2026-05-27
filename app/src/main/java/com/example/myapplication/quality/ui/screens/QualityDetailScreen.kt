package com.example.myapplication.quality.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.RemoveCircle
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.myapplication.quality.domain.CheckIssue
import com.example.myapplication.quality.domain.CheckScope
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
import com.example.myapplication.quality.ui.components.QualityButtonPair
import com.example.myapplication.quality.ui.components.QualityEmptyStateCard
import com.example.myapplication.quality.ui.components.QualityFilterChip
import com.example.myapplication.quality.ui.components.QualityIconTile
import com.example.myapplication.quality.ui.components.QualityPageScaffold
import com.example.myapplication.quality.ui.components.QualityPanelCard
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
        title = "样地详情",
        canNavigateBack = true,
        onNavigateBack = viewModel::navigateBack,
        bottomBar = {
            QualityButtonPair(
                primaryText = "再次质检",
                onPrimaryClick = viewModel::recheck,
                secondaryText = if (state.reviewedRun?.sourceRun?.scope is CheckScope.Single) "返回范围" else "返回汇总",
                onSecondaryClick = viewModel::navigateBack,
            )
        },
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
        if (layout.isLandscape) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(layout.verticalSpacing),
            ) {
                Column(
                    modifier = Modifier
                        .weight(0.35f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    DetailSummaryCard(result = result)
                    SideTableGroupFilter(
                        selected = state.detailTableGroup,
                        onSelected = viewModel::setDetailTableGroup,
                    )
                }
                Column(
                    modifier = Modifier.weight(0.65f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    DetailStatusFilterRow(
                        counts = filtered.counts,
                        selected = state.detailStatusFilter,
                        onSelected = viewModel::setDetailStatusFilter,
                    )
                    DetailContentList(
                        filteredVisibleCount = filtered.visibleCount,
                        pendingMandatory = filtered.pendingMandatory,
                        pendingAdvisory = filtered.pendingAdvisory,
                        skippedRules = filtered.skippedRules,
                        passedRules = filtered.passedRules,
                        ignored = filtered.ignored,
                        onIgnore = { viewModel.setIgnored(it.fingerprint, true) },
                        onUnignore = { viewModel.setIgnored(it.fingerprint, false) },
                        modifier = Modifier.weight(1f, fill = true),
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                DetailSummaryCard(result = result)
                DetailStatusFilterRow(
                    counts = filtered.counts,
                    selected = state.detailStatusFilter,
                    onSelected = viewModel::setDetailStatusFilter,
                )
                TableGroupFilterRow(
                    selected = state.detailTableGroup,
                    onSelected = viewModel::setDetailTableGroup,
                )
                DetailContentList(
                    filteredVisibleCount = filtered.visibleCount,
                    pendingMandatory = filtered.pendingMandatory,
                    pendingAdvisory = filtered.pendingAdvisory,
                    skippedRules = filtered.skippedRules,
                    passedRules = filtered.passedRules,
                    ignored = filtered.ignored,
                    onIgnore = { viewModel.setIgnored(it.fingerprint, true) },
                    onUnignore = { viewModel.setIgnored(it.fingerprint, false) },
                    modifier = Modifier.weight(1f, fill = true),
                )
            }
        }
    }
}

@Composable
private fun DetailSummaryCard(result: ReviewedPlotResult) {
    val accent = accentForPlot(result.plot)
    QualityPanelCard {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            QualityIconTile(
                icon = Icons.Rounded.Eco,
                tint = accent,
                containerColor = accent.copy(alpha = 0.12f),
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                Text(
                    text = "样地 ${result.plot.displayPlotId}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = QualityDesignTokens.textPrimary,
                )
                Text(
                    text = "强:${result.pendingMandatory.size} | 提:${result.pendingAdvisory.size} | 跳:${result.skippedRules.size} | 忽:${result.ignored.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = QualityDesignTokens.textSecondary,
                )
                HorizontalDivider(color = QualityDesignTokens.border)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    QualityTagChip(
                        text = result.plot.countyLabel ?: "未知区县",
                        containerColor = accent.copy(alpha = 0.12f),
                        textColor = accent,
                    )
                    QualityTagChip(
                        text = "规则 ${result.executedRuleCount}",
                        containerColor = QualityDesignTokens.surfaceSoftGreen,
                        textColor = QualityDesignTokens.passedColor,
                        borderColor = QualityDesignTokens.passedColor.copy(alpha = 0.25f),
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailStatusFilterRow(
    counts: DetailFilterCounts,
    selected: DetailStatusFilter,
    onSelected: (DetailStatusFilter) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        QualityTableGroup.values().forEach { group ->
            QualityFilterChip(
                text = group.label,
                selected = selected == group,
                onClick = { onSelected(group) },
            )
        }
    }
}

@Composable
private fun SideTableGroupFilter(
    selected: QualityTableGroup,
    onSelected: (QualityTableGroup) -> Unit,
) {
    QualityPanelCard {
        Text("表单筛选", style = MaterialTheme.typography.titleMedium, color = QualityDesignTokens.textSecondary)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            QualityTableGroup.values().forEach { group ->
                QualityFilterChip(
                    text = group.label,
                    selected = selected == group,
                    onClick = { onSelected(group) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun DetailContentList(
    filteredVisibleCount: Int,
    pendingMandatory: List<CheckIssue>,
    pendingAdvisory: List<CheckIssue>,
    skippedRules: List<SkippedRule>,
    passedRules: List<PassedRule>,
    ignored: List<CheckIssue>,
    onIgnore: (CheckIssue) -> Unit,
    onUnignore: (CheckIssue) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (filteredVisibleCount == 0) {
            item {
                QualityEmptyStateCard(
                    title = "当前筛选无结果",
                    message = "调整状态筛选或表单分组后再查看。",
                )
            }
        }
        item {
            IssueAccordion(
                title = "待处理强制性问题",
                icon = Icons.Rounded.ErrorOutline,
                tint = QualityDesignTokens.mandatoryColor,
                count = pendingMandatory.size,
            ) {
                if (pendingMandatory.isEmpty()) {
                    EmptySectionText("无强制性问题")
                } else {
                    pendingMandatory.forEach { issue ->
                        IssueCard(issue = issue, actionLabel = "标注忽略") { onIgnore(issue) }
                    }
                }
            }
        }
        item {
            IssueAccordion(
                title = "待处理提示性问题",
                icon = Icons.Rounded.WarningAmber,
                tint = QualityDesignTokens.advisoryColor,
                count = pendingAdvisory.size,
            ) {
                if (pendingAdvisory.isEmpty()) {
                    EmptySectionText("无提示性问题")
                } else {
                    pendingAdvisory.forEach { issue ->
                        IssueCard(issue = issue, actionLabel = "标注忽略") { onIgnore(issue) }
                    }
                }
            }
        }
        if (skippedRules.isNotEmpty()) {
            items(skippedRules, key = { it.ruleId }) { skipped ->
                SkippedRuleCard(skipped)
            }
        }
        if (passedRules.isNotEmpty()) {
            items(passedRules, key = { it.ruleId }) { passed ->
                PassedRuleCard(passed)
            }
        }
        if (ignored.isNotEmpty()) {
            item {
                IssueAccordion(
                    title = "已忽略（视为通过）",
                    icon = Icons.Rounded.RemoveCircle,
                    tint = QualityDesignTokens.ignoredColor,
                    count = ignored.size,
                ) {
                    ignored.forEach { issue ->
                        IssueCard(issue = issue, actionLabel = "取消忽略") { onUnignore(issue) }
                    }
                }
            }
        }
    }
}

@Composable
private fun IssueAccordion(
    title: String,
    icon: ImageVector,
    tint: Color,
    count: Int,
    content: @Composable ColumnScope.() -> Unit,
) {
    var expanded by remember(title, count) { mutableStateOf(count > 0) }
    QualityPanelCard(borderColor = tint.copy(alpha = 0.22f)) {
        Surface(
            onClick = { expanded = !expanded },
            color = Color.Transparent,
            contentColor = QualityDesignTokens.textPrimary,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(icon, contentDescription = null, tint = tint)
                Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                QualityTagChip(
                    text = count.toString(),
                    containerColor = tint.copy(alpha = 0.1f),
                    textColor = tint,
                )
            }
        }
        if (expanded) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun EmptySectionText(text: String) {
    Text(text = text, style = MaterialTheme.typography.bodyMedium, color = QualityDesignTokens.textSecondary)
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
            verticalAlignment = Alignment.Top,
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
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Info, contentDescription = null, tint = QualityDesignTokens.ignoredColor)
            Text("无法执行的规则", style = MaterialTheme.typography.titleMedium, color = QualityDesignTokens.textSecondary)
        }
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
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = QualityDesignTokens.passedColor)
            Text("已通过规则", style = MaterialTheme.typography.titleMedium, color = QualityDesignTokens.textSecondary)
        }
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
        border = BorderStroke(1.dp, tint.copy(alpha = 0.6f)),
        shadowElevation = 0.dp,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
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
