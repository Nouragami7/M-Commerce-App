package com.example.buyva.viewmodel.order

import com.example.buyva.admin.GetOrdersByCustomerEmailQuery
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.order.IOrderRepository
import com.example.buyva.features.order.viewmodel.OrderViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrderViewModelTest {

    private lateinit var orderRepository: IOrderRepository
    private lateinit var viewModel: OrderViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        orderRepository = mockk()
        viewModel = OrderViewModel(orderRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getOrders_whenRepositoryReturnsData_getOrdersSuccess() = runTest {
        val email = "test@gmail.com"

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
        val success = state as ResponseState.Success<*>
        val returnedData = success.data as GetOrdersByCustomerEmailQuery.Data
        assertEquals(1, returnedData.orders.edges.size)
    }

    @Test
    fun getOrders_whenRepositoryReturnsNull_getFailureState() = runTest(testDispatcher) {
        val email = "test@gmail.com"

        coEvery { orderRepository.getOrders(email) } returns flowOf(null)

        viewModel.getOrders(email)
        advanceUntilIdle()

        val state = viewModel.orders.value
        assert(state is ResponseState.Failure)
        val failure = state as ResponseState.Failure
        assertEquals("No orders found", failure.message.message)
    }



}
