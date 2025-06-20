package com.example.buyva.features.categories.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.categories.ICategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow

class CategoryViewModel(private val categoryRepository: ICategoryRepository) : ViewModel(){
    private val _productsByCategory =  MutableStateFlow<ResponseState>(ResponseState.Loading)
    val productsByCategory: MutableStateFlow<ResponseState> = _productsByCategory

  suspend fun getProductByCategory(handle: String){
        categoryRepository.getProductsByCategory(handle).collect{
            try {
                _productsByCategory.value = ResponseState.Loading
                if (it != null) {
                    val products = it.collection?.products?.edges?.map { edge -> edge.node}
                    _productsByCategory.value = ResponseState.Success(products)
                } else {
                    _productsByCategory.value = ResponseState.Failure(Exception("Failed to fetch data"))
                }
            }catch (e:Exception){
                _productsByCategory.value = ResponseState.Failure(e)

            }
        }
    }

}

class CategoryFactory(private val categoryRepository: ICategoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(categoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}