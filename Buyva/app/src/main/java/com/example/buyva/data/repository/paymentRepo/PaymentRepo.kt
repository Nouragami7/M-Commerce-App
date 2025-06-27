package com.example.buyva.data.repository.paymentRepo

import com.example.buyva.admin.CompleteDraftOrderMutation
import com.example.buyva.admin.type.DraftOrderInput
import com.example.buyva.data.model.uistate.ResponseState
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface PaymentRepo {
        suspend fun createPaymentIntent(
            amount: Int,
            currency: String,
            paymentMethod: String
        ): Response<com.google.gson.JsonObject>

       suspend fun createDraftOrder(draftOrderInput: DraftOrderInput): Flow<ResponseState>
       suspend fun completeDraftOrder(draftOrderId: String): Flow<CompleteDraftOrderMutation.Data>
}