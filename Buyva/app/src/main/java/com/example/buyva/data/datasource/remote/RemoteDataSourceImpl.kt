package com.example.buyva.data.datasource.remote

import com.apollographql.apollo3.ApolloClient
import com.example.buyva.BrandsAndProductsQuery
import com.example.buyva.ProductsByCollectionQuery
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

}
