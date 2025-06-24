package com.example.buyva.features.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.home.IHomeRepository
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel(private val homeRepository: IHomeRepository) : ViewModel() {
  private val _brandsAndProducts = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val brandsAndProducts: MutableStateFlow<ResponseState> = _brandsAndProducts

    suspend fun getBrandsAndProduct(){
        homeRepository.getBrandsAndProduct().collect{
            try {
                _brandsAndProducts.value = ResponseState.Loading
                if (it != null) {
                    val brands = it.brands.nodes
                    val products = it.products.edges.map { edge -> edge.node }
                    println("brands +++++++++++++++++++++++ ${products.get(0).variants.edges.get(0).node.id}")
                    _brandsAndProducts.value = ResponseState.Success(Pair(brands, products))
                } else {
                    _brandsAndProducts.value = ResponseState.Failure(Exception("Failed to fetch data"))
                }
            }catch (e:Exception){
                _brandsAndProducts.value = ResponseState.Failure(e)

            }
        }
    }


}

class HomeFactory(private val homeRepository: IHomeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(homeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}