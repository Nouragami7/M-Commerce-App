package com.example.buyva.features.cart.cartList.view

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.buyva.data.model.Address
import com.example.buyva.data.model.CartItem
import com.example.buyva.data.model.enums.PaymentMethod
import com.example.buyva.features.cart.cartList.viewmodel.PaymentViewModel
import com.example.buyva.ui.theme.Cold
import com.example.buyva.utils.functions.createOrder
import java.time.LocalDateTime


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PaymentSection(
    price: Double,
    address: Address,
    onConfirm: (LocalDateTime, PaymentMethod, String) -> Unit,
    onPayWithCardClick: () -> Unit,
    onAddressClick: () -> Unit,
    paymentViewModel: PaymentViewModel,
    cartItems: SnapshotStateList<CartItem>,
    defaultAddress: Address?
) {

    var selectedMethod by remember { mutableStateOf(PaymentMethod.CashOnDelivery) }
    var voucherCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }


    Column(modifier = Modifier.padding(16.dp)) {

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = "${address.firstName} ${address.lastName}\n${address.address1}",
            onValueChange = {},
            label = { Text("Default Address") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Cold,
                unfocusedBorderColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = Cold,
            ),
            trailingIcon = {
                IconButton(onClick = {
                    onAddressClick()
                }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
            },
            singleLine = false,
            maxLines = 2
        )


        Spacer(modifier = Modifier.height(16.dp))

        Text("Payment Method", fontWeight = FontWeight.Bold, color = Cold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            PaymentMethod.entries.forEach { method ->
                OutlinedButton(
                    onClick = { selectedMethod = method },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedMethod == method) Cold else Color.Transparent,
                        contentColor = if (selectedMethod == method) Color.White else Cold
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                ) {
                    Text(method.displayName)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = voucherCode,
                onValueChange = { voucherCode = it },
                label = { Text("Voucher Code") },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Cold,
                    unfocusedBorderColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedLabelColor = Cold,
                ),

                )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    //  snackbarHostState.showSnackbar("Voucher applied!")
                },
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Cold)
            ) {
                Text("Apply")
            }

        }

        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
        }

        val context = LocalContext.current

        Button(
            onClick = {
                if (selectedMethod == PaymentMethod.PayWithCard) {
                    onPayWithCardClick()
                } else {
                    Toast.makeText(context, "Payment Successful", Toast.LENGTH_SHORT).show()
                    onConfirm(LocalDateTime.now(), selectedMethod, voucherCode)
                    createOrder(cartItems, defaultAddress, paymentViewModel, context)

                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Cold)
        ) {
            Text("Checkout")
        }

        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.padding(16.dp))
    }
}


