package com.example.buyva.features.search.view

import ProductSection
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.buyva.data.model.UiProduct
import com.example.buyva.features.favourite.viewmodel.FavouriteScreenViewModel
import com.example.buyva.features.search.viewmodel.SearchViewModel
import com.example.buyva.ui.theme.Cold
import com.example.buyva.utils.components.CustomAlertDialog
import com.example.buyva.utils.components.PriceFilterSlider

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    favouriteViewModel: FavouriteScreenViewModel,
    onProductClick: (String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val state by searchViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // ✅ Search header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            OutlinedTextField(
                value = state.searchText,
                onValueChange = { searchViewModel.updateSearchText(it) },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                placeholder = { Text("Search products") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        PriceFilterSlider(
            maxPrice = state.maxPrice,
            onPriceChange = { searchViewModel.updateMaxPrice(it) },
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Debug info
        Text(
            text = "First product: ${state.filteredProducts.firstOrNull()?.title ?: "None"}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray
        )

        LaunchedEffect(state.filteredProducts) {
            println("✅ Showing ${state.filteredProducts.size} filtered products")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Loading, error, or content
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.Black)
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error ?: "Search error",
                        color = Color.Red
                    )
                }
            }

            state.filteredProducts.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No products found",
                        color = Color.Gray
                    )
                }
            }

            else -> {
                // ✅ Show products
                UiProductSection(
                    products = state.filteredProducts,
                    onProductClick = onProductClick,
                    favouriteViewModel = favouriteViewModel
                )
            }
        }
    }
}
@Composable
fun UiProductSection(
    products: List<UiProduct>,
    onProductClick: (String) -> Unit,
    favouriteViewModel: FavouriteScreenViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val rows = products.chunked(2)

        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { product ->
                    key(product.id) {
                        UiProductCard(
                            product = product,
                            onProductClick = onProductClick,
                            modifier = Modifier.weight(1f),
                            favouriteViewModel = favouriteViewModel
                        )
                    }
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
@Composable
fun UiProductCard(
    product: UiProduct,
    modifier: Modifier = Modifier,
    onProductClick: (String) -> Unit,
    favouriteViewModel: FavouriteScreenViewModel
) {
    val favouriteProducts by favouriteViewModel.favouriteProducts.collectAsState()
    var showAlert by remember { mutableStateOf(false) }

    val isFavourite = favouriteProducts.any { it.id == product.id }

    Card(
        modifier = modifier
            .padding(vertical = 4.dp)
            .height(190.dp),
        onClick = { onProductClick(product.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = "Product Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
                    .height(90.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(6.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.title,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${product.price} EGP",
                    color = Cold,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(onClick = {
                    if (isFavourite) {
                        showAlert = true
                    } else {
                        favouriteViewModel.toggleFavourite(product.id)
                    }
                }) {
                    Icon(
                        imageVector = if (isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavourite) Color.Red else Color.Gray
                    )
                }

                if (showAlert) {
                    CustomAlertDialog(
                        title = "Confirm Removal",
                        message = "Are you sure you want to remove this product from favorites?",
                        onConfirm = {
                            favouriteViewModel.toggleFavourite(product.id)
                            showAlert = false
                        },
                        onDismiss = {
                            showAlert = false
                        },
                        confirmText = "Remove",
                        dismissText = "Cancel"
                    )
                }
            }
        }
    }
}

