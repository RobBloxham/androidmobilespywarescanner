package com.spywarescanner.app.ui.screens.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spywarescanner.app.data.model.AlertType
import com.spywarescanner.app.data.model.ScanPhase
import com.spywarescanner.app.data.model.ScanType
import com.spywarescanner.app.data.repository.AlertRepository
import com.spywarescanner.app.data.repository.PreferencesRepository
import com.spywarescanner.app.data.repository.ScanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val scanRepository: ScanRepository,
    private val preferencesRepository: PreferencesRepository,
    private val alertRepository: AlertRepository
) : ViewModel() {

    data class ScannerUiState(
        val isScanning: Boolean = false,
        val securityScore: Int = 100,
        val lastScanTime: Long = 0,
        val totalAppsScanned: Int = 0,
        val lastThreatsFound: Int = 0,
        val lastScanId: Long? = null,
        val shouldNavigateToResults: Boolean = false
    )

    data class ScanProgressState(
        val currentApp: String = "",
        val currentIndex: Int = 0,
        val totalApps: Int = 0,
        val phase: ScanPhase = ScanPhase.INITIALIZING
    )

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    private val _scanProgress = MutableStateFlow(ScanProgressState())
    val scanProgress: StateFlow<ScanProgressState> = _scanProgress.asStateFlow()

    private var scanJob: Job? = null

    init {
        loadLastScanInfo()
    }

    private fun loadLastScanInfo() {
        viewModelScope.launch {
            val lastScan = scanRepository.getLatestScanResult()
            val preferences = preferencesRepository.userPreferences.first()

            _uiState.value = _uiState.value.copy(
                securityScore = lastScan?.securityScore ?: 100,
                lastScanTime = preferences.lastScanTime,
                totalAppsScanned = lastScan?.totalAppsScanned ?: 0,
                lastThreatsFound = lastScan?.threatsFound ?: 0
            )
        }
    }

    fun startScan(scanType: ScanType) {
        if (_uiState.value.isScanning) return

        scanJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isScanning = true,
                shouldNavigateToResults = false
            )

            scanRepository.performScan(scanType).collect { progress ->
                _scanProgress.value = ScanProgressState(
                    currentApp = progress.currentApp,
                    currentIndex = progress.currentIndex,
                    totalApps = progress.totalApps,
                    phase = progress.phase
                )

                if (progress.phase == ScanPhase.COMPLETE) {
                    val result = scanRepository.getLatestScanResult()
                    val currentTime = System.currentTimeMillis()

                    preferencesRepository.setLastScanTime(currentTime)

                    _uiState.value = _uiState.value.copy(
                        isScanning = false,
                        securityScore = result?.securityScore ?: 100,
                        lastScanTime = currentTime,
                        totalAppsScanned = result?.totalAppsScanned ?: 0,
                        lastThreatsFound = result?.threatsFound ?: 0,
                        lastScanId = result?.id,
                        shouldNavigateToResults = true
                    )

                    // Create alert for scan completion
                    if (result != null && result.threatsFound > 0) {
                        alertRepository.createAlert(
                            type = AlertType.SCAN_COMPLETE,
                            title = "Scan Complete - Threats Found",
                            message = "${result.threatsFound} potential threats detected. Tap to view details.",
                            priority = 1
                        )
                    }
                }
            }
        }
    }

    fun stopScan() {
        scanJob?.cancel()
        _uiState.value = _uiState.value.copy(isScanning = false)
        _scanProgress.value = ScanProgressState()
    }

    fun onNavigatedToResults() {
        _uiState.value = _uiState.value.copy(shouldNavigateToResults = false)
    }
}
