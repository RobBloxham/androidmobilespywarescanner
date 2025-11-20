package com.spywarescanner.app.ui.screens.removal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.spywarescanner.app.ui.components.CyberCard
import com.spywarescanner.app.ui.components.GlowingButton
import com.spywarescanner.app.ui.theme.CyberColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemovalGuideScreen(
    packageName: String,
    onNavigateBack: () -> Unit,
    viewModel: RemovalGuideViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var completedSteps by remember { mutableStateOf(setOf<Int>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Removal Guide") },
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
                // Warning header
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
                                .clip(CircleShape)
                                .background(CyberColors.ThreatHigh.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = CyberColors.ThreatHigh
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = uiState.app?.appName ?: "Threat Removal",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = CyberColors.TextPrimary
                            )
                            Text(
                                text = "Follow these steps carefully",
                                style = MaterialTheme.typography.bodySmall,
                                color = CyberColors.TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Step-by-Step Instructions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = CyberColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            itemsIndexed(uiState.removalSteps) { index, step ->
                RemovalStepCard(
                    stepNumber = index + 1,
                    description = step,
                    isCompleted = index in completedSteps,
                    onToggleComplete = {
                        completedSteps = if (index in completedSteps) {
                            completedSteps - index
                        } else {
                            completedSteps + index
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                if (completedSteps.size == uiState.removalSteps.size && uiState.removalSteps.isNotEmpty()) {
                    GlowingButton(
                        text = "Threat Removed",
                        onClick = onNavigateBack,
                        modifier = Modifier.fillMaxWidth(),
                        color = CyberColors.Safe
                    )
                } else {
                    GlowingButton(
                        text = "Done",
                        onClick = onNavigateBack,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun RemovalStepCard(
    stepNumber: Int,
    description: String,
    isCompleted: Boolean,
    onToggleComplete: () -> Unit
) {
    CyberCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onToggleComplete
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCompleted) {
                            CyberColors.Safe.copy(alpha = 0.2f)
                        } else {
                            CyberColors.NeonBlue.copy(alpha = 0.2f)
                        }
                    )
                    .border(
                        width = 2.dp,
                        color = if (isCompleted) CyberColors.Safe else CyberColors.NeonBlue,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = CyberColors.Safe,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = stepNumber.toString(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = CyberColors.NeonBlue
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isCompleted) {
                    CyberColors.TextSecondary
                } else {
                    CyberColors.TextPrimary
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
