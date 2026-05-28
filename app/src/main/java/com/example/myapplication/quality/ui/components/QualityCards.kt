package com.example.myapplication.quality.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.myapplication.quality.ui.design.QualityDesignTokens

@Composable
fun QualityGlassCard(
    modifier: Modifier = Modifier,
    containerColor: Color = QualityDesignTokens.glass,
    borderColor: Color = QualityDesignTokens.borderStrong,
    contentPadding: Dp = QualityDesignTokens.cardPadding,
    innerPadding: Dp = 6.dp,
    innerSurface: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = QualityDesignTokens.glassShape,
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        if (innerSurface) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding),
                color = Color.White.copy(alpha = 0.22f),
                shape = QualityDesignTokens.innerShape,
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.34f)),
                shadowElevation = 0.dp,
            ) {
                Column(
                    modifier = Modifier.padding(contentPadding),
                    verticalArrangement = Arrangement.spacedBy(QualityDesignTokens.stackGap),
                    content = content,
                )
            }
        } else {
            Column(
                modifier = Modifier.padding(contentPadding),
                verticalArrangement = Arrangement.spacedBy(QualityDesignTokens.stackGap),
                content = content,
            )
        }
    }
}

@Composable
fun QualityPanelCard(
    modifier: Modifier = Modifier,
    containerColor: Color = QualityDesignTokens.glass,
    borderColor: Color = QualityDesignTokens.borderStrong,
    content: @Composable ColumnScope.() -> Unit,
) {
    QualityGlassCard(
        modifier = modifier,
        containerColor = containerColor,
        borderColor = borderColor,
        contentPadding = QualityDesignTokens.largeCardPadding,
        content = content,
    )
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
        selected -> QualityDesignTokens.primary
        else -> QualityDesignTokens.borderStrong
    }
    val containerColor = when {
        !enabled -> QualityDesignTokens.glassSoft.copy(alpha = 0.55f)
        selected -> QualityDesignTokens.primaryContainer.copy(alpha = 0.38f)
        else -> QualityDesignTokens.glass
    }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 78.dp)
            .clickable(enabled = enabled, onClick = onClick),
        color = containerColor,
        shape = QualityDesignTokens.glassShape,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
    size: androidx.compose.ui.unit.Dp = QualityDesignTokens.listIconSize,
    circle: Boolean = true,
) {
    Box(
        modifier = modifier
            .size(size)
            .background(containerColor, if (circle) CircleShape else QualityDesignTokens.innerShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(size * 0.5f))
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
        Spacer(Modifier.size(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = QualityDesignTokens.textPrimary,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineLarge,
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
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = QualityDesignTokens.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineLarge,
            color = QualityDesignTokens.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun QualityDivider() {
    HorizontalDivider(color = QualityDesignTokens.borderStrong.copy(alpha = 0.45f))
}

@Composable
fun QualityStatTile(
    title: String,
    value: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Surface(
        modifier = modifier
            .defaultMinSize(minHeight = 118.dp)
            .fillMaxWidth(),
        color = QualityDesignTokens.glass,
        shape = QualityDesignTokens.glassShape,
        border = BorderStroke(1.dp, QualityDesignTokens.borderStrong),
        shadowElevation = 4.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(22.dp),
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = QualityDesignTokens.textSecondary,
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.displayLarge,
                    color = QualityDesignTokens.textPrimary,
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = QualityDesignTokens.textSecondary,
                    )
                }
            }
        }
    }
}

@Composable
fun QualityMiniStatCard(
    label: String,
    value: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    QualityGlassCard(
        modifier = modifier,
        contentPadding = 14.dp,
        innerPadding = 0.dp,
    ) {
        QualityIconTile(icon = icon, tint = tint, containerColor = tint.copy(alpha = 0.12f), size = 44.dp)
        Text(label, style = MaterialTheme.typography.titleMedium, color = QualityDesignTokens.textTertiary)
        val parts = value.split(" ", limit = 2)
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                parts.first(),
                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
                color = QualityDesignTokens.textPrimary,
                maxLines = 1,
            )
            if (parts.size > 1) {
                Text(
                    parts[1],
                    style = MaterialTheme.typography.titleMedium,
                    color = QualityDesignTokens.textPrimary,
                    modifier = Modifier.padding(bottom = 4.dp),
                    maxLines = 1,
                )
            }
        }
    }
}
