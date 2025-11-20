package com.spywarescanner.app.ui.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spywarescanner.app.data.repository.ScanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val scanRepository: ScanRepository
) : ViewModel() {

    data class ReportsUiState(
        val weeklyScans: Int = 0,
        val weeklyThreats: Int = 0,
        val totalAppsChecked: Int = 0,
        val averageSecurityScore: Int = 100,
        val securityImprovement: Int = 0,
        val isLoading: Boolean = true
    )

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        loadReportData()
    }

    private fun loadReportData() {
        viewModelScope.launch {
            val allScans = scanRepository.getRecentScanResults(100).first()
            val now = LocalDateTime.now()
            val oneWeekAgo = now.minus(7, ChronoUnit.DAYS)

            // Filter scans from the last week
            val weeklyScans = allScans.filter { scan ->
                scan.endTime.isAfter(oneWeekAgo)
            }

            val weeklyThreats = weeklyScans.sumOf { it.threatsFound }
            val totalAppsChecked = weeklyScans.maxOfOrNull { it.totalAppsScanned } ?: 0

            // Calculate average security score
            val avgScore = if (weeklyScans.isNotEmpty()) {
                weeklyScans.map { it.securityScore }.average().toInt()
            } else {
                100
            }

            // Calculate security improvement (compare first and last scan of the week)
            val improvement = if (weeklyScans.size >= 2) {
                val firstScan = weeklyScans.last().securityScore
                val lastScan = weeklyScans.first().securityScore
                lastScan - firstScan
            } else {
                0
            }

            _uiState.value = ReportsUiState(
                weeklyScans = weeklyScans.size,
                weeklyThreats = weeklyThreats,
                totalAppsChecked = totalAppsChecked,
                averageSecurityScore = avgScore,
                securityImprovement = improvement,
                isLoading = false
            )
        }
    }
}
