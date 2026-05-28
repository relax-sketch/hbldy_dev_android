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
    containerColor: Color = QualityDesignTokens.surfaceContainerLow,
    textColor: Color = QualityDesignTokens.textSecondary,
    borderColor: Color = Color.Transparent,
    contentPadding: PaddingValues = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
) {
    Box(
        modifier = modifier
            .background(containerColor, QualityDesignTokens.innerShape)
            .then(
                if (borderColor != Color.Transparent) {
                    Modifier.border(1.dp, borderColor, QualityDesignTokens.innerShape)
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
    selectedContainer: Color = QualityDesignTokens.primary,
    selectedText: Color = QualityDesignTokens.onPrimary,
) {
    QualityTagChip(
        text = text,
        modifier = modifier.clickable(onClick = onClick),
        containerColor = if (selected) selectedContainer else Color.Transparent,
        textColor = if (selected) selectedText else QualityDesignTokens.textSecondary,
        borderColor = if (selected) Color.Transparent else Color.Transparent,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 9.dp),
    )
}
