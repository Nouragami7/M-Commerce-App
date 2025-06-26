package com.example.buyva.data.repository.order

import com.example.buyva.admin.GetOrdersByCustomerEmailQuery
import kotlinx.coroutines.flow.Flow

interface IOrderRepository {
    suspend fun getOrders(email: String): Flow<GetOrdersByCustomerEmailQuery.Data?>
}