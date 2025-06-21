package com.example.buyva.data.repository.favourite

import com.example.buyva.data.model.FavouriteProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface FavouriteRepository {
    suspend fun addFavourite(product: FavouriteProduct)
    suspend fun removeFavourite(productId: String)
    fun getFavourites(): StateFlow<List<FavouriteProduct>>
}
