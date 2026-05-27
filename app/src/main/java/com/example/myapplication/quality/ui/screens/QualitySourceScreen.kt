package com.example.myapplication.quality.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.quality.ui.QualityCheckUiState
import com.example.myapplication.quality.ui.QualityCheckViewModel
import com.example.myapplication.quality.ui.components.QualityEmptyStateCard
import com.example.myapplication.quality.ui.components.QualityIconTile
import com.example.myapplication.quality.ui.components.QualityPageScaffold
import com.example.myapplication.quality.ui.components.QualityPanelCard
import com.example.myapplication.quality.ui.components.QualityPrimaryButton
import com.example.myapplication.quality.ui.components.QualitySecondaryButton
import com.example.myapplication.quality.ui.components.QualityStatTile
import com.example.myapplication.quality.ui.components.QualityStatusCard
import com.example.myapplication.quality.ui.components.QualityStatusTone
import com.example.myapplication.quality.ui.design.QualityDesignTokens

@Composable
fun QualitySourceScreen(
    state: QualityCheckUiState,
    viewModel: QualityCheckViewModel,
) {
    val directoryPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        uri?.let(viewModel::onDirectoryPicked)
    }
    QualityPageScaffold(
        title = "草原监测质检",
        canNavigateBack = false,
        onNavigateBack = {},
        bottomBar = {
            QualityPrimaryButton(
                text = "选择质检范围",
                onClick = viewModel::showScopeSelection,
                enabled = state.indexedPlots.isNotEmpty(),
                modifier = Modifier.weight(1f),
            )
        },
    ) { layout ->
        val listState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(layout.verticalSpacing),
        ) {
            item {
                QualityPanelCard {
                    if (layout.isLandscape) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            DirectoryInfo(state = state, modifier = Modifier.weight(1f))
                            Column(
                                modifier = Modifier.weight(0.8f),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                QualityPrimaryButton(
                                    text = "选择目录",
                                    onClick = { directoryPicker.launch(null) },
                                    enabled = !state.isScanning,
                                    leadingIcon = Icons.Rounded.FolderOpen,
                                )
                                QualitySecondaryButton(
                                    text = "重新扫描",
                                    onClick = viewModel::scanSavedDirectory,
                                    enabled = !state.isScanning && state.directory != null,
                                    leadingIcon = Icons.Rounded.Refresh,
                                )
                            }
                        }
                    } else {
                        DirectoryInfo(state = state)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            QualityPrimaryButton(
                                text = "选择目录",
                                onClick = { directoryPicker.launch(null) },
                                modifier = Modifier.weight(1f),
                                enabled = !state.isScanning,
                                leadingIcon = Icons.Rounded.FolderOpen,
                            )
                            QualitySecondaryButton(
                                text = "重新扫描",
                                onClick = viewModel::scanSavedDirectory,
                                modifier = Modifier.weight(1f),
                                enabled = !state.isScanning && state.directory != null,
                                leadingIcon = Icons.Rounded.Refresh,
                            )
                        }
                    }
                }
            }
            if (state.errorMessage != null) {
                item {
                    QualityStatusCard(
                        text = state.errorMessage,
                        tone = QualityStatusTone.Error,
                    )
                }
            }
            if (state.isScanning) {
                item {
                    QualityPanelCard {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Box(
                                modifier = Modifier.size(QualityDesignTokens.iconTileSize),
                                contentAlignment = Alignment.Center,
                            ) {
                                QualityIconTile(
                                    icon = Icons.Rounded.Refresh,
                                    tint = QualityDesignTokens.passedColor.copy(alpha = 0.18f),
                                    containerColor = QualityDesignTokens.passedContainer,
                                )
                                CircularProgressIndicator(
                                    modifier = Modifier.size(30.dp),
                                    strokeWidth = 4.dp,
                                    color = QualityDesignTokens.passedColor,
                                )
                            }
                            Text(
                                text = "正在查找并读取 ZDB 结构...",
                                style = MaterialTheme.typography.titleLarge,
                                color = QualityDesignTokens.textPrimary,
                            )
                        }
                    }
                }
            }
            if (state.scanResult != null || state.rejectedSources.isNotEmpty()) {
                item {
                    QualityPanelCard {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            QualityStatTile(
                                title = "可读取 ZDB",
                                value = state.scanResult?.validSources?.size?.toString() ?: "0",
                                tint = QualityDesignTokens.passedColor,
                                icon = Icons.Rounded.Description,
                                containerColor = QualityDesignTokens.passedContainer,
                                modifier = Modifier.weight(1f),
                            )
                            QualityStatTile(
                                title = "索引样地",
                                value = state.indexedPlots.size.toString(),
                                tint = QualityDesignTokens.executedColor,
                                icon = Icons.Rounded.Place,
                                containerColor = QualityDesignTokens.executedContainer,
                                modifier = Modifier.weight(1f),
                            )
                            QualityStatTile(
                                title = "无法使用",
                                value = state.rejectedSources.size.toString(),
                                tint = QualityDesignTokens.mandatoryColor,
                                icon = Icons.Rounded.ErrorOutline,
                                containerColor = QualityDesignTokens.mandatoryContainer,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            } else if (!state.isScanning && state.directory == null) {
                item {
                    QualityEmptyStateCard(
                        title = "等待选择数据目录",
                        message = "请选择“草原监测 / 数据”目录，应用会递归扫描其中的 ZDB 文件。",
                    )
                }
            }
            item {
                Text(
                    text = "扫描和质检查询均以只读方式读取 ZDB",
                    style = MaterialTheme.typography.bodySmall,
                    color = QualityDesignTokens.textSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun DirectoryInfo(
    state: QualityCheckUiState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        QualityIconTile(
            icon = Icons.Rounded.FolderOpen,
            tint = QualityDesignTokens.passedColor,
            containerColor = QualityDesignTokens.passedContainer,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "当前数据目录",
                style = MaterialTheme.typography.titleMedium,
                color = QualityDesignTokens.textSecondary,
            )
            Text(
                text = state.directory?.displayName ?: "尚未选择数据目录",
                style = MaterialTheme.typography.headlineMedium,
                color = QualityDesignTokens.textPrimary,
            )
        }
    }
}
