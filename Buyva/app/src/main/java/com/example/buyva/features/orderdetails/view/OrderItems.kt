package com.example.buyva.features.orderdetails.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.buyva.admin.GetOrdersByCustomerEmailQuery
import com.example.buyva.ui.theme.Teal

@Composable
fun OrderItems(
    itemsList: List<GetOrdersByCustomerEmailQuery.Edge1>,
    imageUrls: List<String>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(itemsList.size) { index ->
            val item = itemsList[index].node
            Card(
                modifier = Modifier.Companion
                    .width(200.dp)
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Teal)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    val imageUrl = imageUrls.getOrNull(index)
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = item.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .width(150.dp)
                            .padding(bottom = 8.dp)
                    )

                    Text(
                        text = item.title.split("|")
                            .take(2)
                            .map { it.trim().split(" ").firstOrNull().orEmpty() }
                            .joinToString(" | "),
                        fontSize = 18.sp, fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "x${item.quantity}",
                        fontSize = 16.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    item.originalUnitPriceSet.shopMoney.let { price ->
                        Text(
                            text = "${price.amount} ${price.currencyCode}",
                            fontSize = 16.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}