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
import com.example.buyva.utils.components.ProductCard
@Composable

fun FavouriteScreen() {
    val favouriteProducts = remember {
        emptyList<Product>()

//        listOf(
//            Product(1, "CONVERSE", "2000.00 EGP", R.drawable.logo, "Men", "CLASSIC"),
//            Product(2, "VANS", "2100.00 EGP", R.drawable.logo, "Women", "CLASSIC")
//        )
    }

    if (favouriteProducts.isEmpty()) {
        EmptyFavouriteAnimation()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp)
        ) {
            ProductSection(products = favouriteProducts)
        }
    }
}

@Composable
fun EmptyFavouriteAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty_favorite))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(300.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "No favourites yet", color = Color.Gray)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFavouriteScreen() {
    FavouriteScreen()
}
