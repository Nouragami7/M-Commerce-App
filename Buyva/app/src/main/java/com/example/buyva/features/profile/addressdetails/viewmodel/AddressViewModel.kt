package com.example.buyva.features.profile.addressdetails.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.buyva.data.model.Address
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.adresses.IAddressRepo
import com.example.buyva.utils.constants.DEFAULT_ADDRESS_ID
import com.example.buyva.utils.constants.USER_TOKEN
import com.example.buyva.utils.extensions.stripTokenFromShopifyGid
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel@Inject constructor(application: Application, private val repo: IAddressRepo) : AndroidViewModel(
    application
) {

    private var _addresses = MutableStateFlow<ResponseState>(ResponseState.Loading)
    var addresses: StateFlow<ResponseState> = _addresses
    val token = SharedPreferenceImpl.getFromSharedPreferenceInGeneral(USER_TOKEN)


    fun loadAddresses() {
        viewModelScope.launch {
            if (!token.isNullOrEmpty()) {
                Log.i("1", "Loading addresses with token: $token")
                repo.getAddresses(token).collect { response ->
                    Log.i("1", "Address response: $response")
                    _addresses.value = response

                    if (response is ResponseState.Success<*>) {
                        Log.i("1", "Address response data: ${response.data}")
                        val addressList = response.data as? List<Address>
                        if (addressList?.size == 1) {
                            Log.i("1", "Only one address found")
                            val defaultKey = "${DEFAULT_ADDRESS_ID}_$token"
                            val alreadySaved =
                                SharedPreferenceImpl.getFromSharedPreferenceInGeneral("${DEFAULT_ADDRESS_ID}_$token")
                            Log.i("1", "Already saved: $alreadySaved")
                            if (alreadySaved.isNullOrEmpty()) {
                                Log.i("1", "Saving as default")
                                val onlyAddress = addressList.first()
                                val cleanedId = onlyAddress.id?.stripTokenFromShopifyGid()
                                Log.i("1", "Cleaned ID: $cleanedId")
                                if (cleanedId != null) {
                                    SharedPreferenceImpl.saveToSharedPreferenceInGeneral("${DEFAULT_ADDRESS_ID}_$token", cleanedId)
                                }
                                Log.d("1", "Only one address found. Saved as default: ${onlyAddress.id}")
                            }
                        }
                    }
                }
            } else {
                Log.e("1", "Token is null or empty")
                _addresses.value = ResponseState.Failure(Throwable("User token is missing"))
            }
        }
    }


    fun addAddress( address: Address) {

        Log.d("1", "Adding address: $address")
        Log.d("1", "token : $token")

        viewModelScope.launch {
            if (token != null) {
                repo.createAddress( address,token).collect {
                    if (it is ResponseState.Success<*>) {
                        Log.d("1", "Address added successfully")
                        loadAddresses()
                    }
                }
            }
        }
    }
    fun saveAddress(address: Address) {
        viewModelScope.launch {
            if (token != null) {
                if (address.id != null) {
                    repo.updateAddress(address, token).collect {
                        when (it) {
                            is ResponseState.Loading -> {

                            }
                            is ResponseState.Success<*> -> {
                                Log.d("1", "Updated address successfully")
                                loadAddresses()
                            }
                            is ResponseState.Failure -> {
                                Log.e("1", "Update failed: ${it.message}")
                            }
                        }
                    }
                } else {
                    repo.createAddress(address, token).collect {
                        when (it) {
                            is ResponseState.Loading -> { /* show loading */ }
                            is ResponseState.Success<*> -> {
                                Log.d("1", "Created new address")
                                loadAddresses()
                            }
                            is ResponseState.Failure-> {
                                Log.e("1", "Create failed: ${it.message}")
                            }
                        }
                    }
                }
            }
        }
    }


    fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            if (!token.isNullOrEmpty()) {
                repo.deleteCustomerAddress(addressId, token).collect {
                    if (it is ResponseState.Success<*>) {
                        loadAddresses()
                    }
                }
            }
        }
    }
}
