package com.example.buyva.data.remote

import android.util.Log
import com.example.buyva.admin.CompleteDraftOrderMutation
import com.example.buyva.admin.CreateDraftOrderMutation
import com.example.buyva.admin.type.DraftOrderInput
import com.example.buyva.data.datasource.remote.graphql.ApolloAdmin
import com.example.buyva.data.datasource.remote.stripe.StripeAPI
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.utils.sharedpreference.currency.CurrencyManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class RemoteDataSourceImpl(private val stripeAPI: StripeAPI) : RemoteDataSource  {
    override suspend fun createPaymentIntent(
        amount: Int,
        currency: String,
        paymentMethod: String
    ): Response<com.google.gson.JsonObject> {
        return stripeAPI.createPaymentIntent(amount, CurrencyManager.currencyUnit.value.lowercase(), paymentMethod)
    }

    override suspend fun createDraftOrder(draftOrderInput: DraftOrderInput): Flow<ResponseState> = flow {

        try {
            val response = ApolloAdmin.admin.mutation(CreateDraftOrderMutation(draftOrderInput)).execute()
            val draftOrder = response.data?.draftOrderCreate?.draftOrder
            if (draftOrder != null) {
                emit(ResponseState.Success("Draft Order Created: ${draftOrder.id}"))
            } else {
                val errorMsg = response.data?.draftOrderCreate?.userErrors
                    ?.joinToString(", ") { it.message }

                emit(ResponseState.Failure(Throwable(errorMsg)))
            }
        } catch (e: Exception) {
            emit(ResponseState.Failure(Throwable(e.message)))
        }
    }

    override suspend fun completeDraftOrder(draftOrderId: String): Flow<CompleteDraftOrderMutation.Data> = flow {
        try {
            val response = ApolloAdmin.admin
                .mutation(CompleteDraftOrderMutation(draftOrderId))
                .execute()

            val completedOrder = response.data?.draftOrderComplete?.draftOrder
            val userErrors = response.data?.draftOrderComplete?.userErrors

            if (completedOrder != null && userErrors.isNullOrEmpty()) {
                Log.d("OrderRD", "Draft Order Completed: ${completedOrder.id}")
                emit(response.data!!)
            } else {
                val errorMsg = userErrors?.joinToString(", ") { it.message } ?: "Unknown error"
                Log.e("OrderRD", "Failed to complete draft order: $errorMsg")
                throw Exception(errorMsg)
            }
        } catch (e: Exception) {
            Log.e("OrderRD", "Exception while completing draft order: ${e.message}", e)
            throw e
        }
    }

}