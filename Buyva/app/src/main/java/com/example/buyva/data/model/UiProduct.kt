package com.example.buyva.data.model

data class UiProduct(
    val id: String,
    val title: String,
    val imageUrl: String,
    val price: Float
)

data class SearchUiState(
    val searchText: String = "",
    val searchResults: List<UiProduct> = emptyList(),
    val filteredProducts: List<UiProduct> = emptyList(),
    val maxPrice: Float = 300f,
    val isLoading: Boolean = false,
    val error: String? = null
)
