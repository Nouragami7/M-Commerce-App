package com.example.buyva.data.remote

import com.example.buyva.BuildConfig
import kotlinx.serialization.json.JsonObject
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface StripeAPI {

    @FormUrlEncoded
    @POST("payment_intents")
    suspend fun createPaymentIntent(
        @Field("amount") amount: Int,
        @Field("currency") currency: String,
        @Field("payment_method_types[]") paymentMethod: String = "card",
    ): Response<JsonObject>

}