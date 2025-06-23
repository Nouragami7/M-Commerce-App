package com.example.buyva.features.search.view

import ProductSection
import SearchBarWithCartIcon
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buyva.features.favourite.viewmodel.FavouriteScreenViewModel
import com.example.buyva.features.search.viewmodel.SearchViewModel
import com.example.buyva.utils.components.PriceFilterSlider

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = viewModel(),
    favouriteViewModel: FavouriteScreenViewModel,
    onProductClick: (String) -> Unit = {}
) {
    val state by searchViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchBarWithCartIcon(
            onCartClick = { /* TODO: Navigate to cart if needed */ }
        )

        Spacer(modifier = Modifier.height(8.dp))

        PriceFilterSlider(
            maxPrice = state.maxPrice,
            onPriceChange = { searchViewModel.updateMaxPrice(it) }
        )

        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Black)
                }
            }

            state.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.error ?: "Unknown error", color = Color.Red)
                }
            }

            state.filteredProducts.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No products found", color = Color.Gray)
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 8.dp)
                ) {
                    item {
                        ProductSection(
                            products = state.filteredProducts,
                            onProductClick = onProductClick,
                            favouriteViewModel = favouriteViewModel
                        )
                    }
                }
            }
        }
    }

    // Automatically update search if user types (debounced in ViewModel)
    LaunchedEffect(state.searchText) {
        searchViewModel.updateSearchText(state.searchText)
    }
}
