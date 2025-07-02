package com.example.buyva.viewmodel.cart


import com.example.buyva.admin.CompleteDraftOrderMutation
import com.example.buyva.admin.type.DraftOrderInput
import com.example.buyva.data.model.Address
import com.example.buyva.data.model.CartItem
import com.example.buyva.data.model.OrderItem
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.paymentRepo.PaymentRepo
import com.example.buyva.features.cart.cartList.viewmodel.PaymentViewModel
import com.google.gson.JsonObject
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import toDraftOrderInput

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentViewModelTest {

    private lateinit var paymentViewModel: PaymentViewModel
    private lateinit var repo: PaymentRepo
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repo = mockk(relaxed = true)
        paymentViewModel = PaymentViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initiatePaymentFlow success sets clientSecret`() = testScope.runTest {
        val json = JsonObject().apply {
            addProperty("client_secret", "secret_123")
        }
        val response = mockk<retrofit2.Response<JsonObject>>() {
            every { isSuccessful } returns true
            every { body() } returns json
        }
        coEvery { repo.createPaymentIntent(any(), any(), any()) } returns response

        var result: String? = null
        paymentViewModel.initiatePaymentFlow(1000) {
            result = it
        }
        advanceUntilIdle()

        assert(result == "secret_123")
    }

    @Test
    fun `initiatePaymentFlow missing secret sets error`() = testScope.runTest {
        val json = JsonObject() // No client_secret
        val response = mockk<retrofit2.Response<JsonObject>>() {
            every { isSuccessful } returns true
            every { body() } returns json
        }
        coEvery { repo.createPaymentIntent(any(), any(), any()) } returns response

        paymentViewModel.initiatePaymentFlow(1000) {}
        advanceUntilIdle()

        assert(paymentViewModel.error.value == "Missing client_secret in response")
    }

    @Test
    fun `initiatePaymentFlow http error sets error`() = testScope.runTest {
        val response = mockk<retrofit2.Response<JsonObject>>() {
            every { isSuccessful } returns false
            every { errorBody()?.string() } returns "API error"
        }
        coEvery { repo.createPaymentIntent(any(), any(), any()) } returns response

        paymentViewModel.initiatePaymentFlow(1000) {}
        advanceUntilIdle()

        assert(paymentViewModel.error.value?.contains("API error") == true)
    }

    @Test
    fun `initiatePaymentFlow exception sets error`() = testScope.runTest {
        coEvery { repo.createPaymentIntent(any(), any(), any()) } throws Exception("Network fail")

        paymentViewModel.initiatePaymentFlow(1000) {}
        advanceUntilIdle()

        assert(paymentViewModel.error.value?.contains("Network fail") == true)
    }

    @Test
    fun `createDraftOrder sets order state successfully`() = testScope.runTest {
        // Given: real OrderItem with fake data
        val item = OrderItem(
            cartItems = listOf(
                CartItem(
                    id = "1",
                    title = "Test Product",
                    variantId = "gid://shopify/ProductVariant/1",
                    quantity = 1,
                    price = 20.0,
                    imageUrl = "https://example.com/img.jpg",
                    quantityAvailable = 5,
                    selectedOptions = emptyList(),
                    lineId = "30"
                )
            ),
            email = "test@example.com",
            address = Address(
                id = "123",
                firstName = "Test",
                lastName = "User",
                address1 = "123 Street",
                address2 = "",
                country = "Testland",
                city = "Testville",
                phone = "1234567890"
            )
        )

        // When: repo returns success
        coEvery { repo.createDraftOrder(any()) } returns flowOf(
            ResponseState.Success("order123")
        )

        // Act
        paymentViewModel.createDraftOrder(item)
        advanceUntilIdle()

        // Assert
        val state = paymentViewModel.order.value
        assert(state is ResponseState.Success<*>)
        assertEquals("order123", (state as ResponseState.Success<*>).data)
    }


    @Test
    fun `createDraftOrder catches exception`() = testScope.runTest {
        val item = mockk<OrderItem>()
        val draftInput = mockk<DraftOrderInput>()

        every { item.toDraftOrderInput() } returns draftInput
        coEvery { repo.createDraftOrder(draftInput) } throws Exception("Draft error")

        paymentViewModel.createDraftOrder(item)
        advanceUntilIdle()

        assert(paymentViewModel.order.value is ResponseState.Failure)
    }
    @Test
    fun `completeDraftOrder sets success state`() = testScope.runTest {
        val mockResponse = mockk<CompleteDraftOrderMutation.Data>(relaxed = true)

        coEvery { repo.completeDraftOrder("order123") } returns flowOf(mockResponse)

        val viewModel = PaymentViewModel(repo)
        viewModel.completeDraftOrder("order123")
        advanceUntilIdle()

        val result = viewModel.completeOrderState.value
        assert(result is ResponseState.Success<*>)
        assertEquals("Order completed successfully!", (result as ResponseState.Success<*>).data)
    }




    @Test
    fun `completeDraftOrder catches exception`() = testScope.runTest {
        coEvery { repo.completeDraftOrder(any()) } throws Exception("Completion error")

        paymentViewModel.completeDraftOrder("orderId123")
        advanceUntilIdle()

        assert(paymentViewModel.completeOrderState.value is ResponseState.Failure)
    }
}
