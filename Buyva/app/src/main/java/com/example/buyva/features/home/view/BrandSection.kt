package com.example.buyva.features.home.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.buyva.BrandsAndProductsQuery
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.ubuntuMedium

@Composable
fun BrandSection(brands: List<BrandsAndProductsQuery.Node3>, onBrandClick: (String,String, String) -> Unit) {
    Column {
        Text(
            text = "Brands",
            style = MaterialTheme.typography.headlineSmall,
            color = Cold,
            fontFamily = ubuntuMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            items(brands) { brand ->
                val id = brand.id
                val title = brand.title
                val imageUrl = brand.image?.url?.toString() ?: ""
                BrandCard(
                    id = id,
                    title = title,
                    imageUrl = imageUrl,
                    onClick = {
                        onBrandClick(id,title, imageUrl)
                    }
                )
            }
        }
    }
}



@Composable
fun BrandCard(id: String, title: String, imageUrl: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(12.dp)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontFamily = ubuntuMedium,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
