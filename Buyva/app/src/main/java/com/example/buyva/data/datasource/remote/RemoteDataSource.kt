package com.example.buyva.data.datasource.remote

import com.example.buyva.BrandsAndProductsQuery
import com.example.buyva.GetProductByIdQuery
import com.example.buyva.ProductsByCollectionQuery
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    fun getBrandsAndProduct(): Flow<BrandsAndProductsQuery.Data?>
    fun getProductsByCollection(collectionId: String): Flow<ProductsByCollectionQuery.Data?>
    fun getProductById(productId: String): Flow<GetProductByIdQuery.Data?>

}