package com.spywarescanner.app.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.spywarescanner.app.data.local.ScanResultDao
import com.spywarescanner.app.data.local.ScannedAppDao
import com.spywarescanner.app.data.local.ThreatDao
import com.spywarescanner.app.data.model.ScanProgress
import com.spywarescanner.app.data.model.ScanPhase
import com.spywarescanner.app.data.model.ScanResult
import com.spywarescanner.app.data.model.ScanType
import com.spywarescanner.app.data.model.ScannedApp
import com.spywarescanner.app.data.model.Threat
import com.spywarescanner.app.data.model.ThreatLevel
import com.spywarescanner.app.detection.SpywareDetectionEngine
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scannedAppDao: ScannedAppDao,
    private val threatDao: ThreatDao,
    private val scanResultDao: ScanResultDao,
    private val detectionEngine: SpywareDetectionEngine
) {
    fun getAllScannedApps(): Flow<List<ScannedApp>> = scannedAppDao.getAllApps()

    fun getThreateningApps(): Flow<List<ScannedApp>> = scannedAppDao.getThreateningApps()

    fun getAllThreats(): Flow<List<Threat>> = threatDao.getAllThreats()

    fun getUnresolvedThreats(): Flow<List<Threat>> = threatDao.getUnresolvedThreats()

    fun getRecentScanResults(limit: Int = 10): Flow<List<ScanResult>> =
        scanResultDao.getRecentScanResults(limit)

    suspend fun getLatestScanResult(): ScanResult? = scanResultDao.getLatestScanResult()

    suspend fun getScanResultById(scanId: Long): ScanResult? =
        scanResultDao.getScanResultById(scanId)

    suspend fun getAppByPackage(packageName: String): ScannedApp? =
        scannedAppDao.getAppByPackage(packageName)

    suspend fun getThreatById(threatId: Long): Threat? = threatDao.getThreatById(threatId)

    fun getThreatsByPackage(packageName: String): Flow<List<Threat>> =
        threatDao.getThreatsByPackage(packageName)

    suspend fun resolveThreat(threatId: Long) {
        threatDao.resolveThreat(threatId, LocalDateTime.now().toString())
    }

    fun performScan(scanType: ScanType): Flow<ScanProgress> = flow {
        val startTime = LocalDateTime.now()
        val packageManager = context.packageManager

        emit(ScanProgress("", 0, 0, ScanPhase.INITIALIZING))
        delay(500)

        // Get installed apps
        val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val totalApps = installedApps.size

        val scannedApps = mutableListOf<ScannedApp>()
        val threats = mutableListOf<Threat>()

        emit(ScanProgress("", 0, totalApps, ScanPhase.SCANNING_APPS))

        installedApps.forEachIndexed { index, appInfo ->
            val appName = packageManager.getApplicationLabel(appInfo).toString()

            emit(ScanProgress(appName, index + 1, totalApps, ScanPhase.SCANNING_APPS))

            // Analyze the app
            val analysisResult = detectionEngine.analyzeApp(appInfo, packageManager)

            scannedApps.add(analysisResult.scannedApp)

            if (analysisResult.threats.isNotEmpty()) {
                threats.addAll(analysisResult.threats)
            }

            // Add small delay for visual feedback
            if (scanType == ScanType.DEEP) {
                delay(50)
            } else {
                delay(20)
            }
        }

        emit(ScanProgress("", totalApps, totalApps, ScanPhase.AI_ANALYSIS))
        delay(1000)

        emit(ScanProgress("", totalApps, totalApps, ScanPhase.GENERATING_REPORT))

        // Save results to database
        scannedAppDao.deleteAllApps()
        scannedAppDao.insertApps(scannedApps)

        threats.forEach { threat ->
            threatDao.insertThreat(threat)
        }

        // Calculate security score
        val securityScore = calculateSecurityScore(scannedApps)

        // Save scan result
        val scanResult = ScanResult(
            scanType = scanType,
            startTime = startTime,
            endTime = LocalDateTime.now(),
            totalAppsScanned = totalApps,
            threatsFound = threats.size,
            securityScore = securityScore
        )
        scanResultDao.insertScanResult(scanResult)

        delay(500)
        emit(ScanProgress("", totalApps, totalApps, ScanPhase.COMPLETE))
    }

    private fun calculateSecurityScore(apps: List<ScannedApp>): Int {
        if (apps.isEmpty()) return 100

        val totalRisk: Int = apps.sumOf { app ->
            when (app.threatLevel) {
                ThreatLevel.CRITICAL -> 25
                ThreatLevel.HIGH -> 15
                ThreatLevel.MEDIUM -> 8
                ThreatLevel.LOW -> 3
                ThreatLevel.SAFE -> 0
            }
        }

        val maxPossibleRisk = apps.size * 25
        val riskPercentage = (totalRisk.toFloat() / maxPossibleRisk) * 100

        return (100f - riskPercentage).toInt().coerceIn(0, 100)
    }
}
