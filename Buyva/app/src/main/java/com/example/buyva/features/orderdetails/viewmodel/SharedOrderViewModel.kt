package com.example.buyva.features.orderdetails.viewmodel


import androidx.lifecycle.ViewModel
import com.example.buyva.admin.GetOrdersByCustomerEmailQuery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedOrderViewModel : ViewModel() {
    private val _selectedOrder = MutableStateFlow<GetOrdersByCustomerEmailQuery.Node?>(null)
    val selectedOrder: StateFlow<GetOrdersByCustomerEmailQuery.Node?> = _selectedOrder

    fun setOrder(order: GetOrdersByCustomerEmailQuery.Node?) {
        _selectedOrder.value = order
    }
}
