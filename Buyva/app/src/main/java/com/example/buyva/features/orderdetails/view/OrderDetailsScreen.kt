package com.example.buyva.features.orderdetails.view

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buyva.navigation.navbar.NavigationBar
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.Gray
import com.example.buyva.ui.theme.ubuntuMedium

@Composable
    fun OrderDetailsScreen(onBack: () -> Unit = {},     onProductClick: (String) -> Unit // ⬅️ ده تعديلنا
) {

    LaunchedEffect(Unit) {
        NavigationBar.mutableNavBarState.value = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F8FA))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
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
            Text(
                text = "#1090",
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
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
            Text("1 item", color = Color.DarkGray, fontSize = 20.sp)
        }

//        ProductCard(
//    product = product, // أو أي data class عندك
//    onClick = { onProductClick(product.id) } // ⬅️ product.id هو الـ ID اللي هتستخدمه في النفيجيشن
//)

        Spacer(modifier = Modifier.height(24.dp))

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
                    Text("+201126513889", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Place,
                        contentDescription = "Location",
                        tint = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("gamal abdel naser", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                }
            }


            Spacer(modifier = Modifier.height(24.dp))

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
                    Text("Subtotal:", fontSize = 20.sp)
                    Text("9000.00 EGP", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Tax:", fontSize = 20.sp)
                    Text("0.00 EGP", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Divider(color = Color.DarkGray)
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
                        "9000.00 EGP",
                        color = Cold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }


}