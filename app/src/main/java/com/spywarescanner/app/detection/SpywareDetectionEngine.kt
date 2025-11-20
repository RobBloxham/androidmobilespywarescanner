package com.spywarescanner.app.detection

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.spywarescanner.app.data.model.ScannedApp
import com.spywarescanner.app.data.model.Threat
import com.spywarescanner.app.data.model.ThreatLevel
import com.spywarescanner.app.data.model.ThreatType
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

data class AppAnalysisResult(
    val scannedApp: ScannedApp,
    val threats: List<Threat>
)

@Singleton
class SpywareDetectionEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Whitelist of legitimate app packages that shouldn't be flagged
    private val trustedPackages = setOf(
        // Google apps
        "com.google.android", "com.android", "com.google",
        // Samsung
        "com.samsung", "com.sec.android",
        // System
        "android", "system",
        // Popular legitimate apps
        "com.whatsapp", "com.facebook", "com.instagram", "com.twitter",
        "com.spotify", "com.netflix", "com.amazon", "com.uber",
        "com.snapchat", "com.tiktok", "com.discord", "com.telegram",
        "com.microsoft", "com.apple", "org.mozilla", "com.opera"
    )

    // Known spyware package patterns (more specific)
    private val spywarePatterns = listOf(
        "spyware", "stalkerware", "keylogger", "spycam"
    )

    // Known malicious packages (simplified for demo)
    private val knownMaliciousPackages = setOf(
        "com.example.spyware",
        "com.tracker.hidden",
        "net.stalkerware.app"
    )

    // Dangerous permissions that indicate potential spyware
    private val dangerousPermissions = mapOf(
        "android.permission.READ_SMS" to "Can read your text messages",
        "android.permission.RECEIVE_SMS" to "Can intercept incoming messages",
        "android.permission.READ_CALL_LOG" to "Can access your call history",
        "android.permission.PROCESS_OUTGOING_CALLS" to "Can monitor outgoing calls",
        "android.permission.RECORD_AUDIO" to "Can record audio from microphone",
        "android.permission.CAMERA" to "Can access camera",
        "android.permission.ACCESS_FINE_LOCATION" to "Can track precise location",
        "android.permission.ACCESS_COARSE_LOCATION" to "Can track approximate location",
        "android.permission.READ_CONTACTS" to "Can read your contacts",
        "android.permission.READ_EXTERNAL_STORAGE" to "Can read files on device",
        "android.permission.WRITE_EXTERNAL_STORAGE" to "Can modify files on device",
        "android.permission.GET_ACCOUNTS" to "Can access account information",
        "android.permission.READ_CALENDAR" to "Can read calendar events",
        "android.permission.BODY_SENSORS" to "Can access body sensor data",
        "android.permission.SEND_SMS" to "Can send text messages",
        "android.permission.CALL_PHONE" to "Can make phone calls",
        "android.permission.SYSTEM_ALERT_WINDOW" to "Can display over other apps",
        "android.permission.BIND_ACCESSIBILITY_SERVICE" to "Can monitor screen content",
        "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE" to "Can read notifications",
        "android.permission.READ_PHONE_STATE" to "Can access phone identity"
    )

    // High-risk permission combinations that indicate spyware behavior
    private val spywarePermissionCombos = listOf(
        setOf("android.permission.READ_SMS", "android.permission.RECEIVE_SMS", "android.permission.SEND_SMS"),
        setOf("android.permission.RECORD_AUDIO", "android.permission.CAMERA"),
        setOf("android.permission.ACCESS_FINE_LOCATION", "android.permission.CAMERA", "android.permission.RECORD_AUDIO"),
        setOf("android.permission.READ_CALL_LOG", "android.permission.PROCESS_OUTGOING_CALLS", "android.permission.RECORD_AUDIO"),
        setOf("android.permission.BIND_ACCESSIBILITY_SERVICE", "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE")
    )

    fun analyzeApp(appInfo: ApplicationInfo, packageManager: PackageManager): AppAnalysisResult {
        val packageName = appInfo.packageName
        val appName = packageManager.getApplicationLabel(appInfo).toString()
        val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

        // Skip analysis for trusted packages
        if (trustedPackages.any { packageName.startsWith(it) }) {
            return createSafeResult(packageName, appName, packageManager, appInfo)
        }

        // Get package info with permissions
        val packageInfo = try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
        } catch (e: Exception) {
            null
        }

        val requestedPermissions = packageInfo?.requestedPermissions?.toList() ?: emptyList()
        val suspiciousPermissions = requestedPermissions.filter { it in dangerousPermissions.keys }

        // Calculate risk score and detect threats
        val threats = mutableListOf<Threat>()
        var riskScore = 0
        var redFlags = 0 // Count of serious issues

        // Check for known malicious packages
        if (packageName in knownMaliciousPackages) {
            riskScore += 100
            redFlags += 3
            threats.add(createThreat(packageName, appName, ThreatType.SPYWARE, ThreatLevel.CRITICAL,
                "This app is identified as known spyware"))
        }

        // Check package name for suspicious patterns (more specific)
        val matchedPatterns = spywarePatterns.filter {
            packageName.lowercase().contains(it) || appName.lowercase().contains(it)
        }
        if (matchedPatterns.isNotEmpty()) {
            riskScore += 40
            redFlags += 2
            threats.add(createThreat(packageName, appName, ThreatType.SPYWARE, ThreatLevel.CRITICAL,
                "App name contains spyware indicators: ${matchedPatterns.joinToString(", ")}"))
        }

        // Check for HIGH-RISK spyware permission combinations (all permissions must be present)
        for (combo in spywarePermissionCombos) {
            if (requestedPermissions.containsAll(combo)) {
                riskScore += 35
                redFlags += 1
                val comboDescription = combo.map { it.substringAfterLast(".") }.joinToString(", ")
                threats.add(createThreat(packageName, appName, ThreatType.TRACKING, ThreatLevel.HIGH,
                    "Dangerous permission combination: $comboDescription"))
                break // Only count one combo
            }
        }

        // Check if app is hidden from launcher (very suspicious if not a system app)
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent == null && !isSystemApp) {
            riskScore += 25
            redFlags += 1
            threats.add(createThreat(packageName, appName, ThreatType.HIDDEN_APP, ThreatLevel.HIGH,
                "App is hidden from launcher - potential spyware"))
        }

        // Check if app has accessibility service (only flag if combined with other suspicious factors)
        val hasAccessibilityService = "android.permission.BIND_ACCESSIBILITY_SERVICE" in requestedPermissions
        if (hasAccessibilityService && !isSystemApp && redFlags > 0) {
            riskScore += 20
            threats.add(createThreat(packageName, appName, ThreatType.KEYLOGGER, ThreatLevel.MEDIUM,
                "Has accessibility service with other suspicious factors"))
        }

        // Check for notification listener (only flag if not a legitimate messaging/notification app)
        val hasNotificationListener = "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE" in requestedPermissions
        if (hasNotificationListener && !isSystemApp && !isLikelyMessagingApp(packageName, appName)) {
            riskScore += 15
            threats.add(createThreat(packageName, appName, ThreatType.DATA_HARVESTER, ThreatLevel.MEDIUM,
                "Can read all notifications without clear justification"))
        }

        // Check install source for unknown sources (only minor concern)
        val installSource = try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                packageManager.getInstallSourceInfo(packageName).installingPackageName
            } else {
                @Suppress("DEPRECATION")
                packageManager.getInstallerPackageName(packageName)
            }
        } catch (e: Exception) {
            null
        }

        val isFromPlayStore = installSource?.contains("com.android.vending") == true
        if (installSource == null && !isSystemApp && redFlags > 0) {
            riskScore += 10
            threats.add(createThreat(packageName, appName, ThreatType.UNKNOWN_SOURCE, ThreatLevel.LOW,
                "Sideloaded app with suspicious characteristics"))
        }

        // Determine overall threat level
        val threatLevel = when {
            riskScore >= 80 -> ThreatLevel.CRITICAL
            riskScore >= 50 -> ThreatLevel.HIGH
            riskScore >= 25 -> ThreatLevel.MEDIUM
            riskScore >= 10 -> ThreatLevel.LOW
            else -> ThreatLevel.SAFE
        }

        // Determine threat types
        val threatTypes = threats.map { it.threatType }.distinct()

        // Get version info
        val versionName = packageInfo?.versionName ?: "Unknown"
        val versionCode = packageInfo?.longVersionCode ?: 0

        val scannedApp = ScannedApp(
            packageName = packageName,
            appName = appName,
            versionName = versionName,
            versionCode = versionCode,
            installTime = packageInfo?.firstInstallTime ?: 0,
            lastUpdateTime = packageInfo?.lastUpdateTime ?: 0,
            isSystemApp = isSystemApp,
            threatLevel = threatLevel,
            threatTypes = threatTypes.ifEmpty { listOf(ThreatType.SPYWARE).takeIf { threatLevel != ThreatLevel.SAFE } ?: emptyList() },
            suspiciousPermissions = suspiciousPermissions,
            riskScore = riskScore.coerceIn(0, 100),
            lastScanned = LocalDateTime.now()
        )

        return AppAnalysisResult(scannedApp, threats)
    }

    private fun createSafeResult(
        packageName: String,
        appName: String,
        packageManager: PackageManager,
        appInfo: ApplicationInfo
    ): AppAnalysisResult {
        val packageInfo = try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
        } catch (e: Exception) {
            null
        }

        val scannedApp = ScannedApp(
            packageName = packageName,
            appName = appName,
            versionName = packageInfo?.versionName ?: "Unknown",
            versionCode = packageInfo?.longVersionCode ?: 0,
            installTime = packageInfo?.firstInstallTime ?: 0,
            lastUpdateTime = packageInfo?.lastUpdateTime ?: 0,
            isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0,
            threatLevel = ThreatLevel.SAFE,
            threatTypes = emptyList(),
            suspiciousPermissions = emptyList(),
            riskScore = 0,
            lastScanned = LocalDateTime.now()
        )

        return AppAnalysisResult(scannedApp, emptyList())
    }

    private fun isLikelyMessagingApp(packageName: String, appName: String): Boolean {
        val messagingKeywords = listOf(
            "message", "sms", "chat", "mail", "email", "notification",
            "messenger", "telegram", "signal", "whatsapp"
        )

        return messagingKeywords.any {
            packageName.lowercase().contains(it) || appName.lowercase().contains(it)
        }
    }

    private fun createThreat(
        packageName: String,
        appName: String,
        type: ThreatType,
        level: ThreatLevel,
        description: String
    ): Threat {
        return Threat(
            packageName = packageName,
            appName = appName,
            threatLevel = level,
            threatType = type,
            description = description,
            detectedAt = LocalDateTime.now()
        )
    }

    fun generateRemovalSteps(packageName: String, appName: String): List<String> {
        return listOf(
            "Open your device Settings",
            "Navigate to Apps or Application Manager",
            "Find and tap on '$appName'",
            "Tap 'Uninstall' button",
            "Confirm the uninstallation when prompted",
            "Restart your device to ensure complete removal",
            "Run another scan to verify the threat is removed"
        )
    }

    fun getPermissionDescription(permission: String): String {
        return dangerousPermissions[permission] ?: "Unknown permission"
    }
}
