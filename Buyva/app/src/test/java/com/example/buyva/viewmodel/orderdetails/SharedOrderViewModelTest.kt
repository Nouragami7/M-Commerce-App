package com.example.buyva.viewmodel.orderdetails

import com.example.buyva.admin.GetOrdersByCustomerEmailQuery
import com.example.buyva.features.orderdetails.viewmodel.SharedOrderViewModel
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Before
import org.junit.Test

class SharedOrderViewModelTest {
    private lateinit var viewModel: SharedOrderViewModel

    @Before
    fun setUp() {
        viewModel = SharedOrderViewModel()
    }

    @Test
    fun setOrder_shouldUpdate_selectedOrder() {
        val mockNode = mockk<GetOrdersByCustomerEmailQuery.Node>(relaxed = true)

        viewModel.setOrder(mockNode)

        assertEquals(mockNode, viewModel.selectedOrder.value)
    }

    @Test
    fun setOrder_shouldSetNull_whenOrderIsNull() {
        viewModel.setOrder(null)

        assertNull(viewModel.selectedOrder.value)
    }
}