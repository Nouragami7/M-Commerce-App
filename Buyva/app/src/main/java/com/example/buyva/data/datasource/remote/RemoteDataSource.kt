package com.example.buyva.data.datasource.remote

import com.example.buyva.BrandsAndProductsQuery
import com.example.buyva.ProductsByCollectionQuery
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    fun getBrandsAndProduct(): Flow<BrandsAndProductsQuery.Data?>
    fun getProductsByCollection(collectionId: String): Flow<ProductsByCollectionQuery.Data?>
}