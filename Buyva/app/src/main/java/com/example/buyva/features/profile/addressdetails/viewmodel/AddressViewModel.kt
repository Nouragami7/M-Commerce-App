package com.example.buyva.features.profile.addressdetails.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.buyva.data.model.Address
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.adresses.IAddressRepo
import com.example.buyva.utils.constants.USER_TOKEN
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AddressViewModel(application: Application,private val repo: IAddressRepo) : AndroidViewModel(
    application
) {

    private val _addresses = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val addresses: StateFlow<ResponseState> = _addresses
    val token = SharedPreferenceImpl.getFromSharedPreferenceInGeneral(USER_TOKEN)

    init {
        loadAddresses()
    }


    private fun loadAddresses() {
        viewModelScope.launch {
            if (!token.isNullOrEmpty()) {
                Log.i("1", "Loading addresses with token: $token")
                repo.getAddresses(token).collect {
                    Log.i("1", "Address response: $it")
                    _addresses.value = it
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
class AddressViewModelFactory(
    private val application: Application,
    private val addressRepo: IAddressRepo
) : ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddressViewModel::class.java)) {
            return AddressViewModel(application, addressRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}