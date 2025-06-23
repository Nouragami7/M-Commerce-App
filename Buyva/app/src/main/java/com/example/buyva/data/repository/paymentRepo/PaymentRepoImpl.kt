package com.example.buyva.data.repository.paymentRepo
import android.util.Log
import com.example.buyva.admin.type.DraftOrderInput
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow
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

    override suspend fun createDraftOrder(draftOrderInput: DraftOrderInput): Flow<ResponseState> {
        Log.d("OrderRepo", "Calling createDraftOrder with input: $draftOrderInput")
        return remoteDataSource.createDraftOrder(draftOrderInput)

    }


}