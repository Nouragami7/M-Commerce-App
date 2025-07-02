package com.example.buyva.features.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buyva.data.model.DiscountBanner
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.home.IHomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: IHomeRepository) : ViewModel() {

    private val _brandsAndProducts = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val brandsAndProducts: StateFlow<ResponseState> = _brandsAndProducts

    private val _discountBanners = MutableStateFlow<List<DiscountBanner>>(emptyList())
    val discountBanners: StateFlow<List<DiscountBanner>> = _discountBanners

    fun getBrandsAndProduct() {
        viewModelScope.launch {
            homeRepository.getBrandsAndProduct().collect {
                try {
                    _brandsAndProducts.value = ResponseState.Loading
                    if (it != null) {
                        val brands = it.brands.nodes
                        val products = it.products.edges.map { edge -> edge.node }
                        _brandsAndProducts.value = ResponseState.Success(Pair(brands, products))
                    } else {
                        _brandsAndProducts.value =
                            ResponseState.Failure(Exception("Failed to fetch data"))
                    }
                } catch (e: Exception) {
                    _brandsAndProducts.value = ResponseState.Failure(e)
                }
            }
        }
    }

    fun fetchDiscounts() {
        viewModelScope.launch {
            try {
                homeRepository.getDiscountDetails().catch { e ->
                        Log.e("1", "Error: ${e.message}")
                    }.collect { data ->
                        val discounts =
                            data.discountNodes.edges.mapNotNull { it.node.discount.onDiscountCodeBasic }

                        val models = discounts.map {
                            val percentage =
                                it.customerGets.value.onDiscountPercentage?.percentage ?: 0.0
                            DiscountBanner(
                                code = "Use Code: ${it.title} for ${(percentage * 100).toInt()}% Off",
                                percentage = (percentage * 100).toInt(),
                                status = it.status.rawValue,
                                startsAt = it.startsAt.toString(),
                                endsAt = it.endsAt?.toString()
                            )
                        }

                        _discountBanners.value = models
                    }
            } catch (e: Exception) {
                Log.e("1", "Exception: ${e.message}")
            }
        }
    }

}
