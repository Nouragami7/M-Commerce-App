package com.example.buyva.data.datasource.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.exception.ApolloException
import com.example.buyva.AddProductToCartMutation
import com.example.buyva.BrandsAndProductsQuery
import com.example.buyva.CreateCartMutation
import com.example.buyva.GetProductByIdQuery
import com.example.buyva.GetProductsByCategoryQuery
import com.example.buyva.ProductsByCollectionQuery
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

//    suspend fun loginAndGetTheToken(email: String, password: String): Result<String> {
//        return try {
//            val input = CustomerAccessTokenCreateInput(email = email, password = password)
//            val response = apolloClient
//                .mutation(CreateCustomerAccessTokenMutation(input))
//                .execute()
//
//            val token = response.data?.customerAccessTokenCreate?.customerAccessToken?.accessToken
//            val error = response.data?.customerAccessTokenCreate?.customerUserErrors?.firstOrNull()?.message
//
//            when {
//                token != null -> Result.success(token)
//                error != null -> Result.failure(Exception(error))
//                else -> Result.failure(Exception("Unknown Shopify error"))
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }



}
