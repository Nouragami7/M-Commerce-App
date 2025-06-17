package com.example.buyva.features.categories.view

import ProductSection
import SearchBarWithCartIcon
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.buyva.R
import com.example.buyva.data.model.Category
import com.example.buyva.data.model.Product
import com.example.buyva.ui.theme.Gray
import com.example.buyva.utils.components.PriceFilterIcon
import com.example.buyva.utils.components.PriceFilterSlider

@Composable
fun CategoryScreen() {
    var maxPrice by remember { mutableFloatStateOf(2522f) }
    var selectedCategory by remember { mutableStateOf("Men") }
    var showSlider by remember { mutableStateOf(false) }

    val categories = listOf(
        Category("Men", R.drawable.man),
        Category("Women", R.drawable.woman),
        Category("Kid", R.drawable.logo),
        Category("Sale", R.drawable.logo)
    )

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

    val filteredProducts = allProducts.filter {
        it.category == selectedCategory && it.price.removeSuffix(" EGP").toFloatOrNull()?.let { price ->
            price <= maxPrice
        } == true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            SearchBarWithCartIcon()
            Spacer(modifier = Modifier.height(12.dp))

            AnimatedVisibility(visible = showSlider) {
                PriceFilterSlider(
                    maxPrice = maxPrice,
                    onPriceChange = { maxPrice = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .fillMaxHeight()
                        .background(Gray)
                        .padding(vertical = 16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        categories.forEach { category ->
                            CategoryItem(
                                category = category,
                                isSelected = selectedCategory == category.name
                            ) {
                                selectedCategory = category.name
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }

                ProductSection(products = filteredProducts)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            PriceFilterIcon(
                onToggle = { showSlider = !showSlider }
            )
        }
    }
}

