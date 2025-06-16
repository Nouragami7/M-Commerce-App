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


                )
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

       // ProductSection()

    }

}

@Preview(showBackground = true)
@Composable
fun PreviewBuyVaHomeScreen() {
        HomeScreen()

}

