package com.spywarescanner.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.spywarescanner.app.data.model.ThreatLevel
import com.spywarescanner.app.ui.theme.CyberColors

@Composable
fun ThreatMeter(
    score: Int,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp
) {
    val threatColor = when {
        score >= 80 -> CyberColors.Safe
        score >= 60 -> CyberColors.ThreatLow
        score >= 40 -> CyberColors.ThreatMedium
        score >= 20 -> CyberColors.ThreatHigh
        else -> CyberColors.ThreatCritical
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidth = 20.dp.toPx()
            val radius = (size.toPx() - strokeWidth) / 2

            // Background arc
            drawArc(
                color = CyberColors.DarkSurfaceVariant,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Score arc
            val sweepAngle = (score / 100f) * 270f
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(threatColor, threatColor.copy(alpha = 0.5f))
                ),
                startAngle = 135f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = threatColor
            )
            Text(
                text = "Security Score",
                style = MaterialTheme.typography.bodySmall,
                color = CyberColors.TextSecondary
            )
        }
    }
}

@Composable
fun ThreatLevelBadge(
    level: ThreatLevel,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (level) {
        ThreatLevel.CRITICAL -> CyberColors.ThreatCritical to "Critical"
        ThreatLevel.HIGH -> CyberColors.ThreatHigh to "High"
        ThreatLevel.MEDIUM -> CyberColors.ThreatMedium to "Medium"
        ThreatLevel.LOW -> CyberColors.ThreatLow to "Low"
        ThreatLevel.SAFE -> CyberColors.Safe to "Safe"
    }

    Box(
        modifier = modifier
            .background(
                color = color.copy(alpha = 0.2f),
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = 1.dp,
                color = color,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ScanningAnimation(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Outer ring
        Box(
            modifier = Modifier
                .size(size)
                .rotate(rotation)
                .border(
                    width = 3.dp,
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            CyberColors.NeonGreen,
                            CyberColors.NeonGreen.copy(alpha = 0f),
                            CyberColors.NeonGreen.copy(alpha = 0f)
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Inner shield icon
        Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            modifier = Modifier.size(size / 2),
            tint = CyberColors.NeonGreen
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CyberCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = CyberColors.DarkCard
        ),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick ?: {}
    ) {
        Box(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = CyberColors.DarkBorder,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            content()
        }
    }
}

@Composable
fun NeonProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = CyberColors.NeonGreen
) {
    LinearProgressIndicator(
        progress = progress,
        modifier = modifier
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
        color = color,
        trackColor = CyberColors.DarkSurfaceVariant
    )
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = CyberColors.NeonGreen
) {
    CyberCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = CyberColors.TextSecondary
            )
        }
    }
}

@Composable
fun GlowingButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = CyberColors.NeonGreen
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = if (enabled) {
                    Brush.horizontalGradient(
                        colors = listOf(color, color.copy(alpha = 0.8f))
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(
                            CyberColors.DarkSurfaceVariant,
                            CyberColors.DarkSurfaceVariant
                        )
                    )
                }
            )
            .then(
                if (enabled) {
                    Modifier.border(
                        width = 1.dp,
                        color = color.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else Modifier
            )
            .padding(horizontal = 32.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = if (enabled) CyberColors.DarkBackground else CyberColors.TextTertiary
        )
    }
}
