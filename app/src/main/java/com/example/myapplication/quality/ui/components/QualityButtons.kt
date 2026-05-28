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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
    trailingIcon: ImageVector? = null,
    filledMint: Boolean = false,
    pill: Boolean = false,
) {
    val shape = if (pill) QualityDesignTokens.pillShape else QualityDesignTokens.buttonShape
    val brush = when {
        !enabled -> Brush.horizontalGradient(listOf(QualityDesignTokens.surfaceContainer, QualityDesignTokens.surfaceContainer))
        filledMint -> QualityDesignTokens.primaryFixedButtonBrush
        else -> QualityDesignTokens.primaryButtonBrush
    }
    val textColor = when {
        !enabled -> QualityDesignTokens.textTertiary
        filledMint -> QualityDesignTokens.onPrimaryFixed
        else -> QualityDesignTokens.onPrimary
    }
    QualityBrushButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        brush = brush,
        textColor = textColor,
        borderColor = Color.Transparent,
        minHeight = if (pill) QualityDesignTokens.floatingButtonHeight else QualityDesignTokens.buttonHeight,
        horizontalPadding = 20.dp,
        shape = shape,
    )
}

@Composable
fun QualitySecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    accentColor: Color = QualityDesignTokens.primary,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    pill: Boolean = false,
) {
    val shape = if (pill) QualityDesignTokens.pillShape else QualityDesignTokens.buttonShape
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = QualityDesignTokens.buttonHeight),
        onClick = onClick,
        enabled = enabled,
        color = QualityDesignTokens.glass,
        contentColor = if (enabled) accentColor else QualityDesignTokens.textTertiary,
        shape = shape,
        border = BorderStroke(1.dp, QualityDesignTokens.borderStrong),
        shadowElevation = 6.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            ButtonLabel(
                text = text,
                color = if (enabled) accentColor else QualityDesignTokens.textTertiary,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
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
    QualitySecondaryButton(
        text = secondaryText,
        onClick = onSecondaryClick,
        modifier = modifier.weight(1f),
    )
    QualityPrimaryButton(
        text = primaryText,
        onClick = onPrimaryClick,
        modifier = modifier.weight(1f),
        filledMint = true,
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
        Brush.horizontalGradient(listOf(QualityDesignTokens.glass, QualityDesignTokens.glass))
    }
    QualityBrushButton(
        text = label,
        onClick = onClick,
        modifier = modifier,
        enabled = true,
        leadingIcon = null,
        trailingIcon = null,
        brush = background,
        textColor = if (checked) QualityDesignTokens.onPrimary else QualityDesignTokens.textSecondary,
        borderColor = if (checked) Color.Transparent else QualityDesignTokens.borderStrong,
        minHeight = 36.dp,
        horizontalPadding = 12.dp,
        shape = QualityDesignTokens.pillShape,
    )
}

@Composable
private fun QualityBrushButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean,
    leadingIcon: ImageVector?,
    trailingIcon: ImageVector?,
    brush: Brush,
    textColor: Color,
    borderColor: Color,
    minHeight: Dp,
    horizontalPadding: Dp,
    shape: RoundedCornerShape,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = minHeight),
        onClick = onClick,
        enabled = enabled,
        color = Color.Transparent,
        contentColor = textColor,
        shape = shape,
        shadowElevation = if (enabled) 8.dp else 0.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = brush, shape = shape)
                .then(
                    if (borderColor != Color.Transparent) {
                        Modifier.border(1.dp, borderColor, shape)
                    } else {
                        Modifier
                    },
                )
                .padding(horizontal = horizontalPadding, vertical = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            ButtonLabel(
                text = text,
                color = textColor,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
            )
        }
    }
}

@Composable
private fun ButtonLabel(
    text: String,
    color: Color,
    leadingIcon: ImageVector?,
    trailingIcon: ImageVector?,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (leadingIcon != null) {
            Icon(leadingIcon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.size(8.dp))
        }
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (trailingIcon != null) {
            Spacer(Modifier.size(8.dp))
            Icon(trailingIcon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        }
    }
}
