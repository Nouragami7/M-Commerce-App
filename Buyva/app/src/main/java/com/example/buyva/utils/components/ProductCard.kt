package com.example.buyva.utils.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.buyva.BrandsAndProductsQuery
import com.example.buyva.GetProductsByCategoryQuery
import com.example.buyva.ProductsByCollectionQuery
import com.example.buyva.ui.theme.Cold

@Composable
fun ProductCard(
    product: Any, modifier: Modifier = Modifier, onProductClick: (String) -> Unit
) {
    var isFav by remember { mutableStateOf(false) }

    var id = ""
    var imageUrl = ""
    var productTitle = " "
    var productType = " "
    var price = " "
    var currency = " "

    when (product) {
        is BrandsAndProductsQuery.Node -> {
            id = product.id
            imageUrl = product.featuredImage?.url?.toString() ?: ""
            productTitle = product.title
            productType = product.productType
            price = product.variants.edges.firstOrNull()?.node?.price?.amount.toString()
            currency = product.variants.edges.firstOrNull()?.node?.price?.currencyCode?.name ?: ""
        }

        is ProductsByCollectionQuery.Node -> {
            id = product.id
            println("product id is +++++++++++++++ $id")
            imageUrl = product.featuredImage?.url?.toString() ?: ""
            productTitle = product.title
            price = product.variants.edges.firstOrNull()?.node?.price?.amount.toString()
            currency = product.variants.edges.firstOrNull()?.node?.price?.currencyCode?.name ?: ""
        }

        is GetProductsByCategoryQuery.Node -> {
            id = product.id
            imageUrl = product.images.edges.firstOrNull()?.node?.url?.toString() ?: ""
            productTitle = product.title
            productType = product.productType
            price = product.variants.edges.firstOrNull()?.node?.price?.amount.toString()
            currency = product.variants.edges.firstOrNull()?.node?.price?.currencyCode?.name ?: ""

        }
    }

    Card(
        modifier = modifier
            .padding(vertical = 4.dp)
            .height(190.dp),
        onClick = {
            if (id.isNotEmpty()) {
                onProductClick(Uri.encode(id))
            } else {
                println("Cannot navigate")
            }
        },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = productTitle,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(1.dp))

            Text(
                text = productType,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 2.dp)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$price $currency",
                    color = Cold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(onClick = { isFav = !isFav }) {
                    Icon(
                        imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFav) Color.Red else Color.Gray
                    )
                }
            }
        }
    }
}
