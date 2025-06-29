package com.example.buyva.features.cart.cartList.view

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.buyva.data.model.Address
import com.example.buyva.data.model.CartItem
import com.example.buyva.data.model.enums.PaymentMethod
import com.example.buyva.features.cart.cartList.viewmodel.CartViewModel
import com.example.buyva.features.cart.cartList.viewmodel.PaymentViewModel
import com.example.buyva.features.home.viewmodel.HomeViewModel
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.Teal
import com.example.buyva.utils.components.CustomAlertDialog
import com.example.buyva.utils.functions.createOrder
import com.example.buyva.utils.sharedpreference.currency.CurrencyManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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
    defaultAddress: Address?,
    coroutineScope: CoroutineScope
) {
    var selectedMethod by remember { mutableStateOf(PaymentMethod.CashOnDelivery) }
    var voucherCode by remember { mutableStateOf("") }
    val errorMessage by remember { mutableStateOf<String?>(null) }
    val snackBarHostState = remember { SnackbarHostState() }
    var showDialog by remember { mutableStateOf(false) }
    val voucherAfterDiscount = remember { mutableStateOf<String?>(null) }
    val errorMessageAfterDiscount = remember { mutableStateOf<String?>(null) }
    val priceAfterDiscount = remember { mutableStateOf<String?>(null) }
    var isApplied by remember { mutableStateOf(false) }


    val viewModel: CartViewModel = hiltViewModel()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val context = LocalContext.current
    val addressDetails = if (address.lastName != "") {
        "${address.firstName} ${address.lastName}\n${address.address1}"
    } else {
        "Choose Default Address !"
    }
    var discountedPrice by remember { mutableStateOf(price) }
    var voucherCodeprice by remember { mutableStateOf("") }
    val discountBanners by homeViewModel.discountBanners.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.fetchDiscounts()

    }
    Log.i("2", discountBanners.toString())

    if (showDialog) {
        CustomAlertDialog(title = "Error", message = "Please choose default address", onConfirm = {
            showDialog = false
            onAddressClick()

        }, onDismiss = {
            showDialog = false
            coroutineScope.launch {
                snackBarHostState.showSnackbar("Payment Cancelled")
            }
        }, confirmText = "Choose Address", dismissText = "Cancel"
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = addressDetails,
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
                val isCashDisabled = method == PaymentMethod.CashOnDelivery && discountedPrice > 1000

                OutlinedButton(
                    onClick = {
                        if (!isCashDisabled) {
                            selectedMethod = method
                        }
                    },
                    enabled = !isCashDisabled,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedMethod == method) Cold else Color.Transparent,
                        contentColor = if (selectedMethod == method) Color.White else Cold,
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.Gray
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                ) {
                    Text(method.displayName)
                }
            }

        }
        Spacer(modifier = Modifier.height(9.dp))

        if (discountedPrice > 1000) {
            Text(
                "Cash on Delivery is not available for orders above 1000 EGP",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(value = voucherCode,
                        onValueChange = { voucherCode = it },
                        label = { Text("Voucher Code") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(70.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Cold,
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = Cold,
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (discountBanners.isNotEmpty()) {
                                val trimmedUserInput = voucherCode.trim()
                                val matchedDiscount = discountBanners.find { banner ->
                                    val regex = Regex("Use Code: (\\w+)")
                                    val match = regex.find(banner.code)
                                    val extractedCode = match?.groups?.get(1)?.value?.trim() ?: ""
                                    extractedCode.equals(trimmedUserInput, ignoreCase = true)
                                }
                                voucherAfterDiscount.value = null
                                Log.i("2", "matchedDiscount: $matchedDiscount")
                                Log.i("2", "discountBanners: ${voucherAfterDiscount.value}")
                                if (matchedDiscount != null) {
                                    val discountValue = price * (matchedDiscount.percentage / 100f)
                                    discountedPrice = price - discountValue
                                    voucherAfterDiscount.value =
                                        "${matchedDiscount.percentage}% discount applied!"
                                    errorMessageAfterDiscount.value = null
                                    val currency = CurrencyManager.currencyUnit.value
                                    val percentage = matchedDiscount.percentage
                                    val formattedPrice =
                                        "%.2f".format(discountedPrice * CurrencyManager.currencyRate.value)
                                    priceAfterDiscount.value =
                                        "After Discount $percentage%: $currency $formattedPrice"
                                    isApplied = true

                                } else {
                                    discountedPrice = price

                                    voucherAfterDiscount.value = null
                                    errorMessageAfterDiscount.value = "Invalid voucher code"
                                    priceAfterDiscount.value = null
                                    isApplied = false
                                }
                            } else {
                                errorMessageAfterDiscount.value =
                                    "Loading discount codes... Please try again"
                            }
                        },

                        modifier = Modifier.height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = if (isApplied) ButtonDefaults.buttonColors(containerColor = Teal) else ButtonDefaults.buttonColors(containerColor = Cold)
                    ) {
                        Text("Apply", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (errorMessageAfterDiscount.value != null) {
                    Text(
                        text = errorMessageAfterDiscount.value ?: "",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Red,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = "Total: ${CurrencyManager.currencyUnit.value} %.2f".format(price * CurrencyManager.currencyRate.value),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Cold
                )

                if (priceAfterDiscount.value != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = priceAfterDiscount.value ?: "",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Cold
                    )
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
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar("Payment Successful")
                        }
                        onConfirm(LocalDateTime.now(), selectedMethod, voucherCode)
                    }
                } else {
                    // onConfirm(LocalDateTime.now(), selectedMethod, voucherCode)
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

        SnackbarHost(hostState = snackBarHostState, modifier = Modifier.padding(16.dp))
    }
}



