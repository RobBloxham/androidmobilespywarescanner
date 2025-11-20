package com.spywarescanner.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Scanner : Screen("scanner")
    object ScanResults : Screen("scan_results/{scanId}") {
        fun createRoute(scanId: Long) = "scan_results/$scanId"
    }
    object Alerts : Screen("alerts")
    object Reports : Screen("reports")
    object Settings : Screen("settings")
    object Premium : Screen("premium")
    object RemovalGuide : Screen("removal_guide/{packageName}") {
        fun createRoute(packageName: String) = "removal_guide/$packageName"
    }
    object ThreatDetails : Screen("threat_details/{packageName}") {
        fun createRoute(packageName: String) = "threat_details/$packageName"
    }
}

data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int = 0
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = Screen.Scanner.route,
        title = "Scanner",
        selectedIcon = Icons.Filled.Security,
        unselectedIcon = Icons.Outlined.Security
    ),
    BottomNavItem(
        route = Screen.Alerts.route,
        title = "Alerts",
        selectedIcon = Icons.Filled.Notifications,
        unselectedIcon = Icons.Outlined.Notifications
    ),
    BottomNavItem(
        route = Screen.Reports.route,
        title = "Reports",
        selectedIcon = Icons.Filled.Assessment,
        unselectedIcon = Icons.Outlined.Assessment
    ),
    BottomNavItem(
        route = Screen.Settings.route,
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
)
