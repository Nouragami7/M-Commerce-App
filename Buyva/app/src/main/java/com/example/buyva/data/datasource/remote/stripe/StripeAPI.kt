package com.example.buyva.data.datasource.remote.stripe

import com.example.buyva.utils.constants.CURRENCY_UNIT
import com.example.buyva.utils.sharedpreference.currency.CurrencyManager
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.util.Locale

interface StripeAPI {

    @FormUrlEncoded
    @POST("payment_intents")
    suspend fun createPaymentIntent(
        @Field("amount") amount: Int,
        @Field("currency") currency: String ,
        @Field("payment_method_types[]") paymentMethod: String = "card",
    ): Response<com.google.gson.JsonObject>

}