package com.example.buyva.data.repository.brand

import com.example.buyva.ProductsByCollectionQuery
import com.example.buyva.data.datasource.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow

class BrandRepositoryImpl(private val remoteDataSource: RemoteDataSource) : IBrandRepository {
    override fun getProductsByCollection(collectionId: String): Flow<ProductsByCollectionQuery.Data?> {
        return remoteDataSource.getProductsByCollection(collectionId)
    }

}