package com.example.buyva.data.repository.favourite

import com.example.buyva.GetFavouriteProductsByIdsQuery
import com.example.buyva.GetFavouriteProductsByIdsQuery.OnProduct

import com.example.buyva.data.model.FavouriteProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
interface FavouriteRepository {
    suspend fun addFavourite(productId: String)
    suspend fun removeFavourite(productId: String)
    fun getFavourites(): Flow<List<OnProduct>> // Correct type
}

