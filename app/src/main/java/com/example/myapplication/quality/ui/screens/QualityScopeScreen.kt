package com.example.myapplication.quality.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.quality.domain.PlotRef
import com.example.myapplication.quality.ui.QualityCheckUiState
import com.example.myapplication.quality.ui.QualityCheckViewModel
import com.example.myapplication.quality.ui.components.QualityDockTab
import com.example.myapplication.quality.ui.components.QualityGlassCard
import com.example.myapplication.quality.ui.components.QualityIconTile
import com.example.myapplication.quality.ui.components.QualityPageScaffold
import com.example.myapplication.quality.ui.components.QualityPrimaryButton
import com.example.myapplication.quality.ui.components.QualitySelectableCard
import com.example.myapplication.quality.ui.components.QualityStatusCard
import com.example.myapplication.quality.ui.components.QualityStatusTone
import com.example.myapplication.quality.ui.components.QualityTagChip
import com.example.myapplication.quality.ui.design.QualityDesignTokens

@Composable
fun QualityScopeScreen(
    state: QualityCheckUiState,
    viewModel: QualityCheckViewModel,
) {
    QualityPageScaffold(
        title = "Grassland Monitor",
        showDock = true,
        selectedDockTab = QualityDockTab.PLOTS,
        onNavigateBack = viewModel::navigateBack,
        onDockTabClick = { tab ->
            when (tab) {
                QualityDockTab.PLOTS -> Unit
                QualityDockTab.INSPECT,
                QualityDockTab.STATS,
                -> if (state.reviewedRun != null) viewModel.showSummary()
                QualityDockTab.SETTINGS -> viewModel.showSettings()
            }
        },
        floatingAction = {
            QualityPrimaryButton(
                text = if (state.checkAllMode) "开始全量质检" else "开始质检",
                onClick = viewModel::startCheck,
                enabled = if (state.checkAllMode) state.indexedPlots.isNotEmpty() else state.selectedPlot != null,
                trailingIcon = Icons.AutoMirrored.Rounded.ArrowForward,
                pill = true,
            )
        },
        bottomPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 118.dp),
    ) {
        var countyMenuExpanded by remember { mutableStateOf(false) }
        val controlsEnabled = !state.checkAllMode

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 8.dp),
        ) {
            item {
                Text("配置质检参数和目标样地。", style = MaterialTheme.typography.bodyLarge, color = QualityDesignTokens.textSecondary)
            }
            item {
                FilterPanel(
                    state = state,
                    controlsEnabled = controlsEnabled,
                    countyMenuExpanded = countyMenuExpanded,
                    onExpandCounty = { countyMenuExpanded = true },
                    onDismissCounty = { countyMenuExpanded = false },
                    onSelectCounty = {
                        countyMenuExpanded = false
                        viewModel.updateCountyFilter(it)
                    },
                    onUpdateQuery = viewModel::updatePlotQuery,
                    onToggleAll = { viewModel.setCheckAllMode(!state.checkAllMode) },
                    onToggleTest = { viewModel.setTestMode(!state.testMode) },
                )
            }
            if (state.errorMessage != null) {
                item { QualityStatusCard(text = state.errorMessage, tone = QualityStatusTone.Error) }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "可用样地",
                        style = MaterialTheme.typography.labelLarge,
                        color = QualityDesignTokens.textTertiary,
                    )
                    Text(
                        text = if (state.checkAllMode) "全量 ${state.indexedPlots.size} 个" else "匹配 ${state.filteredPlots.size} 个",
                        style = MaterialTheme.typography.labelLarge,
                        color = QualityDesignTokens.primary,
                    )
                }
            }
            if (state.filteredPlots.isEmpty()) {
                item {
                    QualityStatusCard(
                        text = "当前筛选条件下没有匹配样地。",
                        tone = QualityStatusTone.Info,
                    )
                }
            } else {
                items(
                    items = state.filteredPlots,
                    key = { "${it.source.uri}|${it.plotTable.name}|${it.displayPlotId}" },
                ) { plot ->
                    PlotSelectionCard(
                        plot = plot,
                        selected = !state.checkAllMode && state.selectedPlot == plot,
                        enabled = !state.checkAllMode,
                        onClick = { viewModel.selectPlot(plot) },
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterPanel(
    state: QualityCheckUiState,
    controlsEnabled: Boolean,
    countyMenuExpanded: Boolean,
    onExpandCounty: () -> Unit,
    onDismissCounty: () -> Unit,
    onSelectCounty: (String?) -> Unit,
    onUpdateQuery: (String) -> Unit,
    onToggleAll: () -> Unit,
    onToggleTest: () -> Unit,
) {
    QualityGlassCard(contentPadding = 14.dp) {
        OutlinedTextField(
            value = state.plotQuery,
            onValueChange = onUpdateQuery,
            singleLine = true,
            enabled = controlsEnabled,
            placeholder = { Text("搜索样地编号...", color = QualityDesignTokens.textTertiary) },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = QualityDesignTokens.outline) },
            modifier = Modifier.fillMaxWidth(),
            shape = QualityDesignTokens.innerShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = QualityDesignTokens.borderStrong,
                unfocusedBorderColor = QualityDesignTokens.borderStrong,
                disabledBorderColor = QualityDesignTokens.border,
                focusedContainerColor = Color.White.copy(alpha = 0.4f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.4f),
                disabledContainerColor = Color.White.copy(alpha = 0.18f),
            ),
        )
        Box {
            Surface(
                onClick = onExpandCounty,
                enabled = controlsEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                color = Color.White.copy(alpha = if (controlsEnabled) 0.4f else 0.18f),
                shape = QualityDesignTokens.innerShape,
                border = BorderStroke(1.dp, QualityDesignTokens.borderStrong),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (state.checkAllMode) "全域" else state.selectedCounty ?: "区县选择",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (controlsEnabled) QualityDesignTokens.textPrimary else QualityDesignTokens.textTertiary,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null, tint = QualityDesignTokens.outline)
                }
            }
            DropdownMenu(expanded = countyMenuExpanded && controlsEnabled, onDismissRequest = onDismissCounty) {
                DropdownMenuItem(text = { Text("全部区县") }, onClick = { onSelectCounty(null) })
                state.countyOptions.forEach { county ->
                    DropdownMenuItem(text = { Text(county) }, onClick = { onSelectCounty(county) })
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ModeSwitchRow(
                title = "全量模式",
                subtitle = "检查全部已索引样地",
                checked = state.checkAllMode,
                onClick = onToggleAll,
            )
            ModeSwitchRow(
                title = "测试模式",
                subtitle = "显示通过规则，不影响原始数据",
                checked = state.testMode,
                onClick = onToggleTest,
            )
        }
    }
}

@Composable
private fun ModeSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, color = QualityDesignTokens.textPrimary)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = QualityDesignTokens.textSecondary)
        }
        Switch(
            checked = checked,
            onCheckedChange = { onClick() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = QualityDesignTokens.surface,
                checkedTrackColor = QualityDesignTokens.primary,
                uncheckedThumbColor = QualityDesignTokens.surface,
                uncheckedTrackColor = Color.White.copy(alpha = 0.72f),
            ),
        )
    }
}

@Composable
private fun PlotSelectionCard(
    plot: PlotRef,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val accent = accentForPlot(plot)
    QualitySelectableCard(selected = selected, enabled = enabled, onClick = onClick) {
        QualityIconTile(
            icon = Icons.Rounded.Eco,
            tint = accent,
            containerColor = accent.copy(alpha = 0.12f),
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "样地 ${plot.displayPlotId}",
                style = MaterialTheme.typography.titleMedium,
                color = if (enabled) QualityDesignTokens.textPrimary else QualityDesignTokens.textTertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = plot.countyLabel ?: "未知区县",
                style = MaterialTheme.typography.bodyMedium,
                color = if (enabled) QualityDesignTokens.textSecondary else QualityDesignTokens.textTertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box(
            modifier = Modifier
                .size(18.dp)
                .background(Color.White.copy(alpha = 0.8f), QualityDesignTokens.innerShape)
                .then(
                    if (selected) {
                        Modifier.background(QualityDesignTokens.primary, QualityDesignTokens.innerShape)
                    } else {
                        Modifier
                    },
                ),
        )
    }
}

internal fun accentForPlot(plot: PlotRef): Color {
    val palette = listOf(
        QualityDesignTokens.primary,
        QualityDesignTokens.executedColor,
        QualityDesignTokens.advisoryColor,
        QualityDesignTokens.skippedColor,
    )
    val index = kotlin.math.abs(plot.displayPlotId.hashCode()) % palette.size
    return palette[index]
}
