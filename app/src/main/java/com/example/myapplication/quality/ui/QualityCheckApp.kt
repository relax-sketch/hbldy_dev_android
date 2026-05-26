package com.example.myapplication.quality.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.quality.domain.CheckScope
import com.example.myapplication.quality.domain.CheckIssue
import com.example.myapplication.quality.domain.PlotRef
import com.example.myapplication.quality.domain.SkippedRule
import com.example.myapplication.quality.review.ReviewedPlotResult
import com.example.myapplication.quality.rules.RuleSeverity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QualityCheckApp(viewModel: QualityCheckViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(titleFor(state.screen)) },
                navigationIcon = {
                    when (state.screen) {
                        QualityScreen.SCOPE -> TextButton(onClick = viewModel::showSourceSelection) { Text("返回") }
                        QualityScreen.DETAIL -> TextButton(onClick = viewModel::showSummary) { Text("返回") }
                        else -> Unit
                    }
                },
            )
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            state.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
            when (state.screen) {
                QualityScreen.SOURCE -> SourceScreen(state, viewModel)
                QualityScreen.SCOPE -> ScopeScreen(state, viewModel)
                QualityScreen.PROGRESS -> ProgressScreen(state, viewModel)
                QualityScreen.SUMMARY -> SummaryScreen(state, viewModel)
                QualityScreen.DETAIL -> DetailScreen(state, viewModel)
            }
        }
    }
}

@Composable
private fun SourceScreen(state: QualityCheckUiState, viewModel: QualityCheckViewModel) {
    val directoryPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        uri?.let(viewModel::onDirectoryPicked)
    }
    Text("请选择“草原监测/数据”目录。应用将递归扫描其中的 .zdb，并始终以只读方式查询。")
    Button(
        onClick = { directoryPicker.launch(null) },
        enabled = !state.isScanning,
        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
    ) {
        Text("选择数据目录")
    }
    state.directory?.let { directory ->
        Text("当前目录：${directory.displayName}")
        OutlinedButton(
            onClick = viewModel::scanSavedDirectory,
            enabled = !state.isScanning,
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
        ) {
            Text("重新扫描当前目录")
        }
    }
    if (state.isScanning) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CircularProgressIndicator()
            Text("正在查找并读取 ZDB 结构...")
        }
    }
    state.scanResult?.let { result ->
        Text("找到可读取 ZDB：${result.validSources.size} 个")
        Text("索引样地：${state.indexedPlots.size} 个")
        if (state.rejectedSources.isNotEmpty()) {
            Text("无法使用的文件：${state.rejectedSources.size} 个", color = MaterialTheme.colorScheme.error)
        }
        if (state.indexedPlots.isNotEmpty()) {
            Button(
                onClick = viewModel::showScopeSelection,
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
            ) {
                Text("选择质检范围")
            }
        }
    }
}

