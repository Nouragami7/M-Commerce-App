package com.example.buyva.viewmodel.order

import com.example.buyva.admin.GetOrdersByCustomerEmailQuery
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.order.IOrderRepository
import com.example.buyva.features.order.viewmodel.OrderViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrderViewModelTest {
    private lateinit var orderRepository: IOrderRepository
    private lateinit var viewModel: OrderViewModel

    @Before
    fun setUp() {
        orderRepository = mockk()
        viewModel = OrderViewModel(orderRepository)
    }

    @Test
    fun getOrders_whenRepositoryReturnsData_getOrdersSuccess() = runTest {
        val email = "test@example.com"

        val mockNode = mockk<GetOrdersByCustomerEmailQuery.Node>(relaxed = true)

        val mockEdge = mockk<GetOrdersByCustomerEmailQuery.Edge> {
            every { node } returns mockNode
        }

        val mockOrders = mockk<GetOrdersByCustomerEmailQuery.Orders> {
            every { edges } returns listOf(mockEdge)
        }

        val mockData = mockk<GetOrdersByCustomerEmailQuery.Data> {
            every { orders } returns mockOrders
        }

        coEvery { orderRepository.getOrders(email) } returns flowOf(mockData)

        viewModel.getOrders(email)
        advanceUntilIdle()

        val state = viewModel.orders.value
        assert(state is ResponseState.Success<*>)

        val successState = state as ResponseState.Success<*>
        val data = successState.data as? GetOrdersByCustomerEmailQuery.Data
        assertNotNull(data)
        assertEquals(1, data!!.orders.edges.size)
    }
}
