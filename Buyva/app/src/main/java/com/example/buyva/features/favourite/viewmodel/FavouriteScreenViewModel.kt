package com.example.buyva.features.favourite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.buyva.data.model.FavouriteProduct
import com.example.buyva.data.repository.favourite.FavouriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavouriteScreenViewModel(
    private val repository: FavouriteRepository
) : ViewModel() {

    val favouriteProducts = repository.getFavourites()

    fun toggleFavourite(product: FavouriteProduct) {
        viewModelScope.launch {
            if (favouriteProducts.value.any { it.id == product.id }) {
                repository.removeFavourite(product.id)
            } else {
                repository.addFavourite(product)
            }
        }
    }

    fun isFavourite(productId: String): Boolean {
        return favouriteProducts.value.any { it.id == productId }
    }
}
class FavouriteViewModelFactory(
    private val repository: FavouriteRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavouriteScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavouriteScreenViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
