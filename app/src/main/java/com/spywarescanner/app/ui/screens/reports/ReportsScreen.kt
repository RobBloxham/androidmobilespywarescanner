package com.spywarescanner.app.ui.screens.reports

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.spywarescanner.app.ui.components.CyberCard
import com.spywarescanner.app.ui.components.GlowingButton
import com.spywarescanner.app.ui.components.StatCard
import com.spywarescanner.app.ui.theme.CyberColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onNavigateToPremium: () -> Unit,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Security Reports") },
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
            // Stats overview
            Text(
                text = "This Week",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = CyberColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Scans",
                    value = "3",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Threats",
                    value = "0",
                    modifier = Modifier.weight(1f),
                    valueColor = CyberColors.Safe
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Apps Checked",
                    value = "127",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Security Score",
                    value = "95",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Trend card
            CyberCard {
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
                            .background(CyberColors.NeonGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = CyberColors.NeonGreen
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "+5%",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = CyberColors.NeonGreen
                        )
                        Text(
                            text = "Security improved",
                            style = MaterialTheme.typography.bodySmall,
                            color = CyberColors.TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Premium upsell
            Text(
                text = "Detailed Reports",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = CyberColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            CyberCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = CyberColors.NeonPurple
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Upgrade to Premium",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CyberColors.TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Get detailed weekly and monthly reports with threat trends, security insights, and recommendations",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CyberColors.TextSecondary,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    GlowingButton(
                        text = "Upgrade Now",
                        onClick = onNavigateToPremium,
                        color = CyberColors.NeonPurple
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
