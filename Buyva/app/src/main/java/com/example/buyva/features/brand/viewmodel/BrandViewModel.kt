package com.example.buyva.features.brand.viewmodel

import androidx.lifecycle.ViewModel
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.home.IHomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class BrandViewModel @Inject constructor (private val homeRepository:IHomeRepository ) : ViewModel(){
    private val _productsOfBrand = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val productsOfBrand: MutableStateFlow<ResponseState> = _productsOfBrand

    suspend fun getProductsByBrand(collectionId: String) {
        homeRepository.getProductsByBrand(collectionId).collect {
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
