package com.spywarescanner.app.data.model

data class UserPreferences(
    val hasCompletedOnboarding: Boolean = false,
    val isPremiumUser: Boolean = false,
    val autoScanEnabled: Boolean = true,
    val scanFrequency: ScanFrequency = ScanFrequency.WEEKLY,
    val realTimeProtectionEnabled: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val threatNotificationsEnabled: Boolean = true,
    val reportNotificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = true,
    val lastScanTime: Long = 0
)

enum class ScanFrequency(val displayName: String, val intervalHours: Int) {
    DAILY("Daily", 24),
    WEEKLY("Weekly", 168),
    BIWEEKLY("Every 2 weeks", 336),
    MONTHLY("Monthly", 720)
}