@Composable
private fun ColumnScope.ScopeScreen(state: QualityCheckUiState, viewModel: QualityCheckViewModel) {
    var countyMenuExpanded by remember { mutableStateOf(false) }
    Text("已索引样地：${state.indexedPlots.size} 个；ZDB：${state.scanResult?.validSources?.size ?: 0} 个")
    Box {
        OutlinedButton(
            onClick = { countyMenuExpanded = true },
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
        ) {
            Text("区县：${state.selectedCounty ?: "全域"}")
        }
        DropdownMenu(
            expanded = countyMenuExpanded,
            onDismissRequest = { countyMenuExpanded = false },
        ) {
            DropdownMenuItem(
                text = { Text("全域") },
                onClick = {
                    countyMenuExpanded = false
                    viewModel.updateCountyFilter(null)
                },
            )
            state.countyOptions.forEach { county ->
                DropdownMenuItem(
                    text = { Text(county) },
                    onClick = {
                        countyMenuExpanded = false
                        viewModel.updateCountyFilter(county)
                    },
                )
            }
        }
    }
    OutlinedTextField(
        value = state.plotQuery,
        onValueChange = viewModel::updatePlotQuery,
        label = { Text("样地编号") },
        placeholder = { Text("输入编号筛选，或从列表选择") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
    Text("匹配样地：${state.filteredPlots.size} 个", style = MaterialTheme.typography.labelLarge)
    LazyColumn(
        modifier = Modifier.fillMaxWidth().weight(1f),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = state.filteredPlots,
            key = { "${it.source.uri}|${it.plotTable.name}|${it.displayPlotId}" },
        ) { plot ->
            PlotSelectionCard(
                plot = plot,
                selected = state.selectedPlot == plot,
                onClick = { viewModel.selectPlot(plot) },
            )
        }
    }
    ScopeChoice(
        title = "仅检查选中样地",
        subtitle = state.selectedPlot?.displayPlotId ?: "请从列表选择样地",
        selected = state.selectedScope is CheckScope.Single,
        enabled = state.selectedPlot != null,
        onClick = viewModel::chooseSingleScope,
    )
    ScopeChoice(
        title = "检查当前区县",
        subtitle = state.selectedCounty ?: "先选择区县",
        selected = state.selectedScope is CheckScope.County,
        enabled = state.selectedCounty != null,
        onClick = viewModel::chooseCountyScope,
    )
    ScopeChoice(
        title = "检查全部样地",
        subtitle = "共 ${state.indexedPlots.size} 个样地",
        selected = state.selectedScope is CheckScope.All,
        enabled = state.indexedPlots.isNotEmpty(),
        onClick = viewModel::chooseAllScope,
    )
    Button(
        onClick = viewModel::startCheck,
        enabled = state.selectedScope?.plots?.isNotEmpty() == true,
        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
    ) {
        Text("开始质检")
    }
}

@Composable
private fun PlotSelectionCard(plot: PlotRef, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            },
        ),
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(plot.displayPlotId, style = MaterialTheme.typography.titleMedium)
            Text(
                "${plot.countyLabel ?: "区县未知"} · ${plot.source.projectName}",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun ScopeChoice(
    title: String,
    subtitle: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        RadioButton(selected = selected, enabled = enabled, onClick = onClick)
        Column {
            Text(title, color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline)
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun ColumnScope.ProgressScreen(state: QualityCheckUiState, viewModel: QualityCheckViewModel) {
    val progress = state.progress
    LinearProgressIndicator(
        progress = {
            if (progress == null || progress.totalPlots == 0) 0f
            else progress.completedPlots.toFloat() / progress.totalPlots
        },
        modifier = Modifier.fillMaxWidth(),
    )
    Text(
        "正在质检：${progress?.completedPlots ?: 0} / ${progress?.totalPlots ?: 0}",
        style = MaterialTheme.typography.titleMedium,
    )
    progress?.currentPlot?.let { Text("当前样地：${it.displayPlotId}") }
    Text("规则查询以只读模式执行；取消后将保留已完成样地的结果。", style = MaterialTheme.typography.bodySmall)
    Spacer(Modifier.weight(1f))
    OutlinedButton(
        onClick = viewModel::cancelCheck,
        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
    ) {
        Text("取消本次质检")
    }
}

@Composable
private fun ColumnScope.SummaryScreen(state: QualityCheckUiState, viewModel: QualityCheckViewModel) {
    val reviewed = state.reviewedRun
    val summary = reviewed?.summary
    if (reviewed == null || summary == null) {
        Text("暂无质检结果。")
        return
    }
    if (reviewed.sourceRun.cancelled) {
        Text("本次质检已取消，以下为取消前完成的样地结果。", color = MaterialTheme.colorScheme.tertiary)
    }
    Text("已检查 ${summary.checkedPlots} 个样地", style = MaterialTheme.typography.titleMedium)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SummaryMetric("强制性", summary.pendingMandatoryIssues.toString(), MaterialTheme.colorScheme.errorContainer)
        SummaryMetric("提示性", summary.pendingAdvisoryIssues.toString(), MaterialTheme.colorScheme.tertiaryContainer)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SummaryMetric("已忽略", summary.ignoredIssues.toString(), MaterialTheme.colorScheme.primaryContainer)
        SummaryMetric("已跳过规则", summary.skippedRules.toString(), MaterialTheme.colorScheme.surfaceContainerHigh)
    }
    Text("样地结果", style = MaterialTheme.typography.titleMedium)
    LazyColumn(
        modifier = Modifier.fillMaxWidth().weight(1f),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = reviewed.plotResults,
            key = { "${it.plot.source.uri}|${it.plot.displayPlotId}|${it.plot.plotTable.name}" },
        ) { result ->
            PlotResultCard(result = result, onClick = { viewModel.showPlotDetails(result) })
        }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = viewModel::recheck,
            modifier = Modifier.weight(1f).heightIn(min = 48.dp),
        ) {
            Text("再次质检")
        }
        OutlinedButton(
            onClick = viewModel::showScopeSelection,
            modifier = Modifier.weight(1f).heightIn(min = 48.dp),
        ) {
            Text("更改范围")
        }
    }
}

