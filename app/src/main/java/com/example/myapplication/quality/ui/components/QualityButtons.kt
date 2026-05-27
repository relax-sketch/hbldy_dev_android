package com.example.myapplication.quality.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.myapplication.quality.ui.design.QualityDesignTokens

@Composable
fun QualityPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
) {
    QualityBrushButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        brush = if (enabled) {
            QualityDesignTokens.primaryButtonBrush
        } else {
            Brush.linearGradient(listOf(QualityDesignTokens.borderStrong, QualityDesignTokens.borderStrong))
        },
        textColor = if (enabled) QualityDesignTokens.onPrimary else QualityDesignTokens.textSecondary,
        borderColor = Color.Transparent,
        minHeight = QualityDesignTokens.buttonHeight,
        horizontalPadding = 24.dp,
        cornerRadius = 24.dp,
    )
}

@Composable
fun QualitySecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    accentColor: Color = QualityDesignTokens.textPrimary,
    leadingIcon: ImageVector? = null,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = QualityDesignTokens.secondaryButtonHeight),
        onClick = onClick,
        enabled = enabled,
        color = QualityDesignTokens.surface,
        contentColor = if (enabled) accentColor else QualityDesignTokens.textTertiary,
        shape = QualityDesignTokens.buttonShape,
        border = BorderStroke(1.dp, QualityDesignTokens.borderStrong),
        shadowElevation = 0.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            contentAlignment = Alignment.Center,
        ) {
            ButtonLabel(
                text = text,
                color = if (enabled) accentColor else QualityDesignTokens.textTertiary,
                leadingIcon = leadingIcon,
            )
        }
    }
}

@Composable
fun RowScope.QualityButtonPair(
    primaryText: String,
    onPrimaryClick: () -> Unit,
    secondaryText: String,
    onSecondaryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    QualityPrimaryButton(
        text = primaryText,
        onClick = onPrimaryClick,
        modifier = modifier.weight(1f),
    )
    QualitySecondaryButton(
        text = secondaryText,
        onClick = onSecondaryClick,
        modifier = modifier.weight(1f),
    )
}

@Composable
fun QualityModePill(
    label: String,
    checked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val background = if (checked) {
        QualityDesignTokens.primaryButtonBrush
    } else {
        Brush.linearGradient(listOf(QualityDesignTokens.surface, QualityDesignTokens.surface))
    }
    QualityBrushButton(
        text = label,
        onClick = onClick,
        modifier = modifier,
        enabled = true,
        leadingIcon = null,
        brush = background,
        textColor = if (checked) QualityDesignTokens.onPrimary else QualityDesignTokens.textSecondary,
        borderColor = if (checked) Color.Transparent else QualityDesignTokens.borderStrong,
        minHeight = 54.dp,
        horizontalPadding = 20.dp,
        cornerRadius = 22.dp,
    )
}

@Composable
private fun QualityBrushButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean,
    leadingIcon: ImageVector?,
    brush: Brush,
    textColor: Color,
    borderColor: Color,
    minHeight: Dp,
    horizontalPadding: Dp,
    cornerRadius: Dp,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = minHeight),
        onClick = onClick,
        enabled = enabled,
        color = Color.Transparent,
        contentColor = textColor,
        shape = RoundedCornerShape(cornerRadius),
        shadowElevation = if (enabled) 12.dp else 0.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = brush, shape = RoundedCornerShape(cornerRadius))
                .then(
                    if (borderColor != Color.Transparent) {
                        Modifier.border(1.dp, borderColor, RoundedCornerShape(cornerRadius))
                    } else {
                        Modifier
                    },
                )
                .padding(horizontal = horizontalPadding, vertical = 18.dp),
            contentAlignment = Alignment.Center,
        ) {
            ButtonLabel(text = text, color = textColor, leadingIcon = leadingIcon)
        }
    }
}

@Composable
private fun ButtonLabel(
    text: String,
    color: Color,
    leadingIcon: ImageVector?,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp),
            )
            Spacer(Modifier.size(10.dp))
        }
        Text(text = text, color = color, textAlign = TextAlign.Center)
    }
}
