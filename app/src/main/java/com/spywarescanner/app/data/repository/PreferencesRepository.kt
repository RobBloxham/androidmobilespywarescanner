package com.spywarescanner.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.spywarescanner.app.data.model.ScanFrequency
import com.spywarescanner.app.data.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object PreferencesKeys {
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
        val IS_PREMIUM_USER = booleanPreferencesKey("is_premium_user")
        val AUTO_SCAN_ENABLED = booleanPreferencesKey("auto_scan_enabled")
        val SCAN_FREQUENCY = stringPreferencesKey("scan_frequency")
        val REAL_TIME_PROTECTION = booleanPreferencesKey("real_time_protection")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val THREAT_NOTIFICATIONS = booleanPreferencesKey("threat_notifications")
        val REPORT_NOTIFICATIONS = booleanPreferencesKey("report_notifications")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val LAST_SCAN_TIME = longPreferencesKey("last_scan_time")
    }

    val userPreferences: Flow<UserPreferences> = dataStore.data.map { preferences ->
        UserPreferences(
            hasCompletedOnboarding = preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] ?: false,
            isPremiumUser = preferences[PreferencesKeys.IS_PREMIUM_USER] ?: false,
            autoScanEnabled = preferences[PreferencesKeys.AUTO_SCAN_ENABLED] ?: true,
            scanFrequency = preferences[PreferencesKeys.SCAN_FREQUENCY]?.let {
                ScanFrequency.valueOf(it)
            } ?: ScanFrequency.WEEKLY,
            realTimeProtectionEnabled = preferences[PreferencesKeys.REAL_TIME_PROTECTION] ?: false,
            notificationsEnabled = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true,
            threatNotificationsEnabled = preferences[PreferencesKeys.THREAT_NOTIFICATIONS] ?: true,
            reportNotificationsEnabled = preferences[PreferencesKeys.REPORT_NOTIFICATIONS] ?: true,
            darkModeEnabled = preferences[PreferencesKeys.DARK_MODE] ?: true,
            lastScanTime = preferences[PreferencesKeys.LAST_SCAN_TIME] ?: 0
        )
    }

    val hasCompletedOnboarding: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] ?: false
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] = completed
        }
    }

    suspend fun setPremiumUser(isPremium: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_PREMIUM_USER] = isPremium
        }
    }

    suspend fun setAutoScanEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_SCAN_ENABLED] = enabled
        }
    }

    suspend fun setScanFrequency(frequency: ScanFrequency) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SCAN_FREQUENCY] = frequency.name
        }
    }

    suspend fun setRealTimeProtection(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.REAL_TIME_PROTECTION] = enabled
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setThreatNotifications(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THREAT_NOTIFICATIONS] = enabled
        }
    }

    suspend fun setReportNotifications(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.REPORT_NOTIFICATIONS] = enabled
        }
    }

    suspend fun setLastScanTime(time: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_SCAN_TIME] = time
        }
    }
}
