package com.example.buyva.data.repository.search

import com.example.buyva.data.model.UiProduct
import kotlinx.coroutines.flow.Flow

interface ISearchRepository {
    fun getAllProducts(): Flow<List<UiProduct>>
    fun searchProducts(query: String): Flow<List<UiProduct>>

}
