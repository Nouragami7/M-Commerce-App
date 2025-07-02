package com.example.buyva.repository.paymentRepo

import com.example.buyva.admin.CompleteDraftOrderMutation
import com.example.buyva.admin.type.DraftOrderInput
import com.example.buyva.data.datasource.remote.RemoteDataSource
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.paymentRepo.PaymentRepo
import com.example.buyva.data.repository.paymentRepo.PaymentRepoImpl
import com.google.gson.JsonObject
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertFalse
import retrofit2.Response

import org.junit.Assert.assertTrue

class PaymentRepoImplTest {

    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var paymentRepo: PaymentRepo

    @Before
    fun setUp() {
        remoteDataSource = mockk()
        paymentRepo = PaymentRepoImpl(remoteDataSource)
    }

    @Test
    fun createPaymentIntent_returnsExpectedResponse() = runTest {
        val amount = 1000
        val currency = "usd"
        val paymentMethod = "card"

        val expectedJson = JsonObject()
        expectedJson.addProperty("client_secret", "secret_123")
        val expectedResponse = Response.success(expectedJson)

        coEvery { remoteDataSource.createPaymentIntent(amount, currency) } returns expectedResponse

        val result = paymentRepo.createPaymentIntent(amount, currency, paymentMethod)

        assertEquals(expectedResponse, result)
        assertEquals("secret_123", result.body()?.get("client_secret")?.asString)
    }

    @Test
    fun create_payment_intent_returns_error_response() = runTest {
        val amount = 1000
        val currency = "usd"
        val paymentMethod = "card"

        val errorResponse = Response.error<JsonObject>(
            400,
            "{\"error\": \"Invalid request\"}".toResponseBody("application/json".toMediaTypeOrNull())
        )

        coEvery { remoteDataSource.createPaymentIntent(amount, currency) } returns errorResponse

        val result = paymentRepo.createPaymentIntent(amount, currency, paymentMethod)

        assertFalse(result.isSuccessful)
        assertEquals(400, result.code())
    }


    @Test
    fun createDraftOrder_returnsExpectedFlow() = runTest {
        val draftInput = DraftOrderInput()
        val expectedResponse = ResponseState.Success("Draft Order Created")

        coEvery { remoteDataSource.createDraftOrder(draftInput) } returns flowOf(expectedResponse)

        val result = paymentRepo.createDraftOrder(draftInput).toList()

        assertEquals(listOf(expectedResponse), result)
    }

    @Test
    fun completeDraftOrder_returnsExpectedData() = runTest {
        val draftOrderId = "gid://shopify/DraftOrder/123"
        val mockData = mockk<CompleteDraftOrderMutation.Data>()

        coEvery { remoteDataSource.completeDraftOrder(draftOrderId) } returns flowOf(mockData)

        val result = paymentRepo.completeDraftOrder(draftOrderId).toList()

        assertEquals(listOf(mockData), result)
    }

    @Test
    fun createDraftOrder_returnsFailure() = runTest {
        val draftInput = DraftOrderInput()
        val error = ResponseState.Failure(Throwable("Error"))

        coEvery { remoteDataSource.createDraftOrder(draftInput) } returns flowOf(error)

        val result = paymentRepo.createDraftOrder(draftInput).toList()

        assertEquals(listOf(error), result)
    }

    @Test
    fun completeDraftOrder_returnsFailure() = runTest {
        val draftOrderId = "gid://shopify/DraftOrder/456"
        val error = mockk<Throwable>()
        val failure = mockk<CompleteDraftOrderMutation.Data>()

        coEvery { remoteDataSource.completeDraftOrder(draftOrderId) } returns flowOf(failure)

        val result = paymentRepo.completeDraftOrder(draftOrderId).toList()

        assertEquals(listOf(failure), result)
    }
}
