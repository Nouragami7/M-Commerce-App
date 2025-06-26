package com.example.buyva.features.order.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.order.IOrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor (private val orderRepository: IOrderRepository) : ViewModel() {
    private val _orderState = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val orders: MutableStateFlow<ResponseState> = _orderState

    fun getOrders(email: String) {
        viewModelScope.launch {
            _orderState.value = ResponseState.Loading
            orderRepository.getOrders(email).collect { orders ->
                if (orders != null) {
                    _orderState.value = ResponseState.Success(orders)
                } else {
                    _orderState.value = ResponseState.Failure(Exception("Failed to fetch orders"))
                }
            }

        }
    }

}
