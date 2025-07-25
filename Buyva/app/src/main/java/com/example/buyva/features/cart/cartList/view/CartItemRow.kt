package com.example.buyva.features.cart.cartList.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.buyva.data.model.CartItem
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.Gray
import com.example.buyva.ui.theme.Sea
import com.example.buyva.utils.sharedpreference.currency.CurrencyManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CartItemRow(item: CartItem,
                onQuantityChange: (Int) -> Unit,
                onNavigateToProductInfo: ( String,String,String) -> Unit,
                snackbarHostState: SnackbarHostState,
                scope: CoroutineScope
                ) {
    var availableQuantity by remember { mutableStateOf(item.quantityAvailable) }
    var quantity by remember { mutableStateOf(item.quantity) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Gray),
        onClick = {
            onNavigateToProductInfo(item.id, item.selectedOptions[0].value,item.selectedOptions[1].value)

        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = "Product Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Sea
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "%.2f ${CurrencyManager.currencyUnit.value} , ${item.selectedOptions?.get(0)?.name} ${item.selectedOptions?.get(0)?.value} ".format(CurrencyManager.currencyRate.value*(item.price.toDouble())),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = {
                    if (quantity > 1) {
                        quantity--
                        onQuantityChange(quantity)
                    }else{
                        scope.launch {
                            snackbarHostState.showSnackbar("Minimum quantity is 1")
                        }

                    }
                }) {
                    Icon(Icons.Filled.Remove, contentDescription = "Decrease", tint = Cold, modifier = Modifier.size(25.dp))
                }

                Text(
                    quantity.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
                IconButton(onClick = {
                    if (quantity < availableQuantity) {
                        quantity++
                        onQuantityChange(quantity)
                    }else{
                        scope.launch {
                            snackbarHostState.showSnackbar("Maximum quantity is $availableQuantity")
                        }
                    }
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Increase", tint = Cold, modifier = Modifier.size(25.dp))
                }

            }
        }
    }
}
