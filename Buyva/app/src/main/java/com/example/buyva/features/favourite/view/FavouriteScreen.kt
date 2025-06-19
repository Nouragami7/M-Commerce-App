package com.example.buyva.features.favourite.view

import ProductSection
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.*
import com.example.buyva.R
import com.example.buyva.data.model.Product
import com.example.buyva.utils.components.EmptyScreen
import com.example.buyva.utils.components.ProductCard
import com.example.buyva.utils.components.ScreenTitle

@Composable

fun FavouriteScreen() {
    val favouriteProducts = remember {
        emptyList<Product>()

//        listOf(
//            Product(1, "CONVERSE", "2000.00 EGP", R.drawable.logo, "Men", "CLASSIC"),
//            Product(2, "VANS", "2100.00 EGP", R.drawable.logo, "Women", "CLASSIC")
//        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        ScreenTitle("Favourite")

        if (favouriteProducts.isEmpty()) {
        EmptyScreen("No favourites yet", R.raw.empty_order)
    } else {
            ProductSection(products = favouriteProducts, onProductClick = {})
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewFavouriteScreen() {
    FavouriteScreen()
}
