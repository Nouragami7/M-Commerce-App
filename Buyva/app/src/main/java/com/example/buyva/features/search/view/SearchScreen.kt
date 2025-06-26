package com.example.buyva.features.search.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.example.buyva.R
import com.example.buyva.data.model.UiProduct
import com.example.buyva.features.favourite.viewmodel.FavouriteScreenViewModel
import com.example.buyva.features.search.viewmodel.SearchViewModel
import com.example.buyva.ui.theme.Cold
import com.example.buyva.utils.components.CustomAlertDialog
import com.example.buyva.utils.sharedpreference.currency.CurrencyManager
import com.airbnb.lottie.compose.*


@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    favouriteViewModel: FavouriteScreenViewModel?,
    onProductClick: (String) -> Unit = {},
    onBack: () -> Unit = {},
    brandFilter: String? = null,
    onCartClick: () -> Unit = {}
) {
    val state by searchViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    LaunchedEffect(Unit) {
        if (!brandFilter.isNullOrEmpty()) {
            searchViewModel.setSelectedBrand(brandFilter)
        } else {
            searchViewModel.loadAllProducts()
        }
    }

    CurrencyManager.loadFromPreferences()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Spacer(modifier = Modifier.width(8.dp))

            SearchBarWithCartIcon(
                searchText = state.searchText,
                onSearchTextChange = { searchViewModel.updateSearchText(it) },
                onSearchClick = {
                  //  searchViewModel.performSearch(state.searchText)
                },
                onCartClick = onCartClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        PriceFilterSlider(
            minPrice = state.minPrice,
            maxPrice = state.maxPrice,
            onPriceChange = { searchViewModel.updateSelectedPriceLimit(it) },
            currentValue = state.selectedPriceLimit,
            modifier = Modifier.padding(horizontal = 16.dp),
            currency = CurrencyManager.currencyUnit.value
        )


        Spacer(modifier = Modifier.height(16.dp))

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
                if (state.searchText.isNotEmpty()) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty_favorite))
                    val progress by animateLottieCompositionAsState(composition)

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier.size(200.dp)
                        )
                    }
                } else {
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
            }

            else -> {
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
fun SearchBarWithCartIcon(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onCartClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp)),
            placeholder = { Text("Search") },
            leadingIcon = {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedBorderColor = Cold,
                unfocusedBorderColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(onClick = onCartClick) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_shopping_cart_24),
                contentDescription = "Cart",
                tint = Cold,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun UiProductSection(
    products: List<UiProduct>,
    onProductClick: (String) -> Unit,
    favouriteViewModel: FavouriteScreenViewModel?
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
}@Composable
fun PriceFilterSlider(
    minPrice: Float?,
    maxPrice: Float,
    currentValue: Float,
    onPriceChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    currency: String
) {
    var sliderValue by remember { mutableFloatStateOf(currentValue) }

    LaunchedEffect(currentValue) {
        sliderValue = currentValue
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        if (minPrice != null) {
            Slider(
                value = sliderValue,
                onValueChange = {
                    sliderValue = it
                    onPriceChange(it)
                },
                valueRange = minPrice..maxPrice,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { scaleY = 0.6f },
                colors = SliderDefaults.colors(
                    thumbColor = Cold,
                    activeTrackColor = Cold,
                    inactiveTrackColor = Color.LightGray
                )
            )
        }

        Text(
            text = "Price: ${"%.2f".format(sliderValue)} $currency",
            modifier = Modifier.align(Alignment.End),
            fontSize = 12.sp,
            color = Color.DarkGray,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun UiProductCard(
    product: UiProduct,
    modifier: Modifier = Modifier,
    onProductClick: (String) -> Unit,
    favouriteViewModel: FavouriteScreenViewModel?
) {
    val favouriteProducts by favouriteViewModel?.favouriteProducts?.collectAsState()
        ?: remember { mutableStateOf(emptyList()) }
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

            val formattedTitle = product.title
                .split("|")
                .take(2)
                .map { it.trim().split(" ").firstOrNull().orEmpty() }
                .joinToString(" | ")

            Text(
                text = formattedTitle,
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
                    text = CurrencyManager.convertPrice(product.price.toDouble()),
                    color = Cold,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(onClick =  {
                    if (isFavourite) {
                        showAlert = true
                    } else {
                        favouriteViewModel?.toggleFavourite(product.id)
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
                            favouriteViewModel?.toggleFavourite(product.id)
                            showAlert = false
                        },
                        onDismiss = { showAlert = false },
                        confirmText = "Remove",
                        dismissText = "Cancel"
                    )
                }
            }
        }
    }
}
