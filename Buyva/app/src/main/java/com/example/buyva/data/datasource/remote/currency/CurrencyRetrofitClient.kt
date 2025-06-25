package com.example.buyva.data.datasource.remote.currency

import com.example.buyva.utils.constants.CURRENCY_API_BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CurrencyRetrofitClient {

    fun getInstance(): Retrofit {
        val retrofit = Retrofit.Builder()
            .baseUrl(CURRENCY_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        return retrofit
    }

}