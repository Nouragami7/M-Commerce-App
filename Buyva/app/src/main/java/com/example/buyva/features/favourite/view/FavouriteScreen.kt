package com.example.buyva.features.favourite.view

import ProductSection
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.buyva.R
import com.example.buyva.features.favourite.viewmodel.FavouriteScreenViewModel
import com.example.buyva.navigation.navbar.NavigationBar
import com.example.buyva.utils.components.EmptyScreen
import com.example.buyva.utils.components.ScreenTitle

@Composable
fun FavouriteScreen(
    viewModel: FavouriteScreenViewModel?, navController: NavController
) {
    val favouriteProducts by viewModel?.favouriteProducts?.collectAsState() ?: remember {
        mutableStateOf(
            emptyList()
        )
    }
    LaunchedEffect(Unit) {
        NavigationBar.mutableNavBarState.value = true
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        item {
            ScreenTitle("Favourite")
        }

        if (favouriteProducts.isEmpty()) {
            item {
                EmptyScreen("No favourites yet", 28.sp, R.raw.empty_order)
            }
        } else {
            item {
                ProductSection(
                    products = favouriteProducts, onProductClick = { productId ->
                        navController.navigate("productInfo/$productId")
                    }, favouriteViewModel = viewModel
                )

            }
        }

        // Optional: Spacer for padding at bottom
        item {
            Spacer(modifier = Modifier.padding(bottom = 64.dp))
        }
    }
}
