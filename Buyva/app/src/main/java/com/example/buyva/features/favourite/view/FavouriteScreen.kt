package com.example.buyva.features.favourite.view

import ProductSection
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.buyva.R
import com.example.buyva.features.favourite.viewmodel.FavouriteScreenViewModel
import com.example.buyva.data.repository.favourite.FavouriteRepositoryImpl
import com.example.buyva.utils.components.EmptyScreen
import com.example.buyva.utils.components.ScreenTitle
@Composable
fun FavouriteScreen(
    viewModel: FavouriteScreenViewModel, // ✅ استقباله كـ parameter
    navController: NavController
) {
    val favouriteProducts by viewModel.favouriteProducts.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ScreenTitle("Favourite")

        if (favouriteProducts.isEmpty()) {
            EmptyScreen("No favourites yet", 28.sp, R.raw.empty_order)
        } else {
            ProductSection(
                products = favouriteProducts,
                onProductClick = { productId ->
                    navController.navigate("productInfo/$productId")
                }
            )
        }
    }
}
