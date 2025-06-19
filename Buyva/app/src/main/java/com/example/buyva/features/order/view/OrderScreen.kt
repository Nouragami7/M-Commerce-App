package com.example.yourapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.LottieAnimationView
import com.example.buyva.R
import com.example.buyva.data.model.CartItem
import com.example.buyva.features.order.view.OrderCard
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.ubuntuMedium
import com.example.buyva.navigation.navbar.NavigationBar
import com.example.buyva.utils.components.EmptyScreen
import com.example.buyva.utils.components.ScreenTitle


@Composable
fun OrderScreen(onBack: () -> Unit = {}, onOrderClick: (String) -> Unit = {}) {
    LaunchedEffect(Unit) {
        NavigationBar.mutableNavBarState.value = false
    }

    val orderItem = remember {
        mutableStateListOf(
            OrderItem("101654", "June 17, 2025", "$120", R.drawable.bag2),
            OrderItem("25454", "June 15, 2025", "$110", R.drawable.bag2),
            OrderItem("101654", "June 17, 2025", "$120", R.drawable.bag2)
        )
    }


    val activeOrders = emptyList<OrderItem>()
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
                fontWeight = FontWeight.Bold,
                color = Cold,
                fontFamily = ubuntuMedium,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (orderItem.isEmpty()) {
           EmptyScreen("No orders yet", R.raw.empty_order)
        }
        else{
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    OrderSection("Active Orders", orderItem, onOrderClick)
                }
            }
        }


    }
}

@Composable
fun OrderSection(title: String, orders: List<OrderItem>, onOrderClick: (String) -> Unit) {

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
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            orders.forEach { order ->
                OrderCard(order, onOrderClick = { onOrderClick(order.id) })
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }




data class OrderItem(
    val id: String,
    val date: String,
    val price: String,
    val imageRes: Int
)
