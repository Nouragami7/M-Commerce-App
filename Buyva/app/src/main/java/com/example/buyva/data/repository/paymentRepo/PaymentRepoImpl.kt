package com.example.buyva.data.repository.paymentRepo
import android.util.Log
import com.example.buyva.data.remote.RemoteDataSource
import com.stripe.android.model.PaymentIntent
import kotlinx.serialization.json.JsonObject
import retrofit2.Response

class PaymentRepoImpl(private val remoteDataSource: RemoteDataSource)  : PaymentRepo{
    override suspend fun createPaymentIntent(
        amount: Int,
        currency: String,
        paymentMethod: String
        ): Response<com.google.gson.JsonObject> {
        Log.d("1", "Calling Stripe with amount=$amount, currency=$currency")
        return remoteDataSource.createPaymentIntent(amount, currency)
    }
    }