package com.example.buyva.features.categories.viewmodel

import androidx.lifecycle.ViewModel
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.categories.ICategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(private val categoryRepository: ICategoryRepository) : ViewModel() {
    private val _productsByCategory = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val productsByCategory: MutableStateFlow<ResponseState> = _productsByCategory

    suspend fun getProductByCategory(handle: String) {
        _productsByCategory.value = ResponseState.Loading
        try {
            categoryRepository.getProductsByCategory(handle).collect { response ->
                val products =
                    response?.collectionByHandle?.products?.edges?.map { it.node } ?: emptyList()
                println("Fetched ${products.size} products for category: $handle")
                _productsByCategory.value = ResponseState.Success(products)
            }
        } catch (e: Exception) {
            _productsByCategory.value = ResponseState.Failure(e)
        }
    }


}

//class CategoryFactory(private val categoryRepository: ICategoryRepository) :
//    ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST") return CategoryViewModel(categoryRepository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}