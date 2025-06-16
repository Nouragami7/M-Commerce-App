package com.example.buyva.features.cart.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.material3.OutlinedTextField

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buyva.data.model.Address
import com.example.buyva.data.model.enums.PaymentMethod
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.Teal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PaymentSection(
    currentDate: LocalDate = LocalDate.now(),
    currentTime: LocalTime = LocalTime.now(),
    address: Address,
    onConfirm: (LocalDateTime, PaymentMethod, String) -> Unit
) {
    var selectedDate by remember { mutableStateOf(currentDate) }
    var selectedTime by remember { mutableStateOf(currentTime.plusMinutes(5)) }
    var selectedMethod by remember { mutableStateOf(PaymentMethod.CashOnDelivery) }
    var voucherCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    Column(modifier = Modifier.padding(16.dp)) {

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = "${address.firstName} ${address.lastName}",
            onValueChange = {},
            label = { Text("Default Name") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = address.address,
            onValueChange = {},
            label = { Text("Default Address") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
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
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
              //  snackbarHostState.showSnackbar("Voucher applied!")
            }, colors = ButtonDefaults.buttonColors(containerColor = Cold)) {
                Text("Apply")
            }
        }

        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
        }

        Button(
            onClick = {
                if (selectedDate == currentDate && selectedTime.isBefore(currentTime)) {
                    errorMessage = "Please choose a future time"
                } else {
                    val dateTime = LocalDateTime.of(selectedDate, selectedTime)
                    onConfirm(dateTime, selectedMethod, voucherCode)
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


