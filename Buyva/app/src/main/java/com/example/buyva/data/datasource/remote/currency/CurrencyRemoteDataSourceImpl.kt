package com.omarinc.shopify.network.currency

import com.example.buyva.data.datasource.remote.currency.CurrencyApiService
import com.example.buyva.data.datasource.remote.currency.CurrencyRemoteDataSource
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.utils.constants.CURRENCY_API_KEY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CurrencyRemoteDataSourceImpl @Inject constructor(
    private val currencyService: CurrencyApiService
) : CurrencyRemoteDataSource {

    override fun getCurrencyRate(requiredCurrency: String): Flow<ResponseState> {
        return flow {
            emit(ResponseState.Loading)
            try {
                val response = currencyService.getLatestRates(CURRENCY_API_KEY, "EGP", "")
                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(ResponseState.Success(it))
                    } ?: emit(ResponseState.Failure(Throwable("Response is empty")))
                } else {
                    emit(ResponseState.Failure(Throwable("Response error: ${response.code()} - ${response.message()}")))
                }
            } catch (e: Exception) {
                emit(ResponseState.Failure(Throwable("Network error: ${e.localizedMessage}", e)))
            }
        }
    }
}