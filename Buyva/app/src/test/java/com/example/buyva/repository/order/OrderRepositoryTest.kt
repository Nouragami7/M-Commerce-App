package com.example.buyva.repository.order

import com.example.buyva.admin.GetOrdersByCustomerEmailQuery
import com.example.buyva.data.datasource.remote.RemoteDataSource
import com.example.buyva.data.repository.order.IOrderRepository
import com.example.buyva.data.repository.order.OrderRepositoryImpl
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class OrderRepositoryTest {

    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var orderRepository: IOrderRepository


    @Before
    fun setUp() {
        remoteDataSource = mockk()
        orderRepository = OrderRepositoryImpl(remoteDataSource)

    }

    //Test success
    @Test
    fun getOrders_whenRemoteDataSourceReturnsData_returnsOrdersSuccessfully() = runTest {
        val expectedOrders = mockk<GetOrdersByCustomerEmailQuery.Data>()
        val email = "test@gmail.com"

        coEvery { remoteDataSource.getOrders(email) } returns flowOf(expectedOrders)

        val result = orderRepository.getOrders(email)

        result.collect { orders ->
            assertEquals(expectedOrders, orders)
        }

    }


    //Test failure
    @Test
    fun getOrders_whenRemoteCallFails_emitsNull() = runTest {
        val email = "test@example.com"

        coEvery {
            remoteDataSource.getOrders(email)
        } returns flowOf(null)

        val result = orderRepository.getOrders(email)

        result.collect { data ->
            assertEquals(null, data)
        }
    }


}