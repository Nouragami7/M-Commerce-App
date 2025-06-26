package com.example.buyva.data.datasource.remote.currency

import com.example.buyva.data.model.CurrencyResponse
import com.example.buyva.data.model.uistate.ResponseState
import kotlinx.coroutines.flow.Flow

interface CurrencyRemoteDataSource {
    fun getCurrencyRate(requiredCurrency: String): Flow<ResponseState>

}