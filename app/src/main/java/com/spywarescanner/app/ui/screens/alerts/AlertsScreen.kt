package com.spywarescanner.app.ui.screens.alerts

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.spywarescanner.app.data.model.Alert
import com.spywarescanner.app.data.model.AlertType
import com.spywarescanner.app.ui.components.CyberCard
import com.spywarescanner.app.ui.theme.CyberColors
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    onNavigateToThreatDetails: (String) -> Unit,
    viewModel: AlertsViewModel = hiltViewModel()
) {
    val alerts by viewModel.alerts.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alerts") },
                actions = {
                    if (alerts.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearAllAlerts() }) {
                            Icon(
                                imageVector = Icons.Default.ClearAll,
                                contentDescription = "Clear all",
                                tint = CyberColors.TextSecondary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Clear",
                                color = CyberColors.TextSecondary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CyberColors.DarkSurface,
                    titleContentColor = CyberColors.TextPrimary
                )
            )
        },
        containerColor = CyberColors.DarkBackground
    ) { paddingValues ->
        if (alerts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsOff,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = CyberColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Alerts",
                        style = MaterialTheme.typography.titleMedium,
                        color = CyberColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You're all caught up!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CyberColors.TextTertiary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(alerts) { alert ->
                    AlertCard(
                        alert = alert,
                        onClick = {
                            viewModel.markAsRead(alert.id)
                            alert.packageName?.let { onNavigateToThreatDetails(it) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AlertCard(
    alert: Alert,
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
            verticalAlignment = Alignment.Top
        ) {
            // Unread indicator
            if (!alert.isRead) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(CyberColors.NeonGreen)
                )
                Spacer(modifier = Modifier.width(12.dp))
            } else {
                Spacer(modifier = Modifier.width(20.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = alert.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (!alert.isRead) FontWeight.Bold else FontWeight.Normal,
                        color = CyberColors.TextPrimary
                    )

                    Text(
                        text = formatAlertTime(alert.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = CyberColors.TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = alert.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CyberColors.TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                AlertTypeBadge(type = alert.type)
            }
        }
    }
}

@Composable
fun AlertTypeBadge(type: AlertType) {
    val (color, icon) = when (type) {
        AlertType.NEW_THREAT -> CyberColors.ThreatHigh to "THREAT"
        AlertType.PERMISSION_CHANGE -> CyberColors.ThreatMedium to "PERMISSION"
        AlertType.NEW_INSTALL -> CyberColors.NeonCyan to "NEW APP"
        AlertType.SCAN_COMPLETE -> CyberColors.NeonGreen to "SCAN"
        AlertType.WEEKLY_REPORT -> CyberColors.NeonBlue to "REPORT"
        AlertType.SUSPICIOUS_ACTIVITY -> CyberColors.ThreatHigh to "SUSPICIOUS"
    }

    Box(
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.2f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun formatAlertTime(timestamp: LocalDateTime): String {
    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    return when {
        timestamp.toLocalDate() == now.toLocalDate() -> timestamp.format(formatter)
        timestamp.toLocalDate() == now.minusDays(1).toLocalDate() -> "Yesterday"
        else -> timestamp.format(DateTimeFormatter.ofPattern("MMM d"))
    }
}
