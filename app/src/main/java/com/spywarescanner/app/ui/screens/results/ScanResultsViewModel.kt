package com.spywarescanner.app.ui.screens.results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spywarescanner.app.data.model.ScannedApp
import com.spywarescanner.app.data.repository.ScanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanResultsViewModel @Inject constructor(
    private val scanRepository: ScanRepository
) : ViewModel() {

    data class ResultsUiState(
        val securityScore: Int = 100,
        val totalApps: Int = 0,
        val threatsFound: Int = 0,
        val threateningApps: List<ScannedApp> = emptyList()
    )

    private val _uiState = MutableStateFlow(ResultsUiState())
    val uiState: StateFlow<ResultsUiState> = _uiState.asStateFlow()

    init {
        loadResults()
    }

    private fun loadResults() {
        viewModelScope.launch {
            val scanResult = scanRepository.getLatestScanResult()

            scanRepository.getThreateningApps().collect { apps ->
                _uiState.value = ResultsUiState(
                    securityScore = scanResult?.securityScore ?: 100,
                    totalApps = scanResult?.totalAppsScanned ?: 0,
                    threatsFound = scanResult?.threatsFound ?: 0,
                    threateningApps = apps
                )
            }
        }
    }
}
