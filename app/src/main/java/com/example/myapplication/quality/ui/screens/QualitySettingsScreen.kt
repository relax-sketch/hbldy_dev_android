package com.example.myapplication.quality.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.quality.ui.QualityCheckUiState
import com.example.myapplication.quality.ui.QualityCheckViewModel
import com.example.myapplication.quality.ui.components.QualityDockTab
import com.example.myapplication.quality.ui.components.QualityIconTile
import com.example.myapplication.quality.ui.components.QualityMetricRow
import com.example.myapplication.quality.ui.components.QualityPageScaffold
import com.example.myapplication.quality.ui.components.QualityPanelCard
import com.example.myapplication.quality.ui.design.QualityDesignTokens

@Composable
fun QualitySettingsScreen(
    state: QualityCheckUiState,
    viewModel: QualityCheckViewModel,
) {
    QualityPageScaffold(
        title = "设置",
        canNavigateBack = true,
        showDock = true,
        selectedDockTab = QualityDockTab.SETTINGS,
        onNavigateBack = viewModel::navigateBack,
        onDockTabClick = { tab ->
            when (tab) {
                QualityDockTab.PLOTS -> viewModel.showScopeSelection()
                QualityDockTab.INSPECT,
                QualityDockTab.STATS,
                -> if (state.reviewedRun != null) viewModel.showSummary()
                QualityDockTab.SETTINGS -> Unit
            }
        },
        bottomPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            QualityPanelCard {
                QualityIconTile(
                    icon = Icons.Rounded.Settings,
                    tint = QualityDesignTokens.primary,
                    containerColor = QualityDesignTokens.primaryContainer,
                    size = 56.dp,
                )
                Text(
                    text = "设置",
                    style = MaterialTheme.typography.displayLarge,
                    color = QualityDesignTokens.textPrimary,
                )
                Text(
                    text = "当前版本先保留目标稿中的设置入口。质检主线仍围绕数据目录、样地范围、质检结果和样地详情运行。",
                    style = MaterialTheme.typography.bodyLarge,
                    color = QualityDesignTokens.textSecondary,
                )
            }
            QualityPanelCard {
                QualityMetricRow(
                    title = "只读质检",
                    value = "开启",
                    tint = QualityDesignTokens.primary,
                    icon = Icons.Rounded.Security,
                )
                QualityMetricRow(
                    title = "数据来源",
                    value = state.directory?.displayName ?: "未选择",
                    tint = QualityDesignTokens.executedColor,
                    icon = Icons.Rounded.Info,
                )
            }
        }
    }
}
