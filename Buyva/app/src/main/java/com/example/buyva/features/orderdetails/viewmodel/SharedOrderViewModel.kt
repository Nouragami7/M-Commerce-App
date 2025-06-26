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
    fun extractImageUrlsFromNote(note: String?): List<String> {
        if (note == null) return emptyList()
        return note.lines()
            .filter { it.contains("Image: ") }
            .mapNotNull {
                val parts = it.split("Image: ")
                if (parts.size == 2) parts[1].trim() else null
            }
    }

}
