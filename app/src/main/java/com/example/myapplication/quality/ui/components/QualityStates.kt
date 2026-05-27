package com.example.myapplication.quality.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

enum class QualityStatusTone {
    Info,
    Success,
    Warning,
    Error,
}

@Composable
fun QualityStatusCard(
    text: String,
    modifier: Modifier = Modifier,
    tone: QualityStatusTone = QualityStatusTone.Info,
) {
    val icon: ImageVector
    val tint: Color
    val container: Color
    when (tone) {
        QualityStatusTone.Info -> {
            icon = Icons.Rounded.Info
            tint = QualityDesignTokens.executedColor
            container = QualityDesignTokens.executedContainer
        }

        QualityStatusTone.Success -> {
            icon = Icons.Rounded.CheckCircle
            tint = QualityDesignTokens.passedColor
            container = QualityDesignTokens.passedContainer
        }

        QualityStatusTone.Warning -> {
            icon = Icons.Rounded.WarningAmber
            tint = QualityDesignTokens.advisoryColor
            container = QualityDesignTokens.advisoryContainer
        }

        QualityStatusTone.Error -> {
            icon = Icons.Rounded.ErrorOutline
            tint = QualityDesignTokens.mandatoryColor
            container = QualityDesignTokens.mandatoryContainer
        }
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 84.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        shape = QualityDesignTokens.cardShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .padding(start = QualityDesignTokens.mediumCardPadding)
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.82f), QualityDesignTokens.chipShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = tint)
            }
            Spacer(Modifier.size(14.dp))
            Text(
                text = text,
                color = QualityDesignTokens.textPrimary,
                modifier = Modifier.padding(vertical = QualityDesignTokens.mediumCardPadding),
            )
        }
    }
}

@Composable
fun QualityEmptyStateCard(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
) {
    QualityPanelCard(
        modifier = modifier,
        containerColor = QualityDesignTokens.surfaceAlt,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = QualityDesignTokens.textPrimary,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = QualityDesignTokens.textSecondary,
            )
        }
    }
}
