package com.example.buyva.features.profile.addressdetails.viewlist
import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.buyva.data.model.Address
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.features.profile.addressdetails.viewmodel.AddressViewModel
import com.example.buyva.utils.components.LoadingIndicator
import com.example.buyva.utils.constants.USER_TOKEN
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl


@Composable
fun DeliveryAddressListScreen(
    onBackClick: () -> Unit = {},
    onAddressClick: () -> Unit = {},
    onAddressDetailsClick: (String?, String?) -> Unit = { _, _ -> },
) {
    val application = LocalContext.current.applicationContext as Application
    val viewModel: AddressViewModel = hiltViewModel()
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

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 60.dp)) {



        when (addressState) {
            is ResponseState.Loading -> LoadingIndicator()
            is ResponseState.Failure -> {
                Text(
                    "Failed to load addresses",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                AddressList(
                    onAddressClick = onAddressClick,
                    onAddressDetailsClick = onAddressDetailsClick,
                    onBackClick = onBackClick
                )
            }
        }
    }
}