@Composable
private fun RowScope.SummaryMetric(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Card(
        modifier = Modifier.weight(1f),
        colors = CardDefaults.cardColors(containerColor = color),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(value, style = MaterialTheme.typography.headlineSmall)
            Text(label, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun PlotResultCard(result: ReviewedPlotResult, onClick: () -> Unit) {
    val containerColor = when {
        result.pendingMandatory.isNotEmpty() -> MaterialTheme.colorScheme.errorContainer
        result.pendingAdvisory.isNotEmpty() -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.primaryContainer
    }
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(result.plot.displayPlotId, style = MaterialTheme.typography.titleMedium)
            Text(
                "强制性 ${result.pendingMandatory.size} · 提示性 ${result.pendingAdvisory.size} · 已忽略 ${result.ignored.size}",
                style = MaterialTheme.typography.bodyMedium,
            )
            if (result.skippedRules.isNotEmpty()) {
                Text("跳过规则 ${result.skippedRules.size}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun ColumnScope.DetailScreen(state: QualityCheckUiState, viewModel: QualityCheckViewModel) {
    val result = state.detailPlot
    if (result == null) {
        Text("未选择样地结果。")
        return
    }
    Text("样地：${result.plot.displayPlotId}", style = MaterialTheme.typography.titleLarge)
    Text(
        "${result.plot.countyLabel ?: "区县未知"} · 已执行规则 ${result.executedRuleCount} 条",
        style = MaterialTheme.typography.bodySmall,
    )
    LazyColumn(
        modifier = Modifier.fillMaxWidth().weight(1f),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        issueSection(
            heading = "待处理强制性问题",
            issues = result.pendingMandatory,
            emptyText = "无强制性问题",
            onIgnore = { viewModel.setIgnored(it.fingerprint, true) },
        )
        issueSection(
            heading = "待处理提示性问题",
            issues = result.pendingAdvisory,
            emptyText = "无提示性问题",
            onIgnore = { viewModel.setIgnored(it.fingerprint, true) },
        )
        if (result.skippedRules.isNotEmpty()) {
            item { Text("无法执行的规则", style = MaterialTheme.typography.titleMedium) }
            items(result.skippedRules, key = { it.ruleId }) { skipped ->
                SkippedRuleCard(skipped)
            }
        }
        if (result.ignored.isNotEmpty()) {
            item {
                Text(
                    "已忽略（视为通过）",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            items(result.ignored, key = { it.fingerprint }) { issue ->
                IssueCard(
                    issue = issue,
                    actionLabel = "取消忽略",
                    onAction = { viewModel.setIgnored(issue.fingerprint, false) },
                )
            }
        }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = viewModel::recheck,
            modifier = Modifier.weight(1f).heightIn(min = 48.dp),
        ) {
            Text("再次质检")
        }
        OutlinedButton(
            onClick = viewModel::showSummary,
            modifier = Modifier.weight(1f).heightIn(min = 48.dp),
        ) {
            Text("返回汇总")
        }
    }
}

private fun LazyListScope.issueSection(
    heading: String,
    issues: List<CheckIssue>,
    emptyText: String,
    onIgnore: (CheckIssue) -> Unit,
) {
    item { Text(heading, style = MaterialTheme.typography.titleMedium) }
    if (issues.isEmpty()) {
        item { Text(emptyText, style = MaterialTheme.typography.bodySmall) }
    } else {
        items(issues, key = { it.fingerprint }) { issue ->
            IssueCard(
                issue = issue,
                actionLabel = "标注忽略",
                onAction = { onIgnore(issue) },
            )
        }
    }
}

@Composable
private fun IssueCard(issue: CheckIssue, actionLabel: String, onAction: () -> Unit) {
    val containerColor = when {
        issue.ignored -> MaterialTheme.colorScheme.primaryContainer
        issue.severity == RuleSeverity.MANDATORY -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.tertiaryContainer
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "${severityLabel(issue.severity)} · ${issue.title}",
                style = MaterialTheme.typography.titleSmall,
            )
            Text("规则：${issue.ruleId} · 表：${issue.tableName}", style = MaterialTheme.typography.bodySmall)
            Text(issue.explanation, style = MaterialTheme.typography.bodyMedium)
            ValueBlock("定位值", issue.locationValues)
            ValueBlock("实际值", issue.actualValues)
            OutlinedButton(onClick = onAction, modifier = Modifier.heightIn(min = 48.dp)) {
                Text(actionLabel)
            }
        }
    }
}

@Composable
private fun SkippedRuleCard(skipped: SkippedRule) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("${severityLabel(skipped.severity)} · ${skipped.title}", style = MaterialTheme.typography.titleSmall)
            Text("规则：${skipped.ruleId}", style = MaterialTheme.typography.bodySmall)
            Text(skipped.reason, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ValueBlock(label: String, values: Map<String, String?>) {
    Text(label, style = MaterialTheme.typography.labelLarge)
    if (values.isEmpty()) {
        Text("无", style = MaterialTheme.typography.bodySmall)
    } else {
        values.forEach { (key, value) ->
            Text("$key：${value?.ifBlank { "(空)" } ?: "(空)"}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun severityLabel(severity: RuleSeverity): String = when (severity) {
    RuleSeverity.MANDATORY -> "强制性"
    RuleSeverity.ADVISORY -> "提示性"
}

private fun titleFor(screen: QualityScreen): String = when (screen) {
    QualityScreen.SOURCE -> "草原监测质检"
    QualityScreen.SCOPE -> "选择质检范围"
    QualityScreen.PROGRESS -> "质检进行中"
    QualityScreen.SUMMARY -> "质检结果"
    QualityScreen.DETAIL -> "样地详情"
}
