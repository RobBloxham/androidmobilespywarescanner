package com.spywarescanner.app.ui.screens.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spywarescanner.app.data.model.Alert
import com.spywarescanner.app.data.repository.AlertRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val alertRepository: AlertRepository
) : ViewModel() {

    val alerts: StateFlow<List<Alert>> = alertRepository.getAllAlerts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun markAsRead(alertId: Long) {
        viewModelScope.launch {
            alertRepository.markAsRead(alertId)
        }
    }

    fun clearAllAlerts() {
        viewModelScope.launch {
            alertRepository.deleteAllAlerts()
        }
    }
}
