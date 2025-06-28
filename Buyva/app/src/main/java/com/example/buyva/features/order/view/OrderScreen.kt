package com.example.yourapp.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.buyva.R
import com.example.buyva.admin.GetOrdersByCustomerEmailQuery
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.features.order.view.OrderCard
import com.example.buyva.features.order.viewmodel.OrderViewModel
import com.example.buyva.navigation.navbar.NavigationBar
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.ubuntuMedium
import com.example.buyva.utils.components.EmptyScreen
import com.example.buyva.utils.components.LoadingIndicator
import com.google.firebase.auth.FirebaseAuth


@Composable
fun OrderScreen(onBack: () -> Unit = {}, onOrderClick: (GetOrdersByCustomerEmailQuery.Node?) -> Unit ) {
    LaunchedEffect(Unit) {
        NavigationBar.mutableNavBarState.value = false
    }

    val orderViewModel: OrderViewModel = hiltViewModel()

    val orderState by orderViewModel.orders.collectAsStateWithLifecycle()

    val user = FirebaseAuth.getInstance().currentUser
    val email = user?.email

    LaunchedEffect(Unit) {
        if (email != null) {
            orderViewModel.getOrders(email)
            Log.i("order", "OrderScreen: $email")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 16.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back"
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

        when (val state = orderState) {
            is ResponseState.Failure -> {
                Text(text = state.message.toString())
            }

            ResponseState.Loading -> LoadingIndicator()
            is ResponseState.Success<*> -> {
                val response = state.data as GetOrdersByCustomerEmailQuery.Data
                val orders = response.orders.edges.map { it.node }
                if(orders.isEmpty()) {
                    EmptyScreen("No orders yet",22.sp, R.raw.empty_order)

                }else{
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        OrderSection("Your Orders", orders, onOrderClick)
                    }
                }
                }
            }
        }


    }

}

@Composable
fun OrderSection(
    title: String, orders: List<GetOrdersByCustomerEmailQuery.Node>,
    onOrderClick: (GetOrdersByCustomerEmailQuery.Node?) -> Unit
) {

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
            OrderCard(order,onOrderClick)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}





