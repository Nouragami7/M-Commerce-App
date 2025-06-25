package com.example.buyva.data.repository.order

import com.example.buyva.admin.GetOrdersByCustomerEmailQuery
import com.example.buyva.data.datasource.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(private val remoteDataSource: RemoteDataSource) : IOrderRepository {
    override suspend fun getOrders(email: String): Flow<GetOrdersByCustomerEmailQuery.Data?> {
        return remoteDataSource.getOrders(email)
    }
}