package com.spywarescanner.app.ui.screens.removal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spywarescanner.app.data.model.ScannedApp
import com.spywarescanner.app.data.repository.ScanRepository
import com.spywarescanner.app.detection.SpywareDetectionEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemovalGuideViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val scanRepository: ScanRepository,
    private val detectionEngine: SpywareDetectionEngine
) : ViewModel() {

    private val packageName: String = savedStateHandle.get<String>("packageName") ?: ""

    data class RemovalGuideUiState(
        val app: ScannedApp? = null,
        val removalSteps: List<String> = emptyList(),
        val isLoading: Boolean = true
    )

    private val _uiState = MutableStateFlow(RemovalGuideUiState())
    val uiState: StateFlow<RemovalGuideUiState> = _uiState.asStateFlow()

    init {
        loadRemovalGuide()
    }

    private fun loadRemovalGuide() {
        viewModelScope.launch {
            val app = scanRepository.getAppByPackage(packageName)

            val steps = if (app != null) {
                detectionEngine.generateRemovalSteps(app.packageName, app.appName)
            } else {
                // Default steps if app not found
                listOf(
                    "Open your device Settings",
                    "Navigate to Apps or Application Manager",
                    "Find and tap on the suspicious app",
                    "Tap 'Uninstall' or 'Disable' button",
                    "Confirm the uninstallation when prompted",
                    "Clear any remaining data and cache",
                    "Restart your device to ensure complete removal",
                    "Run another scan to verify the threat is removed"
                )
            }

            _uiState.value = RemovalGuideUiState(
                app = app,
                removalSteps = steps,
                isLoading = false
            )
        }
    }
}
