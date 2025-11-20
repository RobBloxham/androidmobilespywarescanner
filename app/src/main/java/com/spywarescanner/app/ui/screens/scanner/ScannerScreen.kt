package com.spywarescanner.app.ui.screens.scanner

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.spywarescanner.app.data.model.ScanPhase
import com.spywarescanner.app.data.model.ScanType
import com.spywarescanner.app.ui.components.CyberCard
import com.spywarescanner.app.ui.components.GlowingButton
import com.spywarescanner.app.ui.components.NeonProgressBar
import com.spywarescanner.app.ui.components.ScanningAnimation
import com.spywarescanner.app.ui.components.StatCard
import com.spywarescanner.app.ui.components.ThreatMeter
import com.spywarescanner.app.ui.theme.CyberColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ScannerScreen(
    onNavigateToResults: (Long) -> Unit,
    onNavigateToPremium: () -> Unit,
    viewModel: ScannerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scanProgress by viewModel.scanProgress.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberColors.DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Security Scanner",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = CyberColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "AI-Powered Spyware Detection",
                style = MaterialTheme.typography.bodyMedium,
                color = CyberColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main scanner area
            AnimatedContent(
                targetState = uiState.isScanning,
                label = "scanner"
            ) { isScanning ->
                if (isScanning) {
                    ScanningView(
                        progress = scanProgress,
                        onStop = { viewModel.stopScan() }
                    )
                } else {
                    IdleView(
                        securityScore = uiState.securityScore,
                        lastScanTime = uiState.lastScanTime,
                        threatsFound = uiState.lastThreatsFound,
                        onStartScan = { scanType -> viewModel.startScan(scanType) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Apps Scanned",
                    value = uiState.totalAppsScanned.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Threats Found",
                    value = uiState.lastThreatsFound.toString(),
                    modifier = Modifier.weight(1f),
                    valueColor = if (uiState.lastThreatsFound > 0) {
                        CyberColors.ThreatHigh
                    } else {
                        CyberColors.Safe
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Scan type options
            Text(
                text = "Scan Options",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = CyberColors.TextPrimary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            ScanTypeCard(
                icon = Icons.Default.FlashOn,
                title = "Quick Scan",
                description = "Fast scan of installed apps",
                onClick = { viewModel.startScan(ScanType.QUICK) },
                enabled = !uiState.isScanning
            )

            Spacer(modifier = Modifier.height(8.dp))

            ScanTypeCard(
                icon = Icons.Default.BugReport,
                title = "Deep Scan",
                description = "Thorough analysis with AI detection",
                onClick = { viewModel.startScan(ScanType.DEEP) },
                enabled = !uiState.isScanning
            )

            Spacer(modifier = Modifier.height(8.dp))

            ScanTypeCard(
                icon = Icons.Default.Star,
                title = "Real-time Protection",
                description = "Continuous monitoring (Premium)",
                onClick = onNavigateToPremium,
                enabled = true,
                isPremium = true
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Navigate to results when scan completes
        if (uiState.lastScanId != null && !uiState.isScanning && uiState.shouldNavigateToResults) {
            viewModel.onNavigatedToResults()
            onNavigateToResults(uiState.lastScanId!!)
        }
    }
}

@Composable
fun IdleView(
    securityScore: Int,
    lastScanTime: Long,
    threatsFound: Int,
    onStartScan: (ScanType) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ThreatMeter(
            score = securityScore,
            size = 200.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (lastScanTime > 0) {
                "Last scan: ${formatTime(lastScanTime)}"
            } else {
                "No scans performed yet"
            },
            style = MaterialTheme.typography.bodySmall,
            color = CyberColors.TextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        GlowingButton(
            text = "Start Scan",
            onClick = { onStartScan(ScanType.QUICK) }
        )
    }
}

@Composable
fun ScanningView(
    progress: ScannerViewModel.ScanProgressState,
    onStop: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScanningAnimation(size = 150.dp)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = progress.phase.displayName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = CyberColors.NeonGreen
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (progress.currentApp.isNotEmpty()) {
            Text(
                text = progress.currentApp,
                style = MaterialTheme.typography.bodySmall,
                color = CyberColors.TextSecondary,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (progress.totalApps > 0) {
            NeonProgressBar(
                progress = progress.currentIndex.toFloat() / progress.totalApps,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${progress.currentIndex} / ${progress.totalApps} apps",
                style = MaterialTheme.typography.bodySmall,
                color = CyberColors.TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        GlowingButton(
            text = "Stop Scan",
            onClick = onStop,
            color = CyberColors.NeonRed
        )
    }
}

@Composable
fun ScanTypeCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    enabled: Boolean,
    isPremium: Boolean = false
) {
    CyberCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isPremium) {
                            CyberColors.NeonPurple.copy(alpha = 0.2f)
                        } else {
                            CyberColors.NeonGreen.copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isPremium) CyberColors.NeonPurple else CyberColors.NeonGreen
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (enabled) CyberColors.TextPrimary else CyberColors.TextTertiary
                    )
                    if (isPremium) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PRO",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberColors.NeonPurple,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = CyberColors.TextSecondary
                )
            }
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60000 -> "Just now"
        diff < 3600000 -> "${diff / 60000} min ago"
        diff < 86400000 -> "${diff / 3600000} hours ago"
        else -> SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(timestamp))
    }
}
