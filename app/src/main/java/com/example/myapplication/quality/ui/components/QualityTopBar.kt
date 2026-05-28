package com.example.myapplication.quality.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.FactCheck
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.quality.ui.design.QualityDesignTokens
import com.example.myapplication.quality.ui.design.QualityLayoutSpec
import com.example.myapplication.quality.ui.design.rememberQualityLayoutSpec

enum class QualityDockTab(
    val label: String,
    val icon: ImageVector,
) {
    PLOTS("样地", Icons.Rounded.Folder),
    INSPECT("质检", Icons.AutoMirrored.Rounded.FactCheck),
    STATS("统计", Icons.Rounded.BarChart),
    SETTINGS("设置", Icons.Rounded.Settings),
}

@Composable
fun QualityPageScaffold(
    title: String,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    showTopBar: Boolean = true,
    showDock: Boolean = false,
    selectedDockTab: QualityDockTab? = null,
    onNavigateBack: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    onDockTabClick: (QualityDockTab) -> Unit = {},
    floatingAction: (@Composable BoxScope.() -> Unit)? = null,
    bottomPadding: PaddingValues = PaddingValues(),
    content: @Composable BoxScope.(QualityLayoutSpec) -> Unit,
) {
    val layout = rememberQualityLayoutSpec()
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(QualityDesignTokens.pageBrush),
    ) {
        AmbientBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding(),
        ) {
            if (showTopBar) {
                QualityTopBar(
                    title = title,
                    canNavigateBack = canNavigateBack,
                    onNavigateBack = onNavigateBack,
                    onMenuClick = onMenuClick,
                    onAccountClick = onAccountClick,
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(
                        start = layout.horizontalPadding,
                        end = layout.horizontalPadding,
                    ),
                contentAlignment = Alignment.TopCenter,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = layout.contentMaxWidth)
                        .padding(bottomPadding),
                    content = { content(layout) },
                )
            }
            if (showDock) {
                QualityBottomDock(
                    selected = selectedDockTab,
                    onTabClick = onDockTabClick,
                )
            }
        }
        if (floatingAction != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(
                        start = layout.horizontalPadding,
                        end = layout.horizontalPadding,
                        bottom = if (showDock) QualityDesignTokens.bottomDockHeight + 8.dp else 20.dp,
                    )
                    .widthIn(max = 520.dp)
                    .fillMaxWidth(),
                content = floatingAction,
            )
        }
    }
}

@Composable
fun QualityTopBar(
    title: String,
    canNavigateBack: Boolean,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    trailingContent: @Composable RowScope.() -> Unit = {},
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(QualityDesignTokens.topBarHeight),
        color = QualityDesignTokens.glass,
        contentColor = QualityDesignTokens.textPrimary,
        shadowElevation = 1.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = QualityDesignTokens.horizontalMargin),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                QualityIconButton(
                    icon = if (canNavigateBack) Icons.AutoMirrored.Rounded.ArrowBack else Icons.Rounded.Menu,
                    contentDescription = if (canNavigateBack) "返回" else "菜单",
                    onClick = if (canNavigateBack) onNavigateBack else onMenuClick,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    content = {
                        trailingContent()
                        QualityIconButton(
                            icon = Icons.Rounded.AccountCircle,
                            contentDescription = "账户",
                            onClick = onAccountClick,
                        )
                    },
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = QualityDesignTokens.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 54.dp),
            )
        }
    }
}

@Composable
fun QualityIconButton(
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = QualityDesignTokens.primary,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .size(QualityDesignTokens.iconButtonSize)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = contentDescription, tint = tint)
    }
}

@Composable
private fun QualityBottomDock(
    selected: QualityDockTab?,
    onTabClick: (QualityDockTab) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(QualityDesignTokens.bottomDockHeight),
        color = QualityDesignTokens.glass,
        shadowElevation = 6.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            QualityDockTab.values().forEach { tab ->
                val active = selected == tab
                Column(
                    modifier = Modifier
                        .size(width = 64.dp, height = 54.dp)
                        .clickable { onTabClick(tab) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = if (active) QualityDesignTokens.primary else QualityDesignTokens.textTertiary,
                        modifier = Modifier.size(24.dp),
                    )
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (active) QualityDesignTokens.primary else QualityDesignTokens.textTertiary,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun AmbientBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color = QualityDesignTokens.primaryFixed.copy(alpha = 0.12f),
            radius = size.minDimension * 0.38f,
            center = Offset(size.width * 0.15f, size.height * 0.05f),
        )
        drawCircle(
            color = QualityDesignTokens.primary.copy(alpha = 0.045f),
            radius = size.minDimension * 0.42f,
            center = Offset(size.width * 0.88f, size.height * 0.78f),
        )
        drawCircle(
            color = Color(0xFFFFEB9E).copy(alpha = 0.14f),
            radius = size.minDimension * 0.34f,
            center = Offset(size.width * 0.1f, size.height * 0.92f),
        )
    }
}
