package com.spywarescanner.app.ui.screens.results

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.spywarescanner.app.data.model.ScannedApp
import com.spywarescanner.app.data.model.ThreatLevel
import com.spywarescanner.app.ui.components.CyberCard
import com.spywarescanner.app.ui.components.GlowingButton
import com.spywarescanner.app.ui.components.ThreatLevelBadge
import com.spywarescanner.app.ui.components.ThreatMeter
import com.spywarescanner.app.ui.theme.CyberColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanResultsScreen(
    scanId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToThreatDetails: (String) -> Unit,
    onNavigateToRemovalGuide: (String) -> Unit,
    viewModel: ScanResultsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan Results") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CyberColors.DarkSurface,
                    titleContentColor = CyberColors.TextPrimary,
                    navigationIconContentColor = CyberColors.TextPrimary
                )
            )
        },
        containerColor = CyberColors.DarkBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                // Security Score
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ThreatMeter(score = uiState.securityScore, size = 180.dp)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Summary Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ResultStat(
                            icon = Icons.Default.Apps,
                            label = "Apps Scanned",
                            value = uiState.totalApps.toString()
                        )
                        ResultStat(
                            icon = Icons.Default.Warning,
                            label = "Threats",
                            value = uiState.threatsFound.toString(),
                            color = if (uiState.threatsFound > 0) CyberColors.ThreatHigh else CyberColors.Safe
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            if (uiState.threateningApps.isNotEmpty()) {
                item {
                    Text(
                        text = "Threats Detected",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = CyberColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(uiState.threateningApps) { app ->
                    AppThreatCard(
                        app = app,
                        onClick = { onNavigateToThreatDetails(app.packageName) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else {
                item {
                    NoThreatsCard()
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            item {
                GlowingButton(
                    text = "Done",
                    onClick = onNavigateBack,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun AppThreatCard(
    app: ScannedApp,
    onClick: () -> Unit
) {
    CyberCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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
                        when (app.threatLevel) {
                            ThreatLevel.CRITICAL -> CyberColors.ThreatCritical.copy(alpha = 0.2f)
                            ThreatLevel.HIGH -> CyberColors.ThreatHigh.copy(alpha = 0.2f)
                            ThreatLevel.MEDIUM -> CyberColors.ThreatMedium.copy(alpha = 0.2f)
                            ThreatLevel.LOW -> CyberColors.ThreatLow.copy(alpha = 0.2f)
                            ThreatLevel.SAFE -> CyberColors.Safe.copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = when (app.threatLevel) {
                        ThreatLevel.CRITICAL -> CyberColors.ThreatCritical
                        ThreatLevel.HIGH -> CyberColors.ThreatHigh
                        ThreatLevel.MEDIUM -> CyberColors.ThreatMedium
                        ThreatLevel.LOW -> CyberColors.ThreatLow
                        ThreatLevel.SAFE -> CyberColors.Safe
                    }
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.appName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = CyberColors.TextPrimary
                )
                Text(
                    text = "${app.suspiciousPermissions.size} suspicious permissions",
                    style = MaterialTheme.typography.bodySmall,
                    color = CyberColors.TextSecondary
                )
            }

            ThreatLevelBadge(level = app.threatLevel)

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = CyberColors.TextSecondary
            )
        }
    }
}

@Composable
fun NoThreatsCard() {
    CyberCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = CyberColors.Safe
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No Threats Detected",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = CyberColors.Safe
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your device appears to be safe",
                style = MaterialTheme.typography.bodyMedium,
                color = CyberColors.TextSecondary
            )
        }
    }
}

@Composable
fun ResultStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color = CyberColors.NeonGreen
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = CyberColors.TextSecondary
        )
    }
}
