package com.example.buyva.data.repository.paymentRepo

import kotlinx.serialization.json.JsonObject
import retrofit2.Response

interface PaymentRepo {
        suspend fun createPaymentIntent(
            amount: Int,
            currency: String,
            paymentMethod: String = "card"
        ): Response<com.google.gson.JsonObject>

}