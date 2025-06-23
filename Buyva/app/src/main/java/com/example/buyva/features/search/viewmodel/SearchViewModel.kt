package com.example.buyva.features.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buyva.data.model.SearchUiState
import com.example.buyva.data.model.UiProduct
import com.example.buyva.data.repository.search.ISearchRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SearchViewModel(private val repository: ISearchRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadAllProducts()
    }

    fun loadAllProducts() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                repository.getAllProducts().collect { products ->
                    _uiState.update {
                        it.copy(
                            allProducts = products,
                            searchResults = products,
                            isLoading = false
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

    fun updateMaxPrice(price: Float) {
        _uiState.update { it.copy(maxPrice = price) }
        applyCombinedFilters()
    }

    fun setSelectedBrand(brand: String?) {
        _uiState.update { it.copy(selectedBrand = brand) }
        applyCombinedFilters()
    }

    fun performSearch(query: String) {
        searchJob?.cancel()
        _uiState.update { it.copy(isLoading = true, error = null) }

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
                // Ignored
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

        val filteredByBrand = state.selectedBrand?.let { brand ->
            state.searchResults.filter { it.vendor.equals(brand, ignoreCase = true) }
        } ?: state.searchResults

        val finalFiltered = filteredByBrand.filter { it.price <= state.maxPrice }

        _uiState.update {
            it.copy(filteredProducts = finalFiltered)
        }
    }
}
