package com.example.buyva.data.remote

import kotlinx.serialization.json.JsonObject
import retrofit2.Response

interface RemoteDataSource {
    suspend fun createPaymentIntent(
        amount: Int,
        currency: String,
        paymentMethod: String = "card"
    ): Response<JsonObject>
}