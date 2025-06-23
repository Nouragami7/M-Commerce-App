package com.example.buyva.features.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buyva.data.model.SearchUiState
import com.example.buyva.data.model.UiProduct
import com.example.buyva.data.repository.search.ISearchRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: ISearchRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun updateSearchText(text: String) {
        _uiState.update { it.copy(searchText = text) }
        performSearch(text)
    }

    fun updateMaxPrice(price: Float) {
        _uiState.update { state ->
            val filtered = state.searchResults.filter { it.price <= price }
            state.copy(
                maxPrice = price,
                filteredProducts = filtered
            )
        }
    }

    private fun performSearch(query: String) {
        searchJob?.cancel()

        if (query.length < 3) {
            _uiState.update {
                it.copy(
                    searchResults = emptyList(),
                    filteredProducts = emptyList(),
                    isLoading = false
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        searchJob = viewModelScope.launch {
            delay(300) // Debounce time
            try {
                repository.searchProducts(query).collect { products ->
                    val filtered = products.filter { it.price <= _uiState.value.maxPrice }
                    _uiState.update {
                        it.copy(
                            searchResults = products,
                            filteredProducts = filtered,
                            isLoading = false
                        )
                    }
                }
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
}