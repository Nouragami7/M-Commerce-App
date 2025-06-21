package com.example.buyva.data.repository.cart

import android.util.Log
import com.example.buyva.data.datasource.remote.RemoteDataSource
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.utils.constants.CART_ID
import com.example.buyva.utils.constants.USER_TOKEN
import com.example.buyva.utils.sharedpreference.SharedPreference
import kotlinx.coroutines.flow.Flow

class CartRepoImpl (
    private val remoteDataSource: RemoteDataSource,
    private val sharedPreferences: SharedPreference
): CartRepo {


    override suspend fun createCart(email: String, token: String): Flow<ResponseState> {
        return remoteDataSource.createCart(email, token)
    }


    override suspend fun addToCartById(
        cartId: String,
        quantity: Int,
        variantID: String
    ): Flow<ResponseState> {
        return remoteDataSource.addToCartById(cartId, quantity, variantID)
    }
//    override suspend fun getCartProducts(cartId: String): Flow<ResponseState> {
//        return remoteDataSource.getProductsCart(cartId)
//    }

    override suspend fun writeCartIdToSharedPreferences(key: String, value: String) {
        Log.i("ShoppingCartFragment", "writeCartIdToSharedPreferences: ")
        sharedPreferences.saveToSharedPreferenceInGeneral( key, value)   }

//    override fun readCartIdFromSharedPreferences(): String {
//        Log.i("ShoppingCartFragment", "readCartIdFromSharedPreferences: ")
//        return sharedPreferences.getFromSharedPreferenceInGeneral(CART_ID) ?: ""
//    }
    override suspend fun readUserToken(): String {
        return sharedPreferences.getFromSharedPreferenceInGeneral(USER_TOKEN) ?: ""
    }



}