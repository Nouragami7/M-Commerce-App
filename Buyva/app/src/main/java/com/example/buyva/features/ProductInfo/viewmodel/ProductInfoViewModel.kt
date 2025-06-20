package com.example.buyva.features.ProductInfo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.buyva.GetProductByIdQuery
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.home.IHomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductInfoViewModel(
    private val repository: IHomeRepository
) : ViewModel() {

    private val _product =
        MutableStateFlow<ResponseState>(ResponseState.Loading)

    val product: StateFlow<ResponseState> = _product

    fun fetchProduct(productId: String) {
        viewModelScope.launch {
            try {
                repository.getProductById(productId).collect { response ->
                    val product = response?.product

                    if (product != null) {
                        _product.value = ResponseState.Success(product)
                    } else {
                        _product.value = ResponseState.Failure(
                            Throwable("Product is null")
                        )
                    }
                }
            } catch (e: Exception) {
                _product.value = ResponseState.Failure(e)
            }
        }
    }
}

class ProductInfoViewModelFactory(private val repository: IHomeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductInfoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
