package com.spywarescanner.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SpywareScannerApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            // Threat Alerts Channel
            val threatChannel = NotificationChannel(
                CHANNEL_THREATS,
                "Threat Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for detected threats and security issues"
                enableVibration(true)
                enableLights(true)
            }

            // Scan Progress Channel
            val scanChannel = NotificationChannel(
                CHANNEL_SCAN,
                "Scan Progress",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for ongoing security scans"
            }

            // Weekly Reports Channel
            val reportsChannel = NotificationChannel(
                CHANNEL_REPORTS,
                "Security Reports",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Weekly and monthly security report notifications"
            }

            notificationManager.createNotificationChannels(
                listOf(threatChannel, scanChannel, reportsChannel)
            )
        }
    }

    companion object {
        const val CHANNEL_THREATS = "threats_channel"
        const val CHANNEL_SCAN = "scan_channel"
        const val CHANNEL_REPORTS = "reports_channel"
    }
}
