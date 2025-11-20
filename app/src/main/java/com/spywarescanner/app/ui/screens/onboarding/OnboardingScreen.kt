package com.spywarescanner.app.ui.screens.onboarding

import android.Manifest
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.spywarescanner.app.ui.components.GlowingButton
import com.spywarescanner.app.ui.theme.CyberColors
import kotlinx.coroutines.launch

data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val description: String
)

val onboardingPages = listOf(
    OnboardingPage(
        icon = Icons.Default.Shield,
        title = "AI-Powered Protection",
        description = "Advanced machine learning algorithms scan your device for hidden spyware, tracking apps, and suspicious software"
    ),
    OnboardingPage(
        icon = Icons.Default.Security,
        title = "Permission Analysis",
        description = "Identify apps with dangerous permission combinations that could be used to spy on your activities"
    ),
    OnboardingPage(
        icon = Icons.Default.Notifications,
        title = "Real-Time Alerts",
        description = "Get instant notifications when new threats are detected or suspicious apps are installed"
    ),
    OnboardingPage(
        icon = Icons.Default.Lock,
        title = "Complete Security",
        description = "Weekly reports, step-by-step removal guides, and premium protection features for ultimate security"
    )
)

@OptIn(ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size + 1 })
    val scope = rememberCoroutineScope()

    val permissionsToRequest = mutableListOf<String>()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
    }

    val permissionsState = rememberMultiplePermissionsState(permissionsToRequest)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberColors.DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (pagerState.currentPage < onboardingPages.size) {
                    TextButton(onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(onboardingPages.size)
                        }
                    }) {
                        Text(
                            text = "Skip",
                            color = CyberColors.TextSecondary
                        )
                    }
                }
            }

            // Pager content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                if (page < onboardingPages.size) {
                    OnboardingPageContent(page = onboardingPages[page])
                } else {
                    PermissionsPage(
                        onRequestPermissions = {
                            permissionsState.launchMultiplePermissionRequest()
                        },
                        permissionsGranted = permissionsState.allPermissionsGranted || permissionsToRequest.isEmpty()
                    )
                }
            }

            // Page indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(onboardingPages.size + 1) { index ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (pagerState.currentPage == index) 24.dp else 8.dp, 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == index) {
                                    CyberColors.NeonGreen
                                } else {
                                    CyberColors.DarkSurfaceVariant
                                }
                            )
                    )
                }
            }

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                if (pagerState.currentPage == onboardingPages.size) {
                    GlowingButton(
                        text = "Get Started",
                        onClick = {
                            viewModel.completeOnboarding()
                            onComplete()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    GlowingButton(
                        text = "Continue",
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(CyberColors.NeonGreen.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = CyberColors.NeonGreen
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = CyberColors.TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = CyberColors.TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PermissionsPage(
    onRequestPermissions: () -> Unit,
    permissionsGranted: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    if (permissionsGranted) {
                        CyberColors.NeonGreen.copy(alpha = 0.1f)
                    } else {
                        CyberColors.NeonOrange.copy(alpha = 0.1f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (permissionsGranted) {
                    Icons.Default.CheckCircle
                } else {
                    Icons.Default.Security
                },
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = if (permissionsGranted) {
                    CyberColors.NeonGreen
                } else {
                    CyberColors.NeonOrange
                }
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = if (permissionsGranted) {
                "You're All Set!"
            } else {
                "Grant Permissions"
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = CyberColors.TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (permissionsGranted) {
                "Your device is ready to be protected. Tap 'Get Started' to begin scanning for threats."
            } else {
                "To provide complete protection, we need permission to send you alerts when threats are detected."
            },
            style = MaterialTheme.typography.bodyLarge,
            color = CyberColors.TextSecondary,
            textAlign = TextAlign.Center
        )

        if (!permissionsGranted) {
            Spacer(modifier = Modifier.height(32.dp))

            GlowingButton(
                text = "Grant Permissions",
                onClick = onRequestPermissions,
                color = CyberColors.NeonOrange
            )
        }
    }
}
