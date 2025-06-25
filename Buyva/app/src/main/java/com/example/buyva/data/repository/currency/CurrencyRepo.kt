package com.example.buyva.data.repository.currency

import com.example.buyva.data.datasource.remote.currency.CurrencyRemoteDataSource
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CurrencyRepo @Inject constructor(
    private val currencyRemoteDataSource: CurrencyRemoteDataSource,
) : ICurrencyRepo {
    override suspend fun getCurrencyRate(requiredCurrency: String): Flow<ResponseState> {
        return currencyRemoteDataSource.getCurrencyRate(requiredCurrency)
    }

    override suspend fun writeCurrencyRate(key: String, value: Double) {
        SharedPreferenceImpl.saveLongToSharedPreferenceInGeneral(key, value)
    }

    override suspend fun writeCurrencyUnit(key: String, value: String) {
        SharedPreferenceImpl.saveToSharedPreferenceInGeneral(key, value)
    }

    override suspend fun readCurrencyRate(key: String): Double {
        return SharedPreferenceImpl.getLongFromSharedPreferenceInGeneral(key)
    }

    override suspend fun readCurrencyUnit(key: String): String{
        return SharedPreferenceImpl.getFromSharedPreferenceInGeneral(key) ?: ""
    }


}