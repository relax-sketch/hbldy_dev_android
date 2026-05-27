package com.example.myapplication.quality.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.myapplication.quality.ui.design.QualityDesignTokens

@Composable
fun QualityPanelCard(
    modifier: Modifier = Modifier,
    containerColor: Color = QualityDesignTokens.surface,
    borderColor: Color = Color.Transparent,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = QualityDesignTokens.cardShape,
        border = if (borderColor == Color.Transparent) null else BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
    ) {
        Column(
            modifier = Modifier.padding(QualityDesignTokens.largeCardPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content,
        )
    }
}

@Composable
fun QualitySelectableCard(
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val borderColor = when {
        selected -> QualityDesignTokens.passedColor
        else -> Color.Transparent
    }
    val containerColor = when {
        !enabled -> QualityDesignTokens.surfaceAlt.copy(alpha = 0.72f)
        selected -> QualityDesignTokens.surfaceSoftGreen
        else -> QualityDesignTokens.surface
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, QualityDesignTokens.cardShape)
            .defaultMinSize(minHeight = 112.dp)
            .clickable(enabled = enabled, onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = QualityDesignTokens.cardShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(QualityDesignTokens.mediumCardPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            content = content,
        )
    }
}

@Composable
fun QualityIconTile(
    icon: ImageVector,
    tint: Color,
    containerColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(QualityDesignTokens.iconTileSize)
            .background(containerColor, QualityDesignTokens.iconTileShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = tint)
    }
}

@Composable
fun QualityMetricRow(
    title: String,
    value: String,
    tint: Color,
    icon: ImageVector,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        QualityIconTile(icon = icon, tint = tint, containerColor = tint.copy(alpha = 0.12f))
        Spacer(Modifier.size(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = QualityDesignTokens.textPrimary,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = tint,
        )
    }
}

@Composable
fun QualityLabeledValue(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = QualityDesignTokens.textSecondary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = QualityDesignTokens.textPrimary,
        )
    }
}

@Composable
fun QualityDivider() {
    HorizontalDivider(color = QualityDesignTokens.border)
}

@Composable
fun QualityStatTile(
    title: String,
    value: String,
    icon: ImageVector,
    tint: Color,
    containerColor: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(icon, contentDescription = null, tint = tint)
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = QualityDesignTokens.textSecondary,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                color = tint,
            )
        }
    }
}
