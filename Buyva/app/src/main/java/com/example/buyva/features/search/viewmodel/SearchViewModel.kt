package com.example.buyva.features.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buyva.data.model.SearchUiState
import com.example.buyva.data.repository.search.ISearchRepository
import com.example.buyva.utils.sharedpreference.currency.CurrencyManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel@Inject constructor(private val repository: ISearchRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    private val _selectedPriceLimit = MutableStateFlow(0f)
    val selectedPriceLimit: StateFlow<Float> = _selectedPriceLimit.asStateFlow()
    private var searchJob: Job? = null
    private val _isSliderTouched = MutableStateFlow(false)
    val isSliderTouched: StateFlow<Boolean> = _isSliderTouched.asStateFlow()

    init {
        loadAllProducts()
    }

    fun loadAllProducts() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                repository.getAllProducts().collect { products ->
                    val rate = CurrencyManager.currencyRate.value
                    val prices = products.map { it.price }
                    val convertedPrices = prices.map { it * rate.toFloat() }

                    val minPrice = convertedPrices.minOrNull() ?: 0f
                    val maxPrice = convertedPrices.maxOrNull() ?: 0f

                    _uiState.update {
                        it.copy(
                            allProducts = products,
                            searchResults = products,
                            isLoading = false,
                            minPrice = minPrice,
                            maxPrice = maxPrice,
                            selectedPriceLimit = maxPrice,

                        )
                    }

                    applyCombinedFilters()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load products: ${e.message}",
                        filteredProducts = emptyList()
                    )
                }
            }
        }
    }

    fun updateSearchText(text: String) {
        _uiState.update { it.copy(searchText = text) }
        performSearch(text)
    }

    fun updateSelectedPriceLimit(price: Float) {
        _isSliderTouched.value = true
        _uiState.update { it.copy(selectedPriceLimit = price) }
        applyCombinedFilters()
    }



    fun setSelectedBrand(brand: String?) {
        _uiState.update { it.copy(selectedBrand = brand) }
        applyCombinedFilters()
    }

    fun performSearch(query: String) {
        searchJob?.cancel()
        _uiState.update { it.copy(isLoading = true, error = null) }
        _isSliderTouched.value = false // reset هنا

        searchJob = viewModelScope.launch {
            try {
                if (query.isNotEmpty()) delay(300)

                val products = if (query.isEmpty()) {
                    _uiState.value.allProducts
                } else {
                    repository.searchProducts(query).first()
                }

                _uiState.update {
                    it.copy(
                        searchResults = products,
                        isLoading = false
                    )
                }

                applyCombinedFilters()
            } catch (e: CancellationException) {
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Search failed: ${e.message}",
                        filteredProducts = emptyList()
                    )
                }
            }
        }
    }

    private fun applyCombinedFilters() {
        val state = _uiState.value
        val rate = CurrencyManager.currencyRate.value.toFloat()

        val filteredByBrand = state.selectedBrand?.let { brand ->
            state.searchResults.filter { it.vendor.equals(brand, ignoreCase = true) }
        } ?: state.searchResults

        val prices = filteredByBrand.map { it.price * rate }
        val newMinPrice = prices.minOrNull() ?: 0f
        val newMaxPrice = prices.maxOrNull() ?: 0f

        val selectedLimit = when {
            _isSliderTouched.value -> state.selectedPriceLimit // المستخدم حرّكه يدويًا
            else -> newMaxPrice // نعيّن الـ max تلقائيًا
        }

        val finalFiltered = filteredByBrand.filter {
            (it.price * rate) <= selectedLimit
        }

        _uiState.update {
            it.copy(
                filteredProducts = finalFiltered,
                minPrice = newMinPrice,
                maxPrice = newMaxPrice,
                selectedPriceLimit = selectedLimit
            )
        }
    }
}
