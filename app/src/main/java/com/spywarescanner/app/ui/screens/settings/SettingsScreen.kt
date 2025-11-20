package com.spywarescanner.app.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.spywarescanner.app.ui.components.CyberCard
import com.spywarescanner.app.ui.theme.CyberColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToPremium: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val preferences by viewModel.preferences.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CyberColors.DarkSurface,
                    titleContentColor = CyberColors.TextPrimary
                )
            )
        },
        containerColor = CyberColors.DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Premium section
            SettingsSection(title = "Premium") {
                SettingsItem(
                    icon = Icons.Default.Star,
                    title = if (preferences.isPremiumUser) "Premium Active" else "Upgrade to Premium",
                    subtitle = if (preferences.isPremiumUser) "All features unlocked" else "Unlock advanced features",
                    onClick = if (!preferences.isPremiumUser) onNavigateToPremium else null,
                    showChevron = !preferences.isPremiumUser
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scanning section
            SettingsSection(title = "Scanning") {
                SettingsSwitchItem(
                    icon = Icons.Default.Schedule,
                    title = "Automatic Scanning",
                    subtitle = "Schedule regular scans",
                    checked = preferences.autoScanEnabled,
                    onCheckedChange = { viewModel.setAutoScanEnabled(it) }
                )

                if (preferences.autoScanEnabled) {
                    SettingsItem(
                        icon = Icons.Default.Schedule,
                        title = "Scan Frequency",
                        subtitle = preferences.scanFrequency.displayName,
                        onClick = { /* Show frequency picker */ }
                    )
                }

                SettingsSwitchItem(
                    icon = Icons.Default.Security,
                    title = "Real-time Protection",
                    subtitle = if (preferences.isPremiumUser) {
                        "Monitor apps continuously"
                    } else {
                        "Premium feature"
                    },
                    checked = preferences.realTimeProtectionEnabled,
                    onCheckedChange = { viewModel.setRealTimeProtection(it) },
                    enabled = preferences.isPremiumUser
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notifications section
            SettingsSection(title = "Notifications") {
                SettingsSwitchItem(
                    icon = Icons.Default.Notifications,
                    title = "Threat Alerts",
                    subtitle = "Get notified about detected threats",
                    checked = preferences.threatNotificationsEnabled,
                    onCheckedChange = { viewModel.setThreatNotifications(it) }
                )

                SettingsSwitchItem(
                    icon = Icons.Default.Notifications,
                    title = "Weekly Reports",
                    subtitle = "Receive weekly security summaries",
                    checked = preferences.reportNotificationsEnabled,
                    onCheckedChange = { viewModel.setReportNotifications(it) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // About section
            SettingsSection(title = "About") {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Version",
                    subtitle = "1.0.0",
                    onClick = null
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = CyberColors.TextSecondary,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        CyberCard {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)?,
    showChevron: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = CyberColors.NeonGreen
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = CyberColors.TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = CyberColors.TextSecondary
            )
        }

        if (showChevron && onClick != null) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = CyberColors.TextSecondary
            )
        }
    }
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) CyberColors.NeonGreen else CyberColors.TextTertiary
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) CyberColors.TextPrimary else CyberColors.TextTertiary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = CyberColors.TextSecondary
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CyberColors.NeonGreen,
                checkedTrackColor = CyberColors.NeonGreen.copy(alpha = 0.5f),
                uncheckedThumbColor = CyberColors.TextTertiary,
                uncheckedTrackColor = CyberColors.DarkSurfaceVariant
            )
        )
    }
}
