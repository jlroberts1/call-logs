package com.contexts.calllog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CallLogUiState(
    val logs: List<CallLogEntry> = emptyList(),
    val error: String? = null,
    val isLoading: Boolean = false
)

class CallLogViewModel(
    private val repository: CallLogRepository,
): ViewModel() {

    private val _uiState = MutableStateFlow(CallLogUiState())
    val uiState = _uiState.asStateFlow()

    // this is here to check if we already fetched since we are checking
    // permissions on the same screen, permissions launcher gets triggered
    // again on rotations.
    private var hasInitialFetch = false

    fun fetchCallLogs() {
        if (hasInitialFetch) return
        hasInitialFetch = true
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                when (val result = repository.getCombinedCallLogs()) {
                    is ApiResult.Success -> {
                        _uiState.update { it.copy(logs = result.data, isLoading = false) }
                    }
                    is ApiResult.Error -> {
                        _uiState.update { it.copy(error = result.message, isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Failed to fetch call logs, ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    companion object {
        fun factory(repository: CallLogRepository) = viewModelFactory {
            initializer {
                CallLogViewModel(repository)
            }
        }
    }
}