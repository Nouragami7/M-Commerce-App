package com.example.buyva.features.home.view

import OfferBanner
import ProductSection
import SearchBarWithCartIcon
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.buyva.R
import com.example.buyva.data.model.Brand
import com.example.buyva.data.model.Category
import com.example.buyva.data.model.Product
import com.example.buyva.navigation.navbar.NavigationBar
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.ubuntuMedium

@Composable
fun HomeScreen(
    onCartClick: () -> Unit = {},
    onBrandClick: (String, Int) -> Unit = { _, _ -> }
){
    LaunchedEffect(Unit) {
        NavigationBar.mutableNavBarState.value = true
    }

    val allProducts = listOf(
        Product(1, "CONVERSE", "2000.00 EGP", R.drawable.logo, "Men", "CLASSIC"),
        Product(2, "VANS", "2100.00 EGP", R.drawable.logo, "Women", "CLASSIC"),
        Product(3, "VANS", "2384.00 EGP", R.drawable.logo, "Kid", "ERA 59"),
        Product(4, "VANS", "400.00 EGP", R.drawable.logo, "Sale", "APPAREL"),
        Product(5, "VANS", "1431.00 EGP", R.drawable.logo, "Men", "AUTHENTIC"),
        Product(6, "CONVERSE", "2000.00 EGP", R.drawable.logo, "Men", "CLASSIC"),
        Product(7, "VANS", "2100.00 EGP", R.drawable.logo, "Women", "CLASSIC"),
        Product(8, "VANS", "2384.00 EGP", R.drawable.logo, "Kid", "ERA 59"),
        Product(9, "VANS", "400.00 EGP", R.drawable.logo, "Sale", "APPAREL"),
        Product(10, "VANS", "1431.00 EGP", R.drawable.logo, "Men", "AUTHENTIC")
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(8.dp)) {

        Spacer(modifier = Modifier.height(32.dp))

        SearchBarWithCartIcon()

        Spacer(modifier = Modifier.height(16.dp))

        OfferBanner()

        Spacer(modifier = Modifier.height(16.dp))

        BrandSection(
            brands = listOf(
                Brand("Adidas", R.drawable.adidas),
                Brand("Adidas", R.drawable.adidas),
                Brand("Adidas", R.drawable.adidas),
                Brand("Adidas", R.drawable.adidas),
                Brand("Adidas", R.drawable.adidas),
                Brand("Adidas", R.drawable.adidas),
                Brand("Adidas", R.drawable.adidas),
                Brand("Adidas", R.drawable.adidas),
                ),
            onBrandClick = onBrandClick
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "For You",
            style = MaterialTheme.typography.headlineSmall,
            color = Cold,
            fontFamily = ubuntuMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        ProductSection(products = allProducts)

    }

}

@Preview(showBackground = true)
@Composable
fun PreviewBuyVaHomeScreen() {
        HomeScreen()

}

