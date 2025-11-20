package com.spywarescanner.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

enum class ThreatLevel(val displayName: String, val priority: Int) {
    SAFE("Safe", 0),
    LOW("Low Risk", 1),
    MEDIUM("Medium Risk", 2),
    HIGH("High Risk", 3),
    CRITICAL("Critical Risk", 4)
}

enum class ThreatType(val displayName: String) {
    SPYWARE("Spyware"),
    TRACKING("Tracking App"),
    SUSPICIOUS_PERMISSIONS("Suspicious Permissions"),
    UNKNOWN_SOURCE("Unknown Source"),
    HIDDEN_APP("Hidden App"),
    DATA_HARVESTER("Data Harvester"),
    KEYLOGGER("Potential Keylogger"),
    STALKERWARE("Stalkerware"),
    ADWARE("Adware"),
    SYSTEM_MODIFIER("System Modifier")
}

@Entity(tableName = "scanned_apps")
data class ScannedApp(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val versionName: String,
    val versionCode: Long,
    val installTime: Long,
    val lastUpdateTime: Long,
    val isSystemApp: Boolean,
    val threatLevel: ThreatLevel,
    val threatTypes: List<ThreatType>,
    val suspiciousPermissions: List<String>,
    val riskScore: Int, // 0-100
    val lastScanned: LocalDateTime,
    val isIgnored: Boolean = false
)

@Entity(tableName = "threats")
data class Threat(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val threatLevel: ThreatLevel,
    val threatType: ThreatType,
    val description: String,
    val detectedAt: LocalDateTime,
    val isResolved: Boolean = false,
    val resolvedAt: LocalDateTime? = null
)

@Entity(tableName = "scan_results")
data class ScanResult(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val scanType: ScanType,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val totalAppsScanned: Int,
    val threatsFound: Int,
    val securityScore: Int
)

enum class ScanType(val displayName: String) {
    QUICK("Quick Scan"),
    DEEP("Deep Scan"),
    CUSTOM("Custom Scan"),
    SCHEDULED("Scheduled Scan"),
    REAL_TIME("Real-time Scan")
}

@Entity(tableName = "alerts")
data class Alert(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: AlertType,
    val title: String,
    val message: String,
    val packageName: String? = null,
    val timestamp: LocalDateTime,
    val isRead: Boolean = false,
    val priority: Int = 0
)

enum class AlertType(val displayName: String) {
    NEW_THREAT("New Threat"),
    PERMISSION_CHANGE("Permission Change"),
    NEW_INSTALL("New Installation"),
    SCAN_COMPLETE("Scan Complete"),
    WEEKLY_REPORT("Weekly Report"),
    SUSPICIOUS_ACTIVITY("Suspicious Activity")
}

data class RemovalStep(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false
)

data class RemovalGuide(
    val threatId: Long,
    val appName: String,
    val packageName: String,
    val steps: List<RemovalStep>,
    val additionalInfo: String? = null
)

data class SecurityReport(
    val period: ReportPeriod,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val totalScans: Int,
    val threatsDetected: Int,
    val threatsResolved: Int,
    val appsAnalyzed: Int,
    val averageSecurityScore: Int,
    val topThreats: List<ThreatType>,
    val recommendations: List<String>
)

enum class ReportPeriod {
    WEEKLY,
    MONTHLY
}

data class AppPermission(
    val name: String,
    val description: String,
    val isDangerous: Boolean,
    val isGranted: Boolean
)

data class ScanProgress(
    val currentApp: String,
    val currentIndex: Int,
    val totalApps: Int,
    val phase: ScanPhase
)

enum class ScanPhase(val displayName: String) {
    INITIALIZING("Initializing scan..."),
    SCANNING_APPS("Scanning installed apps..."),
    ANALYZING_PERMISSIONS("Analyzing permissions..."),
    AI_ANALYSIS("Running AI analysis..."),
    GENERATING_REPORT("Generating report..."),
    COMPLETE("Scan complete")
}
