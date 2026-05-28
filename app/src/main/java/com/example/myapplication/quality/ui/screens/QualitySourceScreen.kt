package com.example.myapplication.quality.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.RuleFolder
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.quality.ui.QualityCheckUiState
import com.example.myapplication.quality.ui.QualityCheckViewModel
import com.example.myapplication.quality.ui.components.QualityGlassCard
import com.example.myapplication.quality.ui.components.QualityIconTile
import com.example.myapplication.quality.ui.components.QualityMiniStatCard
import com.example.myapplication.quality.ui.components.QualityPageScaffold
import com.example.myapplication.quality.ui.components.QualityPrimaryButton
import com.example.myapplication.quality.ui.components.QualitySecondaryButton
import com.example.myapplication.quality.ui.components.QualityStatusCard
import com.example.myapplication.quality.ui.components.QualityStatusTone
import com.example.myapplication.quality.ui.components.QualityTagChip
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
        title = "Grassland Monitor",
        showDock = false,
        floatingAction = {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QualitySecondaryButton(
                    text = "取消",
                    onClick = viewModel::showSourceSelection,
                    enabled = false,
                    modifier = Modifier.weight(0.36f),
                )
                QualityPrimaryButton(
                    text = "下一步：选择范围",
                    onClick = viewModel::showScopeSelection,
                    enabled = state.indexedPlots.isNotEmpty(),
                    leadingIcon = Icons.AutoMirrored.Rounded.ArrowForward,
                    modifier = Modifier.weight(0.64f),
                    filledMint = true,
                )
            }
        },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 24.dp, bottom = 108.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                    Text(
                        text = "草地监测\n质量检查",
                        style = MaterialTheme.typography.displayLarge,
                        color = QualityDesignTokens.textPrimary,
                    )
                    Text(
                        text = "配置本地工作空间，开始野外监测数据质检。",
                        style = MaterialTheme.typography.bodyLarge,
                        color = QualityDesignTokens.textSecondary,
                    )
                }
            }
            item {
                DirectoryCard(
                    directoryName = state.directory?.displayName,
                    onOpenDirectory = { directoryPicker.launch(null) },
                    onRescan = viewModel::scanSavedDirectory,
                    rescanEnabled = !state.isScanning && state.directory != null,
                )
            }
            if (state.errorMessage != null) {
                item {
                    QualityStatusCard(text = state.errorMessage, tone = QualityStatusTone.Error)
                }
            }
            item {
                ScanStatusCard(
                    scanned = state.scanResult?.validSources?.size ?: 0,
                    indexed = state.indexedPlots.size,
                    rejected = state.rejectedSources.size,
                    isScanning = state.isScanning,
                )
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QualityMiniStatCard(
                        label = "目录总数",
                        value = "${state.scanResult?.validSources?.size ?: 0} 个",
                        icon = Icons.Rounded.Folder,
                        tint = QualityDesignTokens.primary,
                        modifier = Modifier.weight(1f),
                    )
                    QualityMiniStatCard(
                        label = "索引样地",
                        value = "${state.indexedPlots.size} 个",
                        icon = Icons.Rounded.RuleFolder,
                        tint = QualityDesignTokens.executedColor,
                        modifier = Modifier.weight(1f),
                    )
                    QualityMiniStatCard(
                        label = "异常历史",
                        value = "${state.rejectedSources.size} 条",
                        icon = Icons.Rounded.WarningAmber,
                        tint = QualityDesignTokens.advisoryColor,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            item {
                Text(
                    text = "扫描和质检均以只读方式读取 ZDB，不会修改原始数据。",
                    style = MaterialTheme.typography.bodyLarge,
                    color = QualityDesignTokens.textTertiary,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun DirectoryCard(
    directoryName: String?,
    onOpenDirectory: () -> Unit,
    onRescan: () -> Unit,
    rescanEnabled: Boolean,
) {
    QualityGlassCard(contentPadding = 18.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            QualityIconTile(
                icon = Icons.Rounded.FolderOpen,
                tint = QualityDesignTokens.primary,
                containerColor = QualityDesignTokens.primaryContainer,
                size = QualityDesignTokens.sourceIconSize,
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("数据目录选择", style = MaterialTheme.typography.titleLarge, color = QualityDesignTokens.textPrimary)
                Text(
                    text = directoryName ?: "选择本地 .zdb 数据目录",
                    style = MaterialTheme.typography.bodyMedium,
                    color = QualityDesignTokens.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QualityPrimaryButton(
                text = "选择目录",
                onClick = onOpenDirectory,
                leadingIcon = Icons.Rounded.Folder,
                modifier = Modifier.weight(1f),
            )
            QualitySecondaryButton(
                text = "重新扫描",
                onClick = onRescan,
                enabled = rescanEnabled,
                leadingIcon = Icons.Rounded.Refresh,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ScanStatusCard(
    scanned: Int,
    indexed: Int,
    rejected: Int,
    isScanning: Boolean,
) {
    QualityGlassCard(contentPadding = 18.dp) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Canvas(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(132.dp),
            ) {
                drawCircle(
                    color = QualityDesignTokens.primaryFixed.copy(alpha = 0.16f),
                    radius = size.minDimension,
                    center = Offset(size.width, 0f),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = QualityDesignTokens.primary)
                        Text("扫描状态", style = MaterialTheme.typography.titleLarge, color = QualityDesignTokens.textPrimary)
                    }
                    QualityTagChip(
                        text = if (isScanning) "扫描中" else "就绪",
                        containerColor = QualityDesignTokens.surfaceContainer,
                        textColor = QualityDesignTokens.textSecondary,
                    )
                }
                if (isScanning) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(26.dp),
                            color = QualityDesignTokens.primary,
                            strokeWidth = 3.dp,
                        )
                        Text("正在查找并读取 ZDB 结构...", color = QualityDesignTokens.textSecondary)
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ScanMetric("已扫描", scanned.toString(), "ZDB 文件", Modifier.weight(1f))
                        ScanMetric("已索引", indexed.toString(), "样地", Modifier.weight(1f))
                        ScanMetric("异常", rejected.toString(), "条", Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun ScanMetric(
    label: String,
    value: String,
    unit: String,
    modifier: Modifier,
) {
    Column(
        modifier = modifier
            .height(76.dp)
            .background(Color.White.copy(alpha = 0.24f), QualityDesignTokens.innerShape)
            .padding(12.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = QualityDesignTokens.textSecondary)
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(value, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = QualityDesignTokens.textPrimary)
            Text(unit, style = MaterialTheme.typography.bodySmall, color = QualityDesignTokens.textTertiary)
        }
    }
}
