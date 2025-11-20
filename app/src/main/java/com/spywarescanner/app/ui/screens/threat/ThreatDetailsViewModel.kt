package com.spywarescanner.app.ui.screens.threat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spywarescanner.app.data.model.ScannedApp
import com.spywarescanner.app.data.model.Threat
import com.spywarescanner.app.data.repository.ScanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThreatDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val scanRepository: ScanRepository
) : ViewModel() {

    private val packageName: String = savedStateHandle.get<String>("packageName") ?: ""

    private val _app = MutableStateFlow<ScannedApp?>(null)
    val app: StateFlow<ScannedApp?> = _app.asStateFlow()

    private val _threats = MutableStateFlow<List<Threat>>(emptyList())
    val threats: StateFlow<List<Threat>> = _threats.asStateFlow()

    init {
        loadAppDetails()
    }

    private fun loadAppDetails() {
        viewModelScope.launch {
            _app.value = scanRepository.getAppByPackage(packageName)

            scanRepository.getThreatsByPackage(packageName).collect { threatList ->
                _threats.value = threatList
            }
        }
    }
}
