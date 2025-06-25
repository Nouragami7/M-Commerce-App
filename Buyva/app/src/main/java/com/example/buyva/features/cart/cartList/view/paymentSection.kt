package com.example.buyva.features.cart.cartList.view

import android.app.Application
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buyva.data.datasource.remote.RemoteDataSourceImpl
import com.example.buyva.data.datasource.remote.graphql.ApolloService
import com.example.buyva.data.model.Address
import com.example.buyva.data.model.CartItem
import com.example.buyva.data.model.enums.PaymentMethod
import com.example.buyva.data.repository.adresses.AddressRepoImpl
import com.example.buyva.data.repository.cart.CartRepoImpl
import com.example.buyva.features.cart.cartList.viewmodel.CartViewModel
import com.example.buyva.features.cart.cartList.viewmodel.CartViewModelFactory
import com.example.buyva.features.cart.cartList.viewmodel.PaymentViewModel
import com.example.buyva.ui.theme.Cold
import com.example.buyva.utils.components.CustomAlertDialog
import com.example.buyva.utils.functions.createOrder
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl
import com.example.buyva.utils.sharedpreference.currency.CurrencyManager
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
    var showDialog by remember { mutableStateOf(false) }
    val application = LocalContext.current.applicationContext
    val cartRepo = CartRepoImpl(RemoteDataSourceImpl(ApolloService.client), SharedPreferenceImpl)
    val addressRepo = AddressRepoImpl(RemoteDataSourceImpl(ApolloService.client))
    val viewModel : CartViewModel = viewModel(
        factory = CartViewModelFactory(application as Application, cartRepo, addressRepo)
    )
    val context = LocalContext.current
    val addressDetails = if (address.lastName != "") {
        "${address.firstName} ${address.lastName}\n${address.address1}"
    } else {
        "Choose Default Address !"
    }

    if (showDialog) {
        CustomAlertDialog(
            title = "Error",
            message = "Please choose default address",
            onConfirm = {
                showDialog = false
                Toast.makeText(context, "Payment Cancelled", Toast.LENGTH_SHORT).show()
            },
            onDismiss = {
                showDialog = false
                Toast.makeText(context, "Payment Cancelled", Toast.LENGTH_SHORT).show()
            },
            confirmText = "OK",
            dismissText = "Cancel"
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = addressDetails,
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

                IconButton(onClick = onAddressClick) {
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

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Box {
                    OutlinedTextField(
                        value = voucherCode,
                        onValueChange = { voucherCode = it },
                        label = { Text("Voucher Code") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Cold,
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = Cold,
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Total: ${CurrencyManager.currencyUnit.value} %.2f".format(price* CurrencyManager.currencyRate.value),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Cold,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.height(50.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { /* Apply voucher */ },
                    modifier = Modifier
                        .defaultMinSize(minHeight = 30.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Cold)
                ) {
                    Text("Apply", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
        }

        Button(
            onClick = {
                if (addressDetails != "Choose Default Address !") {
                    if (selectedMethod == PaymentMethod.PayWithCard) {
                        onPayWithCardClick()
                    } else {
                        createOrder(cartItems, defaultAddress, paymentViewModel, context)
                        viewModel.clearCart()
                        Toast.makeText(context, "Payment Successful", Toast.LENGTH_SHORT).show()
                        onConfirm(LocalDateTime.now(), selectedMethod, voucherCode)
                    }
                } else {
                    onConfirm(LocalDateTime.now(), selectedMethod, voucherCode)
                    showDialog = true
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



