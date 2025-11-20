package com.spywarescanner.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.spywarescanner.app.data.model.Alert
import com.spywarescanner.app.data.model.ScanResult
import com.spywarescanner.app.data.model.ScannedApp
import com.spywarescanner.app.data.model.Threat
import com.spywarescanner.app.data.model.ThreatLevel
import kotlinx.coroutines.flow.Flow

@Dao
interface ScannedAppDao {
    @Query("SELECT * FROM scanned_apps ORDER BY riskScore DESC")
    fun getAllApps(): Flow<List<ScannedApp>>

    @Query("SELECT * FROM scanned_apps WHERE threatLevel != 'SAFE' ORDER BY riskScore DESC")
    fun getThreateningApps(): Flow<List<ScannedApp>>

    @Query("SELECT * FROM scanned_apps WHERE packageName = :packageName")
    suspend fun getAppByPackage(packageName: String): ScannedApp?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: ScannedApp)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApps(apps: List<ScannedApp>)

    @Update
    suspend fun updateApp(app: ScannedApp)

    @Delete
    suspend fun deleteApp(app: ScannedApp)

    @Query("DELETE FROM scanned_apps")
    suspend fun deleteAllApps()

    @Query("SELECT COUNT(*) FROM scanned_apps WHERE threatLevel = :level")
    suspend fun countAppsByThreatLevel(level: ThreatLevel): Int
}

@Dao
interface ThreatDao {
    @Query("SELECT * FROM threats ORDER BY detectedAt DESC")
    fun getAllThreats(): Flow<List<Threat>>

    @Query("SELECT * FROM threats WHERE isResolved = 0 ORDER BY detectedAt DESC")
    fun getUnresolvedThreats(): Flow<List<Threat>>

    @Query("SELECT * FROM threats WHERE packageName = :packageName")
    fun getThreatsByPackage(packageName: String): Flow<List<Threat>>

    @Query("SELECT * FROM threats WHERE id = :threatId")
    suspend fun getThreatById(threatId: Long): Threat?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThreat(threat: Threat): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThreats(threats: List<Threat>)

    @Update
    suspend fun updateThreat(threat: Threat)

    @Query("UPDATE threats SET isResolved = 1, resolvedAt = :resolvedAt WHERE id = :threatId")
    suspend fun resolveThreat(threatId: Long, resolvedAt: String)

    @Delete
    suspend fun deleteThreat(threat: Threat)

    @Query("SELECT COUNT(*) FROM threats WHERE isResolved = 0")
    fun getUnresolvedThreatCount(): Flow<Int>
}

@Dao
interface ScanResultDao {
    @Query("SELECT * FROM scan_results ORDER BY startTime DESC")
    fun getAllScanResults(): Flow<List<ScanResult>>

    @Query("SELECT * FROM scan_results ORDER BY startTime DESC LIMIT 1")
    suspend fun getLatestScanResult(): ScanResult?

    @Query("SELECT * FROM scan_results WHERE id = :scanId")
    suspend fun getScanResultById(scanId: Long): ScanResult?

    @Query("SELECT * FROM scan_results ORDER BY startTime DESC LIMIT :limit")
    fun getRecentScanResults(limit: Int): Flow<List<ScanResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScanResult(result: ScanResult): Long

    @Delete
    suspend fun deleteScanResult(result: ScanResult)

    @Query("SELECT AVG(securityScore) FROM scan_results")
    suspend fun getAverageSecurityScore(): Float?

    @Query("SELECT SUM(threatsFound) FROM scan_results")
    suspend fun getTotalThreatsFound(): Int?
}

@Dao
interface AlertDao {
    @Query("SELECT * FROM alerts ORDER BY timestamp DESC")
    fun getAllAlerts(): Flow<List<Alert>>

    @Query("SELECT * FROM alerts WHERE isRead = 0 ORDER BY timestamp DESC")
    fun getUnreadAlerts(): Flow<List<Alert>>

    @Query("SELECT COUNT(*) FROM alerts WHERE isRead = 0")
    fun getUnreadAlertsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: Alert): Long

    @Update
    suspend fun updateAlert(alert: Alert)

    @Query("UPDATE alerts SET isRead = 1 WHERE id = :alertId")
    suspend fun markAsRead(alertId: Long)

    @Query("UPDATE alerts SET isRead = 1")
    suspend fun markAllAsRead()

    @Delete
    suspend fun deleteAlert(alert: Alert)

    @Query("DELETE FROM alerts")
    suspend fun deleteAllAlerts()
}
