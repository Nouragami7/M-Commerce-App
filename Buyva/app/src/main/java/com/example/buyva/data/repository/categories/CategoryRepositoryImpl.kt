package com.example.buyva.data.repository.categories

import com.example.buyva.GetProductsByCategoryQuery
import com.example.buyva.data.datasource.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow

class CategoryRepositoryImpl(private val remoteDataSource: RemoteDataSource) : ICategoryRepository {
    override fun getProductsByCategory(handle: String): Flow<GetProductsByCategoryQuery.Data?> {
        return remoteDataSource.getProductsByCategory(handle = handle)
    }


}