package com.example.buyva.data.repository.categories

import com.example.buyva.GetProductsByCategoryQuery
import kotlinx.coroutines.flow.Flow

interface ICategoryRepository {
    fun getProductsByCategory(handle: String): Flow<GetProductsByCategoryQuery.Data?>
}