package com.example.myapplication.quality.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.quality.ui.design.QualityDesignTokens
import com.example.myapplication.quality.ui.design.QualityLayoutSpec
import com.example.myapplication.quality.ui.design.rememberQualityLayoutSpec

@Composable
fun QualityPageScaffold(
    title: String,
    canNavigateBack: Boolean,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: @Composable RowScope.() -> Unit = {},
    bottomBar: (@Composable RowScope.(QualityLayoutSpec) -> Unit)? = null,
    content: @Composable BoxScope.(QualityLayoutSpec) -> Unit,
) {
    val layoutSpec = rememberQualityLayoutSpec()
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(QualityDesignTokens.backgroundBrush),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(
                    top = layoutSpec.topPadding,
                    start = layoutSpec.horizontalPadding,
                    end = layoutSpec.horizontalPadding,
                ),
            contentAlignment = Alignment.TopCenter,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = layoutSpec.contentMaxWidth),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(layoutSpec.verticalSpacing),
                ) {
                    QualityTopBar(
                        title = title,
                        canNavigateBack = canNavigateBack,
                        onNavigateBack = onNavigateBack,
                        layoutSpec = layoutSpec,
                        trailingContent = trailingContent,
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = true),
                        content = { content(layoutSpec) },
                    )
                    if (bottomBar != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp, bottom = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            bottomBar(layoutSpec)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QualityTopBar(
    title: String,
    canNavigateBack: Boolean,
    onNavigateBack: () -> Unit,
    layoutSpec: QualityLayoutSpec,
    trailingContent: @Composable RowScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(layoutSpec.topBarHeight),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (canNavigateBack) {
                Surface(
                    onClick = onNavigateBack,
                    color = QualityDesignTokens.surface,
                    contentColor = QualityDesignTokens.textPrimary,
                    shape = QualityDesignTokens.iconTileShape,
                    shadowElevation = 6.dp,
                ) {
                    Box(modifier = Modifier.padding(14.dp)) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "返回")
                    }
                }
            } else {
                Box(Modifier.padding(26.dp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), content = trailingContent)
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            color = QualityDesignTokens.textPrimary,
        )
    }
}
