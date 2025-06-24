package com.example.buyva.features.profile.addressdetails.viewlist
import android.app.Application
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.buyva.data.model.Address
import com.example.buyva.data.datasource.remote.RemoteDataSourceImpl
import com.example.buyva.data.datasource.remote.graphql.ApolloService
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.adresses.AddressRepoImpl
import com.example.buyva.features.profile.addressdetails.viewmodel.AddressViewModel
import com.example.buyva.features.profile.addressdetails.viewmodel.AddressViewModelFactory
import com.example.buyva.utils.components.LoadingIndicator
import androidx.compose.runtime.livedata.observeAsState


@Composable
fun DeliveryAddressListScreen(
    onBackClick: () -> Unit = {},
    onAddressClick: () -> Unit = {},
    onAddressDetailsClick: (String?, String?) -> Unit = { _ ,_-> },
) {
    val application = LocalContext.current.applicationContext as Application
    val viewModel: AddressViewModel = viewModel(
        factory = AddressViewModelFactory(
            application,
            AddressRepoImpl(RemoteDataSourceImpl(ApolloService.client))
        )
    )
    val addressState by viewModel.addresses.collectAsState()

    val addressList = when (addressState) {
        is ResponseState.Success<*> -> {
            (addressState as ResponseState.Success<List<Address>>).data
        }
        else -> emptyList()
    }
//    val navController = rememberNavController()
//    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
//    val shouldReload = currentBackStackEntry
//        ?.savedStateHandle
//        ?.getLiveData<Boolean>("ADDRESS_UPDATED")
//        ?.observeAsState()

 //   LaunchedEffect(shouldReload?.value) {
    LaunchedEffect(viewModel.addresses) {
//        if (shouldReload?.value == true) {
            viewModel.loadAddresses()
//            currentBackStackEntry
//                ?.savedStateHandle
//                ?.set("ADDRESS_UPDATED", false)
//        }
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




