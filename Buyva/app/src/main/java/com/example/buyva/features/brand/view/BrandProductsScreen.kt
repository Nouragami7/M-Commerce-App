package com.example.buyva.features.brand.view

import ProductSection
import SearchBarWithCartIcon
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.buyva.R
import com.example.buyva.data.model.Product
import com.example.buyva.navigation.navbar.NavigationBar
import com.example.buyva.ui.theme.ubuntuMedium
import com.example.buyva.utils.components.PriceFilterIcon
import com.example.buyva.utils.components.PriceFilterSlider


@Composable
fun BrandProductsScreen(
    brandName: String,
    imageRes: Int,
    onBack: () -> Unit
) {
    var showSlider by remember { mutableStateOf(false) }
    var maxPrice by remember { mutableFloatStateOf(2522f) }

    LaunchedEffect(Unit) {
        NavigationBar.mutableNavBarState.value = false
    }

    val allProducts = listOf(
        Product(1, "CONVERSE", "2000.00 EGP", R.drawable.logo, "Men", "CLASSIC"),
        Product(2, "VANS", "2100.00 EGP", R.drawable.logo, "Women", "CLASSIC"),
        Product(3, "VANS", "2384.00 EGP", R.drawable.logo, "Kid", "ERA 59"),
        Product(4, "VANS", "400.00 EGP", R.drawable.logo, "Sale", "APPAREL"),
        Product(5, "VANS", "1431.00 EGP", R.drawable.logo, "Men", "AUTHENTIC"),
        Product(6, "CONVERSE", "2000.00 EGP", R.drawable.logo, "Men", "CLASSIC")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 16.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = brandName,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = brandName,
                style = MaterialTheme.typography.titleLarge,
                fontFamily = ubuntuMedium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        SearchBarWithCartIcon()

        Spacer(modifier = Modifier.height(12.dp))

        AnimatedVisibility(visible = showSlider) {
            PriceFilterSlider(
                maxPrice = maxPrice,
                onPriceChange = { maxPrice = it },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        ProductSection(products = allProducts)
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
