package com.spywarescanner.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.spywarescanner.app.ui.screens.alerts.AlertsScreen
import com.spywarescanner.app.ui.screens.onboarding.OnboardingScreen
import com.spywarescanner.app.ui.screens.premium.PremiumScreen
import com.spywarescanner.app.ui.screens.removal.RemovalGuideScreen
import com.spywarescanner.app.ui.screens.reports.ReportsScreen
import com.spywarescanner.app.ui.screens.results.ScanResultsScreen
import com.spywarescanner.app.ui.screens.scanner.ScannerScreen
import com.spywarescanner.app.ui.screens.settings.SettingsScreen
import com.spywarescanner.app.ui.screens.threat.ThreatDetailsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.Scanner.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Scanner.route) {
            ScannerScreen(
                onNavigateToResults = { scanId ->
                    navController.navigate(Screen.ScanResults.createRoute(scanId))
                },
                onNavigateToPremium = {
                    navController.navigate(Screen.Premium.route)
                }
            )
        }

        composable(
            route = Screen.ScanResults.route,
            arguments = listOf(
                navArgument("scanId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val scanId = backStackEntry.arguments?.getLong("scanId") ?: 0
            ScanResultsScreen(
                scanId = scanId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToThreatDetails = { packageName ->
                    navController.navigate(Screen.ThreatDetails.createRoute(packageName))
                },
                onNavigateToRemovalGuide = { pkg ->
                    navController.navigate(Screen.RemovalGuide.createRoute(pkg))
                }
            )
        }

        composable(Screen.Alerts.route) {
            AlertsScreen(
                onNavigateToThreatDetails = { packageName ->
                    navController.navigate(Screen.ThreatDetails.createRoute(packageName))
                }
            )
        }

        composable(Screen.Reports.route) {
            ReportsScreen(
                onNavigateToPremium = {
                    navController.navigate(Screen.Premium.route)
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateToPremium = {
                    navController.navigate(Screen.Premium.route)
                }
            )
        }

        composable(Screen.Premium.route) {
            PremiumScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.RemovalGuide.route,
            arguments = listOf(
                navArgument("packageName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
            RemovalGuideScreen(
                packageName = packageName,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ThreatDetails.route,
            arguments = listOf(
                navArgument("packageName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
            ThreatDetailsScreen(
                packageName = packageName,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRemovalGuide = { pkg ->
                    navController.navigate(Screen.RemovalGuide.createRoute(pkg))
                }
            )
        }
    }
}
