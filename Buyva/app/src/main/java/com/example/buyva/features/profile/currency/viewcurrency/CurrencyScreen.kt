package com.example.buyva.features.profile.currency.viewcurrency

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.example.buyva.features.profile.currency.viewmodel.CurrencyViewModel
import com.example.buyva.ui.theme.Cold
import com.example.buyva.utils.components.ScreenTitle
import com.example.buyva.utils.sharedpreference.currency.CurrencyManager
import kotlinx.coroutines.launch

@Composable
fun CurrencyScreen(viewModel: CurrencyViewModel, onBackClick: () -> Unit = {}) {
    val currencyRates by viewModel.currencyRates.collectAsState()
    val selectedCurrency by viewModel.currencyResponse.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    val currencyList = currencyRates.keys.sorted()

    LaunchedEffect(Unit) {
        viewModel.fetchCurrencyRates()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ScreenTitle("Currency")

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Select Your Currency",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Cold,
                modifier = Modifier.weight(1f)
            )


        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedCurrency,
                onValueChange = {},
                readOnly = true,
                label = { Text("Currency") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Cold,
                    unfocusedBorderColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedLabelColor = Cold,
                ),
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                currencyList.forEach { code ->
                    DropdownMenuItem(
                        text = {
                            val rate = currencyRates[code]
                            Text("$code - Rate: ${rate?.toString() ?: "--"}")
                        },
                        onClick = {
                         //   CurrencyManager.updateCurrency(currencyRates[code]?.toDouble() ?: 1.0,code)

                          viewModel.setCurrencyUnit(code)
                          viewModel.setCurrencyRate(currencyRates[code]?.toDouble() ?: 1.0)
                           expanded = false
                      }
                  )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        val selectedRate = currencyRates[selectedCurrency]?.toString() ?: "--"
        Text(
            text = "Selected Rate: $selectedRate",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF00897B)
        )
    }
}

