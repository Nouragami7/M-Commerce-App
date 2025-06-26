package com.example.buyva.features.profile.addressdetails.viewlist

import android.app.Application
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.buyva.R
import com.example.buyva.data.datasource.remote.RemoteDataSourceImpl
import com.example.buyva.data.datasource.remote.graphql.ApolloService
import com.example.buyva.data.datasource.remote.stripe.StripeClient
import com.example.buyva.data.model.Address
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.adresses.AddressRepoImpl
import com.example.buyva.features.profile.addressdetails.viewmodel.AddressViewModel
import com.example.buyva.features.profile.addressdetails.viewmodel.AddressViewModelFactory
import com.example.buyva.ui.theme.Cold
import com.example.buyva.utils.components.ScreenTitle
import com.example.buyva.utils.constants.DEFAULT_ADDRESS_ID
import com.example.buyva.utils.constants.USER_TOKEN
import com.example.buyva.utils.extensions.stripTokenFromShopifyGid
import com.example.buyva.utils.jsonStringToAddress
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressList(
    viewModel: AddressViewModel,
    onAddressClick: () -> Unit,
    onAddressDetailsClick: (String?, String?) -> Unit,
    onBackClick: () -> Unit
) {
    val token = SharedPreferenceImpl.getFromSharedPreferenceInGeneral(USER_TOKEN)
    var defaultAddressId by remember { mutableStateOf("") }
    val addressState by viewModel.addresses.collectAsState()

    val addressList = when (addressState) {
        is ResponseState.Success<*> -> (addressState as ResponseState.Success<List<Address>>).data
        else -> emptyList()
    }

    LaunchedEffect(Unit) {
        defaultAddressId =
            SharedPreferenceImpl.getFromSharedPreferenceInGeneral("${DEFAULT_ADDRESS_ID}_$token") ?: ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Addresses", style = MaterialTheme.typography.titleLarge, color = Cold)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Cold)
                    }
                }
            )
        },
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
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.emptyaddress))
                LottieAnimation(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier.size(250.dp)
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

                    AddressItem(
                        address = address,
                        onAddressDetailsClick = { id, model -> onAddressDetailsClick(id, model) },
                        isDefault = isDefault,
                        onSetDefault = {
                            val cleanedId = address.id?.stripTokenFromShopifyGid() ?: return@AddressItem
                            viewModel.setDefaultAddress(cleanedId)
                            defaultAddressId = cleanedId
                        },
                        onDeleteClick = {
                            address.id?.let { viewModel.deleteAddress(it) }
                        }
                    )
                }
            }
        }
    }
}
