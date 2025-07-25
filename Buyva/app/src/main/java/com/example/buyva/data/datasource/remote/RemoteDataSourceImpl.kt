package com.example.buyva.data.datasource.remote

import android.util.Log
import androidx.compose.ui.graphics.PathSegment
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import com.example.buyva.AddProductToCartMutation
import com.example.buyva.BrandsAndProductsQuery
import com.example.buyva.CartLinesUpdateMutation
import com.example.buyva.CreateAddressMutation
import com.example.buyva.CreateCartMutation
import com.example.buyva.CustomerAddressUpdateMutation
import com.example.buyva.DeleteAddressMutation
import com.example.buyva.GetAddressesQuery
import com.example.buyva.GetCartDetailsQuery
import com.example.buyva.GetProductByIdQuery
import com.example.buyva.GetProductsByCategoryQuery
import com.example.buyva.ProductsByCollectionQuery
import com.example.buyva.RemoveProductFromCartMutation
import com.example.buyva.SearchProductsQuery
import com.example.buyva.admin.CompleteDraftOrderMutation
import com.example.buyva.admin.CreateDraftOrderMutation
import com.example.buyva.admin.GetDiscountAmountDetailsQuery
import com.example.buyva.admin.GetOrdersByCustomerEmailQuery
import com.example.buyva.admin.type.DraftOrderInput
import com.example.buyva.data.datasource.remote.graphql.ApolloAdmin
import com.example.buyva.data.datasource.remote.stripe.StripeAPI
import com.example.buyva.data.model.Address
import com.example.buyva.data.model.UiProduct
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.type.CartLineUpdateInput
import com.example.buyva.type.MailingAddressInput
import com.example.buyva.utils.sharedpreference.currency.CurrencyManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient,
    private val apolloAdmin: ApolloAdmin,
    private val stripeAPI: StripeAPI
) : RemoteDataSource {

    override fun getBrandsAndProduct(): Flow<BrandsAndProductsQuery.Data?> = flow {
        val response = apolloClient.query(BrandsAndProductsQuery()).execute()
        emit(response.data)
    }.catch {
        emit(null)
    }

    override fun getProductsByCollection(collectionId: String): Flow<ProductsByCollectionQuery.Data?> =
        flow {
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

    override fun getProductsByCategory(handle: String): Flow<GetProductsByCategoryQuery.Data?> =
        flow {
            val response = apolloClient.query(GetProductsByCategoryQuery(handle)).execute()
            emit(response.data)
        }.catch {
            emit(null)
        }

    override fun addToCartById(
        cartId: String, quantity: Int, variantID: String
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
        cartId: String, lineItemId: String
    ): Flow<ResponseState> = flow {
        emit(ResponseState.Loading)

        val mutation = RemoveProductFromCartMutation(cartId, lineItemId)

        try {
            val response: ApolloResponse<RemoveProductFromCartMutation.Data> =
                apolloClient.mutation(mutation).execute()

            if (response.hasErrors()) {
                val errorMessages = response.errors?.joinToString { it.message } ?: "Unknown error"
                emit(ResponseState.Failure(Throwable(errorMessages)))
            } else {
                val data = response.data
                if (data != null) {
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
        token: String, address: Address
    ): Flow<ResponseState> = flow {
        emit(ResponseState.Loading)
        try {
            val response: ApolloResponse<CreateAddressMutation.Data> = apolloClient.mutation(
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

    override suspend fun deleteCustomerAddress(
        addressId: String, token: String
    ): Flow<ResponseState> = flow {
        emit(ResponseState.Loading)
        try {
            val response = apolloClient.mutation(DeleteAddressMutation(addressId, token)).execute()

            val userErrors = response.data?.customerAddressDelete?.userErrors
            if (!userErrors.isNullOrEmpty()) {
                emit(
                    ResponseState.Failure(
                        Throwable(
                            userErrors.first().message
                        )
                    )
                )
            } else {
                emit(ResponseState.Success(response.data?.customerAddressDelete?.deletedCustomerAddressId))
            }
        } catch (e: Exception) {
            emit(ResponseState.Failure(e))
        }
    }

    override suspend fun getOrders(email: String): Flow<GetOrdersByCustomerEmailQuery.Data?> =
        flow {
            val response = apolloAdmin.admin.query(GetOrdersByCustomerEmailQuery(email)).execute()
            emit(response.data)
        }.catch {
            emit(null)

        }

    override fun searchProducts(query: String): Flow<List<UiProduct>> = flow {
        Log.d("SearchQuery", "Query sent: $query")

        val formattedQuery = "title:*$query*"
        val response = apolloClient.query(SearchProductsQuery(formattedQuery)).execute()
        Log.d("SearchRaw", "Raw response: ${response.data}")

        val products = response.data?.products?.edges?.mapNotNull { edge ->
            val node = edge.node

            val imageUrl = node.images.edges.firstOrNull()?.node?.src as? String ?: ""
            val price = node.variants.edges.firstOrNull()?.node?.priceV2?.amount?.let {
                it.toString().toFloatOrNull()
            } ?: 0f


            val vendor = node.vendor

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

    override suspend fun updateAddress(address: Address, token: String): Flow<ResponseState> =
        flow {
            emit(ResponseState.Loading)

            try {
                val response = apolloClient.mutation(
                    CustomerAddressUpdateMutation(
                        customerAccessToken = token,
                        id = address.id!!,
                        address = MailingAddressInput(
                            firstName = Optional.Present(address.firstName),
                            lastName = Optional.Present(address.lastName),
                            phone = Optional.Present(address.phone),
                            address1 = Optional.Present(address.address1),
                            address2 = Optional.Present(address.address2),
                            city = Optional.Present(address.city),
                            country = Optional.Present(address.country),
                        )
                    )
                ).execute()

                val updated = response.data?.customerAddressUpdate?.customerAddress

                if (updated != null) {
                    emit(ResponseState.Success(address.copy(id = updated.id)))
                } else {
                    val errors = response.data?.customerAddressUpdate?.customerUserErrors
                    val errorMessage =
                        errors?.joinToString { "${it.field?.joinToString()} - ${it.message}" }
                            ?: "Unknown Shopify error"
                    Log.e("1", errorMessage)
                    emit(ResponseState.Failure(Throwable(errorMessage)))
                }

            } catch (e: Exception) {
                emit(
                    ResponseState.Failure(
                        Throwable(e.message ?: "Unknown error")
                    )
                )
            }
        }

    override suspend fun getDiscountDetails(): Flow<GetDiscountAmountDetailsQuery.Data> = flow {
        val response = apolloAdmin.admin.query(GetDiscountAmountDetailsQuery()).execute()

        if (response.hasErrors()) {
            throw Exception(response.errors?.first()?.message ?: "GraphQL error")
        }

        val data = response.data ?: throw Exception("Response data is null")
        emit(data)
    }

    override suspend fun createPaymentIntent(
        amount: Int, currency: String, paymentMethod: String
    ): Response<com.google.gson.JsonObject> {
        return stripeAPI.createPaymentIntent(
            amount, CurrencyManager.currencyUnit.value.lowercase(), paymentMethod
        )
    }

    override suspend fun createDraftOrder(draftOrderInput: DraftOrderInput): Flow<ResponseState> =
        flow {

            try {
                val response =
                    apolloAdmin.admin.mutation(CreateDraftOrderMutation(draftOrderInput)).execute()
                val draftOrder = response.data?.draftOrderCreate?.draftOrder
                if (draftOrder != null) {
                    emit(ResponseState.Success("Draft Order Created: ${draftOrder.id}"))
                } else {
                    val errorMsg =
                        response.data?.draftOrderCreate?.userErrors?.joinToString(", ") { it.message }

                    emit(ResponseState.Failure(Throwable(errorMsg)))
                }
            } catch (e: Exception) {
                emit(ResponseState.Failure(Throwable(e.message)))
            }
        }

    override suspend fun completeDraftOrder(draftOrderId: String): Flow<CompleteDraftOrderMutation.Data> =
        flow {
            try {
                val response =
                    ApolloAdmin.admin.mutation(CompleteDraftOrderMutation(draftOrderId)).execute()

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


    override fun updateCartLine(cartId: String, lineId: String, quantity: Int): Flow<ResponseState> = flow {
        emit(ResponseState.Loading)
        try {
            val response = apolloClient.mutation(
                CartLinesUpdateMutation(
                    cartId = cartId,
                    lines = listOf(
                        CartLineUpdateInput(
                            id = lineId,
                            quantity = Optional.Present(quantity)
                        )
                    )
                )
            ).execute()

            if (response.hasErrors()) {
                emit(ResponseState.Failure(Throwable(response.errors?.firstOrNull()?.message)))
            } else {
                emit(ResponseState.Success(true))
            }
        } catch (e: Exception) {
            emit(ResponseState.Failure(e))
        }
    }


}


