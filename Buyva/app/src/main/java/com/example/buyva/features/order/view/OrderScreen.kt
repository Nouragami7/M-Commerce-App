package com.example.yourapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buyva.R
import com.example.buyva.features.order.view.OrderCard
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.ubuntuMedium

@Composable
fun OrderScreen(onBack: () -> Unit = {}) {
    val activeOrders = listOf(
        OrderItem("101654", "June 17, 2025", "$120", R.drawable.bag2),
        OrderItem("25454", "June 15, 2025", "$110", R.drawable.bag2),
        OrderItem("25454", "June 15, 2025", "$110", R.drawable.bag2),
        OrderItem("25454", "June 15, 2025", "$110", R.drawable.bag2),
    )
    val pastOrders = listOf(
        OrderItem("1001", "June 17, 2025", "$99", R.drawable.bag2),
        OrderItem("2521", "June 15, 2025", "$115", R.drawable.bag2),
        OrderItem("2545", "June 15, 2025", "$100", R.drawable.bag2)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "Orders",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                OrderSection("Active Orders", activeOrders)
                Spacer(modifier = Modifier.height(16.dp))
                OrderSection("Past Orders", pastOrders)
            }
        }
    }
}

@Composable
fun OrderSection(title: String, orders: List<OrderItem>) {

        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Cold,
                    fontFamily = ubuntuMedium,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            orders.forEach { order ->
                OrderCard(order)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }




data class OrderItem(
    val id: String,
    val date: String,
    val price: String,
    val imageRes: Int
)
