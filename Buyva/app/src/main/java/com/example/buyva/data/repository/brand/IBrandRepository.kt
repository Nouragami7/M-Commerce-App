package com.example.buyva.data.repository.brand

import com.example.buyva.ProductsByCollectionQuery
import kotlinx.coroutines.flow.Flow

interface IBrandRepository {
    fun getProductsByCollection(collectionId: String): Flow<ProductsByCollectionQuery.Data?>
}