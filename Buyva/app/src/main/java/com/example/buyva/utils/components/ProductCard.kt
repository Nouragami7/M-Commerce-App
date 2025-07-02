package com.example.buyva.utils.components

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.buyva.GetFavouriteProductsByIdsQuery
import com.example.buyva.GetProductsByCategoryQuery
import com.example.buyva.ProductsByCollectionQuery
import com.example.buyva.features.authentication.login.viewmodel.UserSessionManager
import com.example.buyva.features.favourite.viewmodel.FavouriteScreenViewModel
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.Sea
import com.example.buyva.utils.sharedpreference.currency.CurrencyManager

@Composable
fun ProductCard(
    product: Any,
    modifier: Modifier = Modifier,
    onProductClick: (String) -> Unit,
    favouriteViewModel: FavouriteScreenViewModel? = null
) {
    val favouriteProducts by favouriteViewModel?.favouriteProducts?.collectAsState()
        ?: remember { mutableStateOf(emptyList()) }
    var showAlert by remember { mutableStateOf(false) }
    var showGuestAlert by remember { mutableStateOf(false) }
    var guestActionType by remember { mutableStateOf("") }


    val id = when (product) {
        is BrandsAndProductsQuery.Node -> product.id
        is ProductsByCollectionQuery.Node -> product.id
        is GetProductsByCategoryQuery.Node -> product.id
        is GetFavouriteProductsByIdsQuery.OnProduct -> product.id
        else -> ""
    }

    val isFavourite = favouriteProducts.any { it.id == id }

    var imageUrl = ""
    var productTitle = " "
    var productType = " "
    var price = " "
    var currency = " "

    when (product) {
        is BrandsAndProductsQuery.Node -> {
            imageUrl = product.featuredImage?.url?.toString() ?: ""
            productTitle = product.title.split("|").take(2)
                .map { it.trim().split(" ").firstOrNull().orEmpty() }.joinToString(" | ")
            productType = product.productType
            price = product.variants.edges.firstOrNull()?.node?.price?.amount?.toString() ?: "0"
            currency = product.variants.edges.firstOrNull()?.node?.price?.currencyCode?.name ?: ""
        }


        is ProductsByCollectionQuery.Node -> {
            imageUrl = product.featuredImage?.url?.toString() ?: ""
            productTitle = product.title.split("|").take(2)
                .map { it.trim().split(" ").firstOrNull().orEmpty() }.joinToString(" | ")
            price = product.variants.edges.firstOrNull()?.node?.price?.amount?.toString() ?: "0"
            productType = "Shoes"
            currency = product.variants.edges.firstOrNull()?.node?.price?.currencyCode?.name ?: ""
        }

        is GetProductsByCategoryQuery.Node -> {
            imageUrl = product.images.edges.firstOrNull()?.node?.url?.toString() ?: ""
            productTitle = product.title.trim().split(" ").firstOrNull().orEmpty()
            productType = product.productType
            price = product.variants.edges.firstOrNull()?.node?.price?.amount.toString()
            currency = product.variants.edges.firstOrNull()?.node?.price?.currencyCode?.name ?: ""
        }

        is GetFavouriteProductsByIdsQuery.OnProduct -> {
            imageUrl = product.featuredImage?.url?.toString() ?: ""
            productTitle = product.title.split("|").take(2)
                .map { it.trim().split(" ").firstOrNull().orEmpty() }.joinToString(" | ")
            productType = product.productType
            price = product.variants.edges.firstOrNull()?.node?.price?.amount?.toString() ?: "0"
            currency = product.variants.edges.firstOrNull()?.node?.price?.currencyCode?.name ?: ""
        }
    }
    Log.i("1", "ProductCurrency: $currency")
    CurrencyManager.loadFromPreferences()
    val priceDouble = price.toDoubleOrNull() ?: 0.0
    val newPrice = CurrencyManager.convertPrice(priceDouble)
    Card(
        modifier = modifier
            .padding(vertical = 4.dp)
            .height(200.dp),
        onClick = {
            if (id.isNotEmpty()) {
                onProductClick(Uri.encode(id))
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
                contentDescription = "Image of product",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
                    .height(90.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(6.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = productTitle,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(1.dp))

            Text(
                text = productType,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 13.sp,
                modifier = Modifier.padding(start = 2.dp)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = newPrice,
                    color = Cold,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(onClick = {
                    if (UserSessionManager.isGuest()) {
                        guestActionType = "fav"
                        showGuestAlert = true
                        return@IconButton
                    }

                    if (isFavourite) {
                        showAlert = true
                    } else {
                        favouriteViewModel?.toggleFavourite(id)
                    }
                }) {
                    Icon(
                        imageVector = if (isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavourite) Color.Red else Color.Gray
                    )
                }


                if (showAlert) {
                    CustomAlertDialog(
                        title = "Confirm Removal",
                        message = "Are you sure you want to remove this product from favorites?",
                        onConfirm = {
                            favouriteViewModel?.toggleFavourite(id)
                            showAlert = false
                        },
                        onDismiss = {
                            showAlert = false
                        },
                        confirmText = "Remove",
                        dismissText = "Cancel"
                    )
                }
                if (showGuestAlert) {
                    AlertDialog(
                        onDismissRequest = { showGuestAlert = false },
                        title = { Text("Login Required") },
                        text = { Text("You need to login to add this product to your favourites.") },
                        confirmButton = {
                            TextButton(
                                onClick = { showGuestAlert = false },
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Sea
                                )
                            ) {
                                Text("OK")
                            }
                        }
                    )
                }

            }
        }
    }
}