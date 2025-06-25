package com.example.buyva.features.profile.addressdetails.viewlist

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.buyva.R
import com.example.buyva.data.model.Address
import com.example.buyva.features.profile.addressdetails.viewmodel.AddressViewModel
import com.example.buyva.utils.constants.DEFAULT_ADDRESS_ID
import com.example.buyva.utils.constants.USER_TOKEN
import com.example.buyva.utils.extensions.stripTokenFromShopifyGid
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl

@Composable
fun AddressList(
    addressList: List<Address>,
    onAddressClick: () -> Unit,
    onAddressDetailsClick: (String?, String?) -> Unit
){
    val token = SharedPreferenceImpl.getFromSharedPreferenceInGeneral(USER_TOKEN)
    var defaultAddressId by remember { mutableStateOf("") }

    val viewModel: AddressViewModel = hiltViewModel()
    
    LaunchedEffect(Unit) {
        defaultAddressId = SharedPreferenceImpl.getFromSharedPreferenceInGeneral("${DEFAULT_ADDRESS_ID}_$token") ?: ""
       // viewModel.loadAddresses()
    }

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
                    modifier = Modifier
                        .size(250.dp)
                        .padding(bottom = 16.dp)
                )
            }
        } else {
            LazyColumn(
                contentPadding = paddingValues,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            ) {
                items(addressList) { address ->
                    val isDefault = address.id?.stripTokenFromShopifyGid() == defaultAddressId
Log.d("1", "AddressList called ${address.country}  and ${address.city}")
                    AddressItem(
                        address = address,
                        onAddressDetailsClick = { address1, addressModel ->
                            onAddressDetailsClick(address1, addressModel)},
                        isDefault = isDefault,
                        onSetDefault = {
                            val cleanedId = address.id?.stripTokenFromShopifyGid() ?: return@AddressItem
                            SharedPreferenceImpl.saveToSharedPreferenceInGeneral("${DEFAULT_ADDRESS_ID}_$token", cleanedId)
                            defaultAddressId = cleanedId
                        },
                        onDeleteClick = {
                            address.id?.let { viewModel.deleteAddress(it) }
                            addressList.toMutableList().remove(address)

                        }
                    )
                }
            }
        }
    }
}

