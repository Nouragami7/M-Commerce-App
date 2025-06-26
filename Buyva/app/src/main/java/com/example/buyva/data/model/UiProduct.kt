package com.example.buyva.data.model

data class UiProduct(
    val id: String,
    val title: String,
    val imageUrl: String,
    val price: Float  ,
    val vendor: String,

)

data class SearchUiState(
    val allProducts: List<UiProduct> = emptyList(),
    val searchText: String = "",
    val searchResults: List<UiProduct> = emptyList(),
    val filteredProducts: List<UiProduct> = emptyList(),
    val minPrice: Float = 0f,
    val maxPrice: Float = 0f,
    val selectedPriceLimit: Float = 0f, // ✅ ده الجديد

    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedBrand: String? = null

)
