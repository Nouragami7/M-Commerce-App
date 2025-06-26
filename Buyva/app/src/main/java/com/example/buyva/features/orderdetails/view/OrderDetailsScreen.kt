package com.example.buyva.features.orderdetails.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buyva.features.orderdetails.viewmodel.SharedOrderViewModel
import com.example.buyva.navigation.navbar.NavigationBar
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.Gray
import com.example.buyva.ui.theme.ubuntuMedium
import com.example.buyva.utils.sharedpreference.currency.CurrencyManager

@Composable
fun OrderDetailsScreen(
    sharedOrderViewModel: SharedOrderViewModel, onBack: () -> Unit = {}
) {

    val order by sharedOrderViewModel.selectedOrder.collectAsState()

    val subtotal = order?.subtotalPriceSet?.shopMoney?.amount.toString()
        .toDouble() * CurrencyManager.currencyRate.value
    val totalTax = order?.totalTaxSet?.shopMoney?.amount.toString()
        .toDouble() * CurrencyManager.currencyRate.value
    val totalPrice = order?.totalPriceSet?.shopMoney?.amount.toString()
        .toDouble() * CurrencyManager.currencyRate.value
    val currencyCode = CurrencyManager.currencyUnit.value

    val numberOfItems = order?.lineItems?.edges?.size
    val imageUrls = sharedOrderViewModel.extractImageUrlsFromNote(order?.note)
    Log.i("TAG", "OrderDetailsScreen: $imageUrls")

    LaunchedEffect(Unit) {
        NavigationBar.mutableNavBarState.value = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back"
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Order Details",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Cold,
                fontFamily = ubuntuMedium,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(2.dp))


        Text(
            text = "Thank you for buying from us!",
            fontSize = 26.sp,
            color = Cold,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            fontFamily = ubuntuMedium
        )
        Spacer(modifier = Modifier.height(16.dp))


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Text(
                text = "Order ID:",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Cold,
                fontFamily = ubuntuMedium
            )
            Spacer(modifier = Modifier.width(20.dp))
            order?.let {
                Text(
                    text = it.name,
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = "Order Items:",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Cold,
                fontFamily = ubuntuMedium
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(numberOfItems.toString() + " items", color = Color.DarkGray, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    text = "Ordered Items",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = ubuntuMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                val itemsList = order?.lineItems?.edges ?: emptyList()

                OrderItems(itemsList, imageUrls)

            }

        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Gray)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Home Address",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = ubuntuMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Call, contentDescription = "Phone", tint = Color.DarkGray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        order?.shippingAddress?.phone.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Place, contentDescription = "Location", tint = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        order?.shippingAddress?.address1.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }


            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 32.dp),
                thickness = 1.dp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))


            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = "Billing details",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = ubuntuMedium
                )
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Subtotal:", fontSize = 20.sp, fontFamily = ubuntuMedium)
                    Text(
                        text = "%.2f %s".format(subtotal, currencyCode), fontSize = 18.sp, fontFamily = ubuntuMedium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Tax:", fontSize = 20.sp, fontFamily = ubuntuMedium)
                    Text(
                        text = "%.2f %s".format(totalTax, currencyCode), fontSize = 18.sp, fontFamily = ubuntuMedium
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color.Gray)
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Total Cost:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        fontFamily = ubuntuMedium
                    )
                    Text(
                        text = "%.2f %s".format(totalPrice, currencyCode),
                        color = Cold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        fontFamily = ubuntuMedium
                    )
                }
            }
        }
    }

}

