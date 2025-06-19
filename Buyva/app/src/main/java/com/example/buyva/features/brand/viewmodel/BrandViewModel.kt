package com.example.buyva.features.brand.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.brand.IBrandRepository
import kotlinx.coroutines.flow.MutableStateFlow

class BrandViewModel(private val brandRepository: IBrandRepository) : ViewModel(){
    private val _productsOfBrand = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val productsOfBrand: MutableStateFlow<ResponseState> = _productsOfBrand

    suspend fun getProductsByCollection(collectionId: String) {
        brandRepository.getProductsByCollection(collectionId).collect {
            try {
                _productsOfBrand.value = ResponseState.Loading
                if (it?.collection?.products?.edges != null) {
                    val products = it.collection.products.edges.map { edge -> edge.node }
                    _productsOfBrand.value = ResponseState.Success(products)
                } else {
                    _productsOfBrand.value = ResponseState.Failure(Exception("Failed to fetch data: collection or products is null"))
                }

            }catch(e:Exception) {
                _productsOfBrand.value = ResponseState.Failure(e)

            }
        }
    }
}
class BrandFactory(private val brandRepository: IBrandRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BrandViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BrandViewModel(brandRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}