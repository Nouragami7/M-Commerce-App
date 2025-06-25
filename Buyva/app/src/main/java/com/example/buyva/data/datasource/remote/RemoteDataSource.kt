package com.example.buyva.data.datasource.remote

import com.example.buyva.BrandsAndProductsQuery
import com.example.buyva.GetProductByIdQuery
import com.example.buyva.GetProductsByCategoryQuery
import com.example.buyva.ProductsByCollectionQuery
import com.example.buyva.admin.GetDiscountAmountDetailsQuery
import com.example.buyva.admin.GetOrdersByCustomerEmailQuery
import com.example.buyva.data.model.Address
import com.example.buyva.data.model.UiProduct
import com.example.buyva.data.model.uistate.ResponseState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface RemoteDataSource {
    fun getBrandsAndProduct(): Flow<BrandsAndProductsQuery.Data?>
    fun getProductsByCollection(collectionId: String): Flow<ProductsByCollectionQuery.Data?>
    fun getProductById(productId: String): Flow<GetProductByIdQuery.Data?>
    fun getProductsByCategory(handle: String): Flow<GetProductsByCategoryQuery.Data?>
fun addToCartById(cartId: String, quantity: Int, variantID: String): Flow<ResponseState>
suspend fun createCart(email: String, token: String): Flow<ResponseState>
     suspend fun getCartDetails(cartId: String): Flow<ResponseState>
     suspend fun removeProductFromCart(cartId: String, lineItemId: String): Flow<ResponseState>
     suspend fun createCustomerAddress(token: String, address: Address): Flow<ResponseState>
     suspend fun getCustomerAddresses(token: String): Flow<ResponseState>
     suspend fun deleteCustomerAddress(addressId: String, token: String): Flow<ResponseState>
     suspend fun getOrders(email: String): Flow<GetOrdersByCustomerEmailQuery.Data?>
    fun searchProducts(query: String): Flow<List<UiProduct>>
     suspend fun updateAddress(address: Address, token: String): Flow<ResponseState>
     suspend fun getDiscountDetails(): Flow<GetDiscountAmountDetailsQuery.Data>







    }