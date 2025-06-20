package com.example.buyva.features.cart.cartList.view

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.buyva.data.model.Address
import com.example.buyva.data.model.enums.PaymentMethod
import com.example.buyva.ui.theme.Cold
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PaymentSection(
    address: Address,
    onConfirm: (LocalDateTime, PaymentMethod, String) -> Unit,
    onNavigateToCart: () -> Unit
) {

    var selectedMethod by remember { mutableStateOf(PaymentMethod.CashOnDelivery) }
    var voucherCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    Column(modifier = Modifier.padding(16.dp)) {

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = "${address.firstName} ${address.lastName}\n${address.address}",
            onValueChange = {},
            label = { Text("Default Address") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Cold,
                unfocusedBorderColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = Cold,
            ),
            trailingIcon = {
                IconButton(onClick = {

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
                    modifier = Modifier.weight(1f).padding(4.dp)
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
            Button(onClick = {
              //  snackbarHostState.showSnackbar("Voucher applied!")
            }, modifier =  Modifier.align(Alignment.CenterVertically).padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Cold)) {
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
                    Toast.makeText(context, "Payment Successful", Toast.LENGTH_SHORT).show()
                    onNavigateToCart()
                }else{
                    Toast.makeText(context, "Payment Successful", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Cold)
        ) {
            Text("Pay Now")
        }

        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.padding(16.dp))
    }
}


