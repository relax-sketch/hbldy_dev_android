package com.example.myapplication.quality.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.quality.ui.design.QualityDesignTokens

@Composable
fun QualityTagChip(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = QualityDesignTokens.surfaceAlt,
    textColor: Color = QualityDesignTokens.textSecondary,
    borderColor: Color = Color.Transparent,
    contentPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
) {
    Box(
        modifier = modifier
            .background(containerColor, QualityDesignTokens.chipShape)
            .then(
                if (borderColor != Color.Transparent) {
                    Modifier.border(1.dp, borderColor, QualityDesignTokens.chipShape)
                } else {
                    Modifier
                },
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, color = textColor, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun QualityFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedContainer: Color = QualityDesignTokens.surfaceSoftGreen,
    selectedText: Color = QualityDesignTokens.passedColor,
) {
    QualityTagChip(
        text = text,
        modifier = modifier.clickable(onClick = onClick),
        containerColor = if (selected) selectedContainer else QualityDesignTokens.surface,
        textColor = if (selected) selectedText else QualityDesignTokens.textSecondary,
        borderColor = if (selected) Color.Transparent else QualityDesignTokens.border,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
    )
}
