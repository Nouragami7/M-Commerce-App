package com.example.buyva.features.profile.addressdetails.viewlist
import android.app.Application
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buyva.data.model.Address
import com.example.buyva.data.datasource.remote.RemoteDataSourceImpl
import com.example.buyva.data.datasource.remote.graphql.ApolloService
import com.example.buyva.data.datasource.remote.stripe.StripeClient
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.adresses.AddressRepoImpl
import com.example.buyva.features.profile.addressdetails.viewmodel.AddressViewModel
import com.example.buyva.features.profile.addressdetails.viewmodel.AddressViewModelFactory
import com.example.buyva.utils.components.LoadingIndicator
import com.example.buyva.utils.constants.DEFAULT_ADDRESS_ID
import com.example.buyva.utils.constants.USER_TOKEN
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl


@Composable
fun DeliveryAddressListScreen(
    onBackClick: () -> Unit = {},
    onAddressClick: () -> Unit = {},
    onAddressDetailsClick: (String?, String?) -> Unit = { _, _ -> },
) {
    val application = LocalContext.current.applicationContext as Application
    val viewModel: AddressViewModel = viewModel(
        factory = AddressViewModelFactory(
            application,
            AddressRepoImpl(RemoteDataSourceImpl(ApolloService.client, StripeClient.api))
        )
    )
    val addressState by viewModel.addresses.collectAsState()

    val addressList: List<Address> = when (addressState) {
        is ResponseState.Success<*> -> {
            (addressState as ResponseState.Success<List<Address>>).data
        }
        else -> emptyList()
    }
    val token = SharedPreferenceImpl.getFromSharedPreferenceInGeneral(USER_TOKEN)


    LaunchedEffect(Unit) {
        viewModel.loadAddresses()
    }

    Box(modifier = Modifier.fillMaxSize().padding(bottom = 60.dp)) {

        AddressList(
            addressList = addressList,
            onAddressClick = onAddressClick,
            onAddressDetailsClick = onAddressDetailsClick
        )

        when (addressState) {
            is ResponseState.Loading -> LoadingIndicator()
            is ResponseState.Failure -> {
                Text(
                    "Failed to load addresses",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> Unit
        }
    }
}




