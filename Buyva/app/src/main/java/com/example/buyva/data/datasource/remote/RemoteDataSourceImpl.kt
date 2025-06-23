package com.example.buyva.data.datasource.remote

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.exception.ApolloException
import com.example.buyva.AddProductToCartMutation
import com.example.buyva.CreateAddressMutation
import com.example.buyva.BrandsAndProductsQuery
import com.example.buyva.CreateCartMutation
import com.example.buyva.DeleteAddressMutation
import com.example.buyva.GetAddressesQuery
import com.example.buyva.GetCartDetailsQuery
import com.example.buyva.GetProductByIdQuery
import com.example.buyva.GetProductsByCategoryQuery
import com.example.buyva.ProductsByCollectionQuery
import com.example.buyva.RemoveProductFromCartMutation
import com.example.buyva.SearchProductsQuery
import com.example.buyva.data.model.Address
import com.example.buyva.data.model.UiProduct
import com.example.buyva.data.model.uistate.ResponseState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class RemoteDataSourceImpl(  private val apolloClient: ApolloClient
) : RemoteDataSource {

    override fun getBrandsAndProduct(): Flow<BrandsAndProductsQuery.Data?> = flow {
        val response = apolloClient.query(BrandsAndProductsQuery()).execute()
        emit(response.data)
    }.catch {
        emit(null)
    }

    override fun getProductsByCollection(collectionId: String): Flow<ProductsByCollectionQuery.Data?> = flow{
        val response = apolloClient.query(ProductsByCollectionQuery(collectionId)).execute()
        emit(response.data)
    }.catch {
        emit(null)
    }
    override fun getProductById(productId: String): Flow<GetProductByIdQuery.Data?> = flow {
        val response = apolloClient.query(GetProductByIdQuery(productId)).execute()
        emit(response.data)
    }.catch {
        emit(null)
    }

    override fun getProductsByCategory(handle: String): Flow<GetProductsByCategoryQuery.Data?> = flow {
        val response = apolloClient.query(GetProductsByCategoryQuery(handle)).execute()
        emit(response.data)
    }.catch{
        emit(null)
    }

    override fun addToCartById(
        cartId: String,
        quantity: Int,
        variantID: String
    ): Flow<ResponseState> {
        val mutation = AddProductToCartMutation(cartId, quantity, variantID)
        return flow {
            emit(ResponseState.Loading)
            val response: ApolloResponse<AddProductToCartMutation.Data> =
                apolloClient.mutation(mutation).execute()
            if (response.hasErrors()) {
                val errorMessages = response.errors?.joinToString { it.message } ?: "Unknown error"
                emit(ResponseState.Failure(Exception(errorMessages)))
            } else {
                emit(ResponseState.Success(true))
            }
        }
    }


    override suspend fun createCart(email: String, token: String): Flow<ResponseState> = flow {
val mutation = CreateCartMutation(email, token)
        try {
            emit(ResponseState.Loading)


            val response: ApolloResponse<CreateCartMutation.Data> =
                apolloClient.mutation(mutation).execute()


            if (response.hasErrors()) {
                val errorMessages = response.errors?.joinToString { it.message } ?: "Unknown error"
                emit(ResponseState.Failure(Exception(errorMessages)))

            } else {

                val cartId = response.data?.cartCreate?.cart?.id
                emit(ResponseState.Success(cartId))

            }
        } catch (e: ApolloException) {
            emit(ResponseState.Failure(e))
        }
    }

    override suspend fun getCartDetails(cartId: String): Flow<ResponseState> = flow {
        emit(ResponseState.Loading)

        val response = apolloClient.query(GetCartDetailsQuery(cartId)).execute()
        val cart = response.data?.cart
        if (cart != null) {
            emit(ResponseState.Success(cart))
        } else {
            throw Exception("No cart found with this ID")
        }
    }.catch { e ->
        emit(ResponseState.Failure(e))
    }


    override suspend fun removeProductFromCart(
        cartId: String,
        lineItemId: String
    ): Flow<ResponseState> = flow {
        emit(ResponseState.Loading)

        val mutation = RemoveProductFromCartMutation(cartId, lineItemId)

        try {
            val response: ApolloResponse<RemoveProductFromCartMutation.Data> =
                apolloClient.mutation(mutation).execute()

            if (response.hasErrors()) {
                Log.i("1", "removeProductFromCart: ${response.errors}")
                val errorMessages = response.errors?.joinToString { it.message } ?: "Unknown error"
                emit(ResponseState.Failure(Throwable(errorMessages)))
            } else {
                val data = response.data
                if (data != null) {
                    Log.i("1", "removeProductFromCart: $data")
                    emit(ResponseState.Success(data.cartLinesRemove.toString()))
                } else {
                    emit(ResponseState.Failure(Throwable("Response data is null")))
                }
            }
        } catch (e: ApolloException) {
            emit(ResponseState.Failure(e))
        }
    }

    override suspend fun createCustomerAddress(
        token: String,
        address: Address
    ): Flow<ResponseState> = flow {
        emit(ResponseState.Loading)
        try {
            val response : ApolloResponse<CreateAddressMutation.Data> = apolloClient.mutation(
                CreateAddressMutation(
                    address1 = address.address1,
                    address2 = address.address2,
                    city = address.city,
                    country = address.country,
                    firstName = address.firstName,
                    lastName = address.lastName,
                    phone = address.phone,
                    token = token
                )
            ).execute()

            val errors = response.data?.customerAddressCreate?.customerUserErrors
            if (!errors.isNullOrEmpty()) {
                emit(ResponseState.Failure(Exception(errors.first().message)))
            } else {
                val createdAddress = response.data?.customerAddressCreate?.customerAddress
                emit(ResponseState.Success(createdAddress))
            }
        } catch (e: Exception) {
            emit(ResponseState.Failure(e))
        }
    }

    override suspend fun getCustomerAddresses(token: String): Flow<ResponseState> = flow {
        emit(ResponseState.Loading)
        try {
            val response = apolloClient.query(GetAddressesQuery(token)).execute()

            val nodes = response.data?.customer?.addresses?.nodes ?: emptyList()

            val addresses = nodes.mapNotNull { node ->
                node.let {
                    Address(
                        id = it.id,
                        address1 = it.address1 ?: "",
                        address2 = it.address2 ?: "",
                        city = it.city ?: "",
                        country = it.country ?: "",
                        firstName = it.firstName ?: "",
                        lastName = it.lastName ?: "",
                        phone = it.phone ?: ""
                    )
                }
            }

            emit(ResponseState.Success(addresses))
        } catch (e: Exception) {
            emit(ResponseState.Failure(e))
        }
    }
    override suspend fun deleteCustomerAddress(addressId: String, token: String): Flow<ResponseState> = flow {
        emit(ResponseState.Loading)
        try {
            val response = apolloClient.mutation(DeleteAddressMutation(addressId, token)).execute()

            val userErrors = response.data?.customerAddressDelete?.userErrors
            if (!userErrors.isNullOrEmpty()) {
                emit(ResponseState.Failure(Throwable(userErrors.first().message ?: "Unknown error")))
            } else {
                emit(ResponseState.Success(response.data?.customerAddressDelete?.deletedCustomerAddressId))
            }
        } catch (e: Exception) {
            emit(ResponseState.Failure(e))
        }
    }

    override fun searchProducts(query: String): Flow<List<UiProduct>> = flow {
        Log.d("SearchQuery", "Query sent: $query")

        val formattedQuery = "title:*$query*"
        val response = apolloClient.query(SearchProductsQuery(formattedQuery)).execute()
        Log.d("SearchRaw", "Raw response: ${response.data}")

        val products = response.data?.products?.edges?.mapNotNull { edge ->
            val node = edge.node ?: return@mapNotNull null

            val imageUrl = node.images?.edges?.firstOrNull()?.node?.src as? String ?: ""
            val price = node.variants?.edges?.firstOrNull()?.node?.priceV2?.amount
                ?.let { it.toString().toFloatOrNull() } ?: 0f


            val vendor = node.vendor ?: ""

            UiProduct(
                id = node.id,
                title = node.title,
                imageUrl = imageUrl,
                price = price,
                vendor = vendor
            )
        } ?: emptyList()

        emit(products)
    }.catch {
        Log.e("RemoteDataSource", "Search failed", it)
        emit(emptyList())
    }

}


