package com.spywarescanner.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spywarescanner.app.data.model.UserPreferences
import com.spywarescanner.app.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = preferencesRepository.userPreferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    fun setAutoScanEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setAutoScanEnabled(enabled)
        }
    }

    fun setRealTimeProtection(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setRealTimeProtection(enabled)
        }
    }

    fun setThreatNotifications(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setThreatNotifications(enabled)
        }
    }

    fun setReportNotifications(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setReportNotifications(enabled)
        }
    }
}
