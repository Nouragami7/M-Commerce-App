package com.example.buyva.viewmodel.currency

import com.example.buyva.data.model.Currency
import com.example.buyva.data.model.CurrencyResponse
import com.example.buyva.data.model.Meta
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.currency.CurrencyRepo
import com.example.buyva.features.profile.currency.viewmodel.CurrencyViewModel
import com.example.buyva.utils.constants.CURRENCY_RATE
import com.example.buyva.utils.constants.CURRENCY_UNIT
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CurrencyViewModelTest {

    private lateinit var repository: CurrencyRepo
    private lateinit var viewModel: CurrencyViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)

        coEvery { repository.readCurrencyUnit(any()) } returns "USD"
        coEvery { repository.readCurrencyRate(any()) } returns 22.5

        coEvery { repository.getCurrencyRate("USD") } returns flowOf(
            ResponseState.Success(
                CurrencyResponse(
                    data = mapOf(
                        "EGP" to Currency(
                            code = "EGP",
                            value = 48.9
                        ),
                        "EUR" to Currency(
                            code = "EUR",
                            value = 0.92
                        )
                    ),
                    meta = Meta(last_updated_at = "2024-01-01T00:00:00Z")
                )
            )
        )

        viewModel = CurrencyViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads currency unit and rates`() = runTest {
        advanceUntilIdle()

        assertEquals("USD", viewModel.currencyResponse.value)
        assertEquals(
            mapOf("EGP" to 48.9, "EUR" to 0.92),
            viewModel.currencyRates.value
        )
    }

    @Test
    fun `getCurrencyUnit updates currencyResponse`() = runTest {
        coEvery { repository.readCurrencyUnit(any()) } returns "EUR"

        viewModel.getCurrencyUnit()
        assertEquals("EUR", viewModel.currencyResponse.value)
    }

    @Test
    fun `getCurrencyRate returns correct value`() = runTest {
        coEvery { repository.readCurrencyRate(any()) } returns 3.14

        val result = viewModel.getCurrencyRate()
        assertEquals(3.14, result, 0.001)
    }

    @Test
    fun `setCurrencyUnit updates repo and state`() = runTest {
        val unit = "GBP"
        viewModel.setCurrencyUnit(unit)
        advanceUntilIdle()

        coVerify { repository.writeCurrencyUnit(CURRENCY_UNIT, unit) }
        assertEquals("GBP", viewModel.currencyResponse.value)
    }

    @Test
    fun `setCurrencyRate updates repo and state`() = runTest {
        val rate = 9.9
        viewModel.setCurrencyRate(rate)
        advanceUntilIdle()

        coVerify { repository.writeCurrencyRate(CURRENCY_RATE, rate) }
        assertEquals(9.9, viewModel.currencyResponseRate.value)
    }

    @Test
    fun `fetchCurrencyRates updates currencyRates`() = runTest {
        viewModel.fetchCurrencyRates("USD")
        advanceUntilIdle()

        val expectedRates = mapOf("EGP" to 48.9, "EUR" to 0.92)
        assertEquals(expectedRates, viewModel.currencyRates.value)
    }

    @Test
    fun `fetchCurrencyRates handles failure response`() = runTest {
        coEvery { repository.getCurrencyRate("USD") } returns flowOf(
            ResponseState.Failure(Throwable("Network error"))
        )

        viewModel.fetchCurrencyRates("USD")
        advanceUntilIdle()

        assertTrue(viewModel.currencyRates.value.isEmpty())
    }
    @Test
    fun `fetchCurrencyRates handles Loading state`() = runTest {
        coEvery { repository.getCurrencyRate("USD") } returns flowOf(
            ResponseState.Loading
        )

        viewModel = CurrencyViewModel(repository)
        advanceUntilIdle()

        assertEquals(emptyMap(), viewModel.currencyRates.value)
    }

}