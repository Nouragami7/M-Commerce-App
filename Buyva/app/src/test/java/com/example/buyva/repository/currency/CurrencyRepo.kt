package com.example.buyva.repository.currency

import com.example.buyva.data.datasource.remote.currency.CurrencyRemoteDataSource
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.currency.CurrencyRepo
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CurrencyRepoTest {

    private lateinit var currencyRemoteDataSource: CurrencyRemoteDataSource
    private lateinit var currencyRepo: CurrencyRepo

    @Before
    fun setup() {
        currencyRemoteDataSource = mockk()
        currencyRepo = CurrencyRepo(currencyRemoteDataSource)
    }

    @Test
    fun get_currency_rate_returns_expected_flow() = runTest {
        val currency = "USD"
        val expected = ResponseState.Success(27.5)

        coEvery { currencyRemoteDataSource.getCurrencyRate(currency) } returns flowOf(expected)

        val result = currencyRepo.getCurrencyRate(currency).toList()

        assertEquals(listOf(expected), result)
    }

    @Test
    fun write_currency_rate_saves_successfully() = runTest {
        val key = "rate_key"
        val value = 27.5

        mockkObject(SharedPreferenceImpl)
        coEvery { SharedPreferenceImpl.saveLongToSharedPreferenceInGeneral(key, value) } just Runs

        currencyRepo.writeCurrencyRate(key, value)

        coVerify { SharedPreferenceImpl.saveLongToSharedPreferenceInGeneral(key, value) }
    }

    @Test
    fun write_currency_unit_saves_successfully() = runTest {
        val key = "unit_key"
        val value = "USD"

        mockkObject(SharedPreferenceImpl)
        coEvery { SharedPreferenceImpl.saveToSharedPreferenceInGeneral(key, value) } just Runs

        currencyRepo.writeCurrencyUnit(key, value)

        coVerify { SharedPreferenceImpl.saveToSharedPreferenceInGeneral(key, value) }
    }

    @Test
    fun read_currency_rate_returns_expected_value() = runTest {
        val key = "rate_key"
        val expected = 27.5

        mockkObject(SharedPreferenceImpl)
        coEvery { SharedPreferenceImpl.getLongFromSharedPreferenceInGeneral(key) } returns expected

        val result = currencyRepo.readCurrencyRate(key)

        assertEquals(expected, result, 0.0001)    }

    @Test
    fun read_currency_unit_returns_expected_string() = runTest {
        val key = "unit_key"
        val expected = "USD"

        mockkObject(SharedPreferenceImpl)
        coEvery { SharedPreferenceImpl.getFromSharedPreferenceInGeneral(key) } returns expected

        val result = currencyRepo.readCurrencyUnit(key)

        assertEquals(expected, result)
    }

    @Test
    fun read_currency_unit_returns_empty_string_when_null() = runTest {
        val key = "unit_key"

        mockkObject(SharedPreferenceImpl)
        coEvery { SharedPreferenceImpl.getFromSharedPreferenceInGeneral(key) } returns null

        val result = currencyRepo.readCurrencyUnit(key)

        assertEquals("", result)
    }
}
