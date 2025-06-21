package com.example.buyva.features.favourite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buyva.GetFavouriteProductsByIdsQuery.OnProduct
import com.example.buyva.data.repository.favourite.FavouriteRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavouriteScreenViewModel(
    private val repository: FavouriteRepository
) : ViewModel() {

    private val _favouriteProducts = MutableStateFlow<List<OnProduct>>(emptyList())
    val favouriteProducts: StateFlow<List<OnProduct>> = _favouriteProducts.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getFavourites().collect { products ->
                _favouriteProducts.value = products // Fixed this line
            }
        }
    }

    fun toggleFavourite(productId: String) {
        viewModelScope.launch {
            if (isFavourite(productId)) {
                repository.removeFavourite(productId)
            } else {
                repository.addFavourite(productId)
            }
        }
    }

    fun isFavourite(productId: String): Boolean {
        return _favouriteProducts.value.any { it.id == productId }
    }
}