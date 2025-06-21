package com.example.buyva.data.repository.favourite

import com.example.buyva.data.model.FavouriteProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FavouriteRepositoryImpl : FavouriteRepository {
    private val favourites = MutableStateFlow<List<FavouriteProduct>>(emptyList())

    override suspend fun addFavourite(product: FavouriteProduct) {
        val currentList = favourites.value.toMutableList()
        if (currentList.none { it.id == product.id }) {
            currentList.add(product)
            favourites.value = currentList
        }
    }

    override suspend fun removeFavourite(productId: String) {
        val currentList = favourites.value.toMutableList()
        currentList.removeAll { it.id == productId }
        favourites.value = currentList
    }

    override fun getFavourites(): StateFlow<List<FavouriteProduct>> = favourites
}
