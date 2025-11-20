package com.spywarescanner.app.data.repository

import com.spywarescanner.app.data.local.AlertDao
import com.spywarescanner.app.data.model.Alert
import com.spywarescanner.app.data.model.AlertType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlertRepository @Inject constructor(
    private val alertDao: AlertDao
) {
    fun getAllAlerts(): Flow<List<Alert>> = alertDao.getAllAlerts()

    fun getUnreadAlerts(): Flow<List<Alert>> = alertDao.getUnreadAlerts()

    fun getUnreadAlertsCount(): Flow<Int> = alertDao.getUnreadAlertsCount()

    suspend fun createAlert(
        type: AlertType,
        title: String,
        message: String,
        packageName: String? = null,
        priority: Int = 0
    ): Long {
        val alert = Alert(
            type = type,
            title = title,
            message = message,
            packageName = packageName,
            timestamp = LocalDateTime.now(),
            priority = priority
        )
        return alertDao.insertAlert(alert)
    }

    suspend fun markAsRead(alertId: Long) {
        alertDao.markAsRead(alertId)
    }

    suspend fun markAllAsRead() {
        alertDao.markAllAsRead()
    }

    suspend fun deleteAlert(alert: Alert) {
        alertDao.deleteAlert(alert)
    }

    suspend fun deleteAllAlerts() {
        alertDao.deleteAllAlerts()
    }
}
