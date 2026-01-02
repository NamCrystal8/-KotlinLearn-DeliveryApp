package com.example.deliveryapp.ui.courier

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deliveryapp.data.local.TokenManager
import com.example.deliveryapp.data.remote.dto.OrderResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CourierUiState(
    val isLoading: Boolean = false,
    val availableOrders: List<OrderResponse> = emptyList(),
    val activeOrder: OrderResponse? = null,
    val error: String? = null
)

@HiltViewModel
class CourierViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CourierUiState())
    val uiState: StateFlow<CourierUiState> = _uiState.asStateFlow()

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearAll()
        }
    }
}
