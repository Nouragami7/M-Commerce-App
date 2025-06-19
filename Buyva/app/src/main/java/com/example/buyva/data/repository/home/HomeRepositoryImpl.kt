package com.example.buyva.data.repository.home

import com.example.buyva.BrandsAndProductsQuery
import com.example.buyva.data.datasource.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow

class HomeRepositoryImpl(private val remoteDataSource: RemoteDataSource) : IHomeRepository{
    override fun getBrandsAndProduct(): Flow<BrandsAndProductsQuery.Data?> {
        return remoteDataSource.getBrandsAndProduct()
    }
}