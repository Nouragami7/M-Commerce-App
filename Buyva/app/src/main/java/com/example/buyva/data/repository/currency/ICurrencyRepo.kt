package com.example.buyva.data.repository.currency

import com.example.buyva.data.model.uistate.ResponseState
import kotlinx.coroutines.flow.Flow

interface ICurrencyRepo {
    suspend fun getCurrencyRate(requiredCurrency: String): Flow<ResponseState>

    suspend fun writeCurrencyRate(key: String, value: Double)

    suspend fun writeCurrencyUnit(key: String, value: String)

    suspend fun readCurrencyRate(key: String): Double

    suspend fun readCurrencyUnit(key: String): String

}
