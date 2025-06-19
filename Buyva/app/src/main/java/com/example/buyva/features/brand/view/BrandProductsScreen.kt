package com.example.buyva.features.brand.view

import SearchBarWithCartIcon
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.example.buyva.navigation.navbar.NavigationBar
import com.example.buyva.ui.theme.ubuntuMedium
import com.example.buyva.utils.components.PriceFilterIcon
import com.example.buyva.utils.components.PriceFilterSlider
import com.example.buyva.utils.components.ScreenTitle


@Composable
fun BrandProductsScreen(
    brandName: String,
    imageRes: Int,
    onBack: () -> Unit,
    onProductClick: () -> Unit

) {
    var showSlider by remember { mutableStateOf(false) }
    var maxPrice by remember { mutableFloatStateOf(2522f) }

    LaunchedEffect(Unit) {
        NavigationBar.mutableNavBarState.value = false
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        ScreenTitle("Brand Products")
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

      //  ProductSection(products = allProducts, onProductClick)
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
