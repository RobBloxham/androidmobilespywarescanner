package com.spywarescanner.app.ui.screens.threat

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import com.spywarescanner.app.ui.components.CyberCard
import com.spywarescanner.app.ui.components.GlowingButton
import com.spywarescanner.app.ui.components.ThreatLevelBadge
import com.spywarescanner.app.ui.theme.CyberColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreatDetailsScreen(
    packageName: String,
    onNavigateBack: () -> Unit,
    onNavigateToRemovalGuide: (Long) -> Unit,
    viewModel: ThreatDetailsViewModel = hiltViewModel()
) {
    val app by viewModel.app.collectAsState()
    val threats by viewModel.threats.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Threat Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            app?.let { scannedApp ->
                item {
                    // App header
                    CyberCard {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(CyberColors.ThreatHigh.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = CyberColors.ThreatHigh
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = scannedApp.appName,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = CyberColors.TextPrimary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = scannedApp.packageName,
                                style = MaterialTheme.typography.bodySmall,
                                color = CyberColors.TextSecondary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            ThreatLevelBadge(level = scannedApp.threatLevel)

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = scannedApp.riskScore.toString(),
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = CyberColors.ThreatHigh
                                    )
                                    Text(
                                        text = "Risk Score",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = CyberColors.TextSecondary
                                    )
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = scannedApp.suspiciousPermissions.size.toString(),
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = CyberColors.ThreatMedium
                                    )
                                    Text(
                                        text = "Permissions",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = CyberColors.TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                if (scannedApp.suspiciousPermissions.isNotEmpty()) {
                    item {
                        Text(
                            text = "Suspicious Permissions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = CyberColors.TextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    items(scannedApp.suspiciousPermissions) { permission ->
                        PermissionCard(permission)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (threats.isNotEmpty()) {
                    item {
                        Text(
                            text = "Detected Threats",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = CyberColors.TextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    items(threats) { threat ->
                        ThreatItemCard(
                            title = threat.threatType.displayName,
                            description = threat.description
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                item {
                    GlowingButton(
                        text = "View Removal Guide",
                        onClick = {
                            threats.firstOrNull()?.id?.let { threatId ->
                                onNavigateToRemovalGuide(threatId)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        color = CyberColors.NeonOrange
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun PermissionCard(permission: String) {
    CyberCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(CyberColors.ThreatMedium)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = permission.substringAfterLast("."),
                style = MaterialTheme.typography.bodyMedium,
                color = CyberColors.TextPrimary
            )
        }
    }
}

@Composable
fun ThreatItemCard(
    title: String,
    description: String
) {
    CyberCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = CyberColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = CyberColors.TextSecondary
            )
        }
    }
}
