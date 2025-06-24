package com.example.buyva.data.repository.home

import com.example.buyva.BrandsAndProductsQuery
import com.example.buyva.GetProductByIdQuery
import com.example.buyva.ProductsByCollectionQuery
import com.example.buyva.data.datasource.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow

class HomeRepositoryImpl(private val remoteDataSource: RemoteDataSource) : IHomeRepository{
    override fun getBrandsAndProduct(): Flow<BrandsAndProductsQuery.Data?> {
        return remoteDataSource.getBrandsAndProduct()
    }
    override fun getProductById(productId: String): Flow<GetProductByIdQuery.Data?> {
        return remoteDataSource.getProductById(productId)
    }

    override fun getProductsByBrand(collectionId: String): Flow<ProductsByCollectionQuery.Data?> {
        return remoteDataSource.getProductsByCollection(collectionId)
    }

}