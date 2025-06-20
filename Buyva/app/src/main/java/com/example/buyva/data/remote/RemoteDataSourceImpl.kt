package com.example.buyva.data.remote

import kotlinx.serialization.json.JsonObject
import retrofit2.Response

class RemoteDataSourceImpl(private val stripeAPI: StripeAPI) : RemoteDataSource  {
    override suspend fun createPaymentIntent(
        amount: Int,
        currency: String,
        paymentMethod: String
    ): Response<com.google.gson.JsonObject> {
        return stripeAPI.createPaymentIntent(amount, currency, paymentMethod)
    }
}