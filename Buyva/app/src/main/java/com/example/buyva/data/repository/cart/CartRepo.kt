package com.example.buyva.data.repository.cart

import com.example.buyva.data.model.uistate.ResponseState
import kotlinx.coroutines.flow.Flow

interface CartRepo {
    suspend fun createCart(email: String, token: String): Flow<ResponseState>
    suspend fun addToCartById(
        cartId: String,
        quantity: Int,
        variantID: String
    ): Flow<ResponseState>
   suspend fun getCartProducts(cartId: String): Flow<ResponseState>
    suspend fun writeCartIdToSharedPreferences(key: String, value: String)
    suspend fun getCartProductList(cartId: String): Flow<ResponseState>

    //fun readCartIdFromSharedPreferences(): String
     suspend fun readUserToken(): String
     suspend fun removeProductFromCart(cartId: String, lineItemId: String): Flow<ResponseState>





}