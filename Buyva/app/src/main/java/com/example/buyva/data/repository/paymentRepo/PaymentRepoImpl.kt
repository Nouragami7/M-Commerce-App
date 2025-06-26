package com.example.buyva.data.repository.paymentRepo
import android.util.Log
import com.example.buyva.admin.CompleteDraftOrderMutation
import com.example.buyva.admin.type.DraftOrderInput
import com.example.buyva.data.datasource.remote.RemoteDataSource
import com.example.buyva.data.model.uistate.ResponseState
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class PaymentRepoImpl@Inject constructor(private val remoteDataSource: RemoteDataSource)  : PaymentRepo{
    override suspend fun createPaymentIntent(
        amount: Int,
        currency: String,
        paymentMethod: String
        ): Response<com.google.gson.JsonObject> {
        Log.d("1", "Calling Stripe with amount=$amount, currency=$currency")
        return remoteDataSource.createPaymentIntent(amount, currency)
    }

    override suspend fun createDraftOrder(draftOrderInput: DraftOrderInput): Flow<ResponseState> {
        return remoteDataSource.createDraftOrder(draftOrderInput)

    }

    override suspend fun completeDraftOrder(draftOrderId: String): Flow<CompleteDraftOrderMutation.Data> {
        return remoteDataSource.completeDraftOrder(draftOrderId)
    }


}