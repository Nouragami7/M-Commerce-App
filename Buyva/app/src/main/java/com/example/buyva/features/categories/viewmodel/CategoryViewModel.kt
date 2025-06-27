package com.example.buyva.features.categories.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.categories.ICategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(private val categoryRepository: ICategoryRepository) :
    ViewModel() {
    private val _productsByCategory = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val productsByCategory: MutableStateFlow<ResponseState> = _productsByCategory

    fun getProductByCategory(handle: String) {
        viewModelScope.launch {
            _productsByCategory.value = ResponseState.Loading
            try {
                categoryRepository.getProductsByCategory(handle).collect { response ->
                    val products = response?.collectionByHandle?.products?.edges?.map { it.node }
                        ?: emptyList()
                    println("Fetched ${products.size} products for category: $handle")
                    _productsByCategory.value = ResponseState.Success(products)
                }
            } catch (e: Exception) {
                _productsByCategory.value = ResponseState.Failure(e)
            }
        }
    }

}
