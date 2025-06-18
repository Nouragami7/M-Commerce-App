package com.example.buyva.features.profile.addresseslist.view
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import com.example.buyva.data.model.Address

import com.airbnb.lottie.compose.*
import com.example.buyva.R


val sampleAddresses = listOf(
    Address(
        firstName = "Ali",
        lastName = "Hassan",
        address = "123 Nile Street",
        country = "Egypt",
        city = "Cairo",
        floorNumber = "2",
        buildingNumber = "12A",
        phoneNumber = "01001234567"
    ),
    Address(
        firstName = "Sara",
        lastName = "Mahmoud",
        address = "45 El-Geish Avenue",
        country = "Egypt",
        city = "Alexandria",
        floorNumber = "3",
        buildingNumber = "5B",
        phoneNumber = "01234567890"
    )
)

@Composable
fun DeliveryAddressListScreen(
    onBackClick: () -> Unit = {},
    onAddressClick: () -> Unit = {},
    onAddressDetailsClick: (String?) -> Unit = { _ -> },
    initialAddresses: List<Address> = sampleAddresses // rename to avoid confusion
) {
    val addressList = remember { mutableStateListOf<Address>().apply { addAll(initialAddresses) } }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddressClick() },
                modifier = Modifier.offset(y = (-70).dp),
                containerColor = Color(0xFF006A71),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Address")
            }
        }
    ) { paddingValues ->

        if (addressList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(R.raw.emptyaddress)
                )
                LottieAnimation(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier.size(250.dp).padding(bottom = 16.dp)
                )
            }
        } else {
            LazyColumn(
                contentPadding = paddingValues,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            ) {
                items(addressList, key = { it.address }) { address ->
                    AddressItem(
                        address = address,
                        onAddressDetailsClick = {
                            onAddressDetailsClick(address.address)
                        },
                        onDeleteClick = {
                            addressList.remove(address)
                        }
                    )
                }
            }
        }
    }
}



