package com.example.buyva.features.favourite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buyva.GetFavouriteProductsByIdsQuery.OnProduct
import com.example.buyva.data.repository.favourite.FavouriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteScreenViewModel @Inject constructor(
    private val repository: FavouriteRepository
) : ViewModel() {

    private val _favouriteProducts = MutableStateFlow<List<OnProduct>>(emptyList())
    val favouriteProducts: StateFlow<List<OnProduct>> = _favouriteProducts.asStateFlow()

    init {
        observeFavourites()
    }

    private fun observeFavourites() {
        viewModelScope.launch {
            repository.getFavourites().collect { products ->
                _favouriteProducts.value = products
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
