package com.example.buyva.features.home.view

import OfferBanner
import ProductSection
import SearchBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import com.example.buyva.navigation.navbar.NavigationBar
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.ubuntuMedium

@Composable
fun HomeScreen(){
    LaunchedEffect(Unit) {
        NavigationBar.mutableNavBarState.value = true
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp)) {

        Text(
            text = "BuyVa",
            style = MaterialTheme.typography.headlineSmall,
            color = Cold,
            fontFamily = ubuntuMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(12.dp))

        SearchBar()

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


                )
        )
        Spacer(modifier = Modifier.height(16.dp))

        ProductSection()

    }

}

@Preview(showBackground = true)
@Composable
fun PreviewBuyVaHomeScreen() {
        HomeScreen()

}

