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
        _orderState.value = ResponseState.Loading
        viewModelScope.launch {
            try {
                orderRepository.getOrders(email).collect { result ->
                    if (result != null) {
                        _orderState.value = ResponseState.Success(result)
                    } else {
                        _orderState.value = ResponseState.Failure(Exception("No orders found"))
                    }
                }
            } catch (e: Exception) {
                _orderState.value = ResponseState.Failure(e)
            }
        }
    }

}
