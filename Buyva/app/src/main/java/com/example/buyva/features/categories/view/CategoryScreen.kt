package com.example.buyva.features.categories.view

import ProductSection
import SearchBarWithCartIcon
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buyva.R
import com.example.buyva.data.model.Category
import com.example.buyva.data.model.Product
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.Gray

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
        Product(1, "CONVERSE", "", "2000.00 EGP", R.drawable.logo, "Men"),
        Product(2, "VANS", "CLASSIC", "2100.00 EGP", R.drawable.logo, "Women"),
        Product(3, "VANS", "ERA 59", "2384.00 EGP", R.drawable.logo, "Kid"),
        Product(4, "VANS", "APPAREL", "400.00 EGP", R.drawable.logo, "Sale"),
        Product(5, "VANS", "AUTHENTIC", "1431.00 EGP", R.drawable.logo, "Men"),
        Product(6, "CONVERSE", "", "2000.00 EGP", R.drawable.logo, "Men"),
        Product(7, "VANS", "CLASSIC", "2100.00 EGP", R.drawable.logo, "Women"),
        Product(8, "VANS", "ERA 59", "2384.00 EGP", R.drawable.logo, "Kid"),
        Product(9, "VANS", "APPAREL", "400.00 EGP", R.drawable.logo, "Sale"),
        Product(10, "VANS", "AUTHENTIC", "1431.00 EGP", R.drawable.logo, "Men")
    )

    val filteredProducts = allProducts.filter {
        it.category == selectedCategory && it.price.removeSuffix(" EGP").toFloatOrNull()?.let { price ->
            price <= maxPrice
        } == true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState())) {
            Spacer(modifier = Modifier.height(32.dp))
            SearchBarWithCartIcon()
            Spacer(modifier = Modifier.height(12.dp))

            AnimatedVisibility(visible = showSlider) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Slider(
                        value = maxPrice,
                        onValueChange = { maxPrice = it },
                        valueRange = 0f..3000f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer { scaleY = 0.7f }
                            .padding(horizontal = 12.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = Cold,
                            activeTrackColor = Cold,
                            inactiveTrackColor = Color.LightGray
                        )
                    )
                    Text(
                        text = "Max Price: ${maxPrice.toInt()} EGP",
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(end = 12.dp),
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }
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

        FloatingActionButton(
            onClick = { showSlider = !showSlider },
            containerColor = Cold,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp,bottom = 60.dp,end = 20.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_filter_icon),
                contentDescription = "Filter",
                tint = Color.White
            )
        }
    }
}

