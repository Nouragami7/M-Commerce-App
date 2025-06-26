package com.example.buyva.data.repository.cart

import android.util.Log
import com.example.buyva.GetCartDetailsQuery
import com.example.buyva.data.datasource.remote.RemoteDataSource
import com.example.buyva.data.model.CartItem
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.utils.constants.USER_TOKEN
import com.example.buyva.utils.sharedpreference.SharedPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CartRepoImpl @Inject constructor (
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
    override suspend fun getCartProducts(cartId: String): Flow<ResponseState> {
        return remoteDataSource.getCartDetails(cartId)
    }

    override suspend fun getCartProductList(cartId: String): Flow<ResponseState> = flow {
        emit(ResponseState.Loading)

        remoteDataSource.getCartDetails(cartId).collect { response ->
            when (response) {
                is ResponseState.Success<*> -> {
                    val cart = response.data as? GetCartDetailsQuery.Cart
                    if (cart != null) {
                        Log.i("1", "getCartProductList: ${cart.lines.edges}")
                        val products = cart.lines.edges.mapNotNull { edge ->
                            val variant = edge.node.merchandise.onProductVariant ?: return@mapNotNull null
                            val product = variant.product
                            Log.d("1", "amount type: ${variant.price.amount!!::class.qualifiedName}")

                            val priceString = variant.price.amount.toString()
                            val price = priceString.toDoubleOrNull() ?: 0.0


                            CartItem(
                                lineId = edge.node.id,
                                id = product.id,
                                variantId = variant.id,
                                title = product.title,
                                imageUrl = (product.featuredImage?.url ?: "").toString(),
                                price = price,
                                quantity = edge.node.quantity,
                                quantityAvailable = edge.node.merchandise.onProductVariant.quantityAvailable ?: 0
                            )
                        }
                        emit(ResponseState.Success(products))
                        Log.i("1", "getCartProductList: $products")
                    } else {
                        emit(ResponseState.Failure(Exception("Cart is null")))
                    }
                }

                is ResponseState.Failure -> emit(ResponseState.Failure(Exception("Unknown error")))
                is ResponseState.Loading -> emit(ResponseState.Loading)
            }
        }
    }





    override suspend fun writeCartIdToSharedPreferences(key: String, value: String) {
        Log.i("ShoppingCartFragment", "writeCartIdToSharedPreferences: ")
        sharedPreferences.saveToSharedPreferenceInGeneral( key, value)   }

    override suspend fun readUserToken(): String {
        return sharedPreferences.getFromSharedPreferenceInGeneral(USER_TOKEN) ?: ""
    }

    override suspend fun removeProductFromCart(
        cartId: String,
        lineItemId: String
    ): Flow<ResponseState> {
        Log.i("1", "removeProductFromCart: $cartId $lineItemId")
        return remoteDataSource.removeProductFromCart(cartId, lineItemId)

    }


}