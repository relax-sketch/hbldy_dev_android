package com.example.myapplication.quality.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
import com.example.myapplication.quality.ui.components.QualityIconTile
import com.example.myapplication.quality.ui.components.QualityModePill
import com.example.myapplication.quality.ui.components.QualityPageScaffold
import com.example.myapplication.quality.ui.components.QualityPanelCard
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
    var countyMenuExpanded by remember { mutableStateOf(false) }
    val filterControlsEnabled = !state.checkAllMode
    QualityPageScaffold(
        title = "选择质检范围",
        canNavigateBack = true,
        onNavigateBack = viewModel::navigateBack,
        trailingContent = {
            QualityModePill(
                label = "全量",
                checked = state.checkAllMode,
                onClick = { viewModel.setCheckAllMode(!state.checkAllMode) },
            )
            QualityModePill(
                label = "测试",
                checked = state.testMode,
                onClick = { viewModel.setTestMode(!state.testMode) },
            )
        },
        bottomBar = {
            QualityPrimaryButton(
                text = "开始质检",
                onClick = viewModel::startCheck,
                enabled = if (state.checkAllMode) state.indexedPlots.isNotEmpty() else state.selectedPlot != null,
                modifier = Modifier.weight(1f),
            )
        },
    ) { layout ->
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(layout.verticalSpacing),
        ) {
            CompactFilterBar(
                state = state,
                filterControlsEnabled = filterControlsEnabled,
                countyMenuExpanded = countyMenuExpanded,
                onExpandCounty = { countyMenuExpanded = true },
                onDismissCounty = { countyMenuExpanded = false },
                onSelectCounty = {
                    countyMenuExpanded = false
                    viewModel.updateCountyFilter(it)
                },
                onUpdateQuery = viewModel::updatePlotQuery,
            )
            if (state.errorMessage != null) {
                QualityStatusCard(text = state.errorMessage, tone = QualityStatusTone.Error)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (state.checkAllMode) {
                        "全量模式，将检查全部已索引样地"
                    } else {
                        "匹配 ${state.filteredPlots.size} 个样地"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = QualityDesignTokens.textSecondary,
                )
                Text(
                    text = if (state.selectedPlot == null || state.checkAllMode) "" else "已选择 ${state.selectedPlot.displayPlotId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = QualityDesignTokens.passedColor,
                )
            }
            PlotSelectionList(
                plots = state.filteredPlots,
                selectedPlot = state.selectedPlot,
                enabled = !state.checkAllMode,
                onPlotClick = viewModel::selectPlot,
                modifier = Modifier.weight(1f, fill = true),
            )
        }
    }
}

@Composable
private fun CompactFilterBar(
    state: QualityCheckUiState,
    filterControlsEnabled: Boolean,
    countyMenuExpanded: Boolean,
    onExpandCounty: () -> Unit,
    onDismissCounty: () -> Unit,
    onSelectCounty: (String?) -> Unit,
    onUpdateQuery: (String) -> Unit,
) {
    QualityPanelCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(modifier = Modifier.weight(0.38f)) {
                Surface(
                    onClick = onExpandCounty,
                    enabled = filterControlsEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 54.dp),
                    color = if (filterControlsEnabled) QualityDesignTokens.surface else QualityDesignTokens.surfaceAlt,
                    contentColor = if (filterControlsEnabled) QualityDesignTokens.textPrimary else QualityDesignTokens.textTertiary,
                    shape = QualityDesignTokens.buttonShape,
                    border = BorderStroke(1.dp, QualityDesignTokens.border),
                    shadowElevation = 0.dp,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = if (state.checkAllMode) "全域" else state.selectedCounty ?: "全域",
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null)
                    }
                }
                DropdownMenu(
                    expanded = countyMenuExpanded && filterControlsEnabled,
                    onDismissRequest = onDismissCounty,
                ) {
                    DropdownMenuItem(text = { Text("全域") }, onClick = { onSelectCounty(null) })
                    state.countyOptions.forEach { county ->
                        DropdownMenuItem(text = { Text(county) }, onClick = { onSelectCounty(county) })
                    }
                }
            }
            OutlinedTextField(
                value = state.plotQuery,
                onValueChange = onUpdateQuery,
                singleLine = true,
                enabled = filterControlsEnabled,
                placeholder = {
                    Text("搜索样地编号", color = QualityDesignTokens.textTertiary)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                        tint = QualityDesignTokens.textTertiary,
                    )
                },
                modifier = Modifier
                    .weight(0.62f)
                    .heightIn(min = 54.dp),
                shape = QualityDesignTokens.buttonShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = QualityDesignTokens.borderStrong,
                    unfocusedBorderColor = QualityDesignTokens.border,
                    disabledBorderColor = QualityDesignTokens.border,
                    focusedContainerColor = QualityDesignTokens.surface,
                    unfocusedContainerColor = QualityDesignTokens.surface,
                    disabledContainerColor = QualityDesignTokens.surfaceAlt,
                    focusedTextColor = QualityDesignTokens.textPrimary,
                    unfocusedTextColor = QualityDesignTokens.textPrimary,
                    disabledTextColor = QualityDesignTokens.textTertiary,
                ),
            )
        }
    }
}

@Composable
private fun PlotSelectionList(
    plots: List<PlotRef>,
    selectedPlot: PlotRef?,
    enabled: Boolean,
    onPlotClick: (PlotRef) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (plots.isEmpty()) {
        QualityStatusCard(
            text = "当前筛选条件下没有匹配样地",
            tone = QualityStatusTone.Info,
            modifier = modifier,
        )
        return
    }
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = plots,
            key = { "${it.source.uri}|${it.plotTable.name}|${it.displayPlotId}" },
        ) { plot ->
            val accent = accentForPlot(plot)
            QualitySelectableCard(
                selected = enabled && selectedPlot == plot,
                enabled = enabled,
                onClick = { onPlotClick(plot) },
            ) {
                QualityIconTile(
                    icon = Icons.Rounded.Eco,
                    tint = accent,
                    containerColor = accent.copy(alpha = 0.12f),
                )
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "样地 ${plot.displayPlotId}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (enabled) QualityDesignTokens.textPrimary else QualityDesignTokens.textTertiary,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    QualityTagChip(
                        text = plot.countyLabel ?: "未知区县",
                        containerColor = accent.copy(alpha = 0.12f),
                        textColor = accent,
                    )
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                color = if (enabled) accent else QualityDesignTokens.borderStrong,
                                shape = CircleShape,
                            ),
                    )
                }
            }
        }
    }
}

internal fun accentForPlot(plot: PlotRef): Color {
    val palette = listOf(
        QualityDesignTokens.passedColor,
        QualityDesignTokens.executedColor,
        QualityDesignTokens.skippedColor,
        QualityDesignTokens.advisoryColor,
        Color(0xFF39BFA7),
    )
    return palette[kotlin.math.abs(plot.displayPlotId.hashCode()) % palette.size]
}

private fun Modifier.clickableIf(enabled: Boolean, onClick: () -> Unit): Modifier =
    if (enabled) clickable(onClick = onClick) else this
