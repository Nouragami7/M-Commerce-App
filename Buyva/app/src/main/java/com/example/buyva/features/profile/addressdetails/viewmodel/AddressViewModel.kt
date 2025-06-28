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
import com.example.buyva.utils.constants.DEFAULT_ADDRESS_ID
import com.example.buyva.utils.constants.USER_TOKEN
import com.example.buyva.utils.extensions.stripTokenFromShopifyGid
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel@Inject constructor(application: Application, private val repo: IAddressRepo) : AndroidViewModel(
    application
) {
    private val _saveAddressState = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val saveAddressState: StateFlow<ResponseState> = _saveAddressState


    private var _addresses = MutableStateFlow<ResponseState>(ResponseState.Loading)
    var addresses: StateFlow<ResponseState> = _addresses
    val token = SharedPreferenceImpl.getFromSharedPreferenceInGeneral(USER_TOKEN)


    fun loadAddresses() {
        viewModelScope.launch {
            if (!token.isNullOrEmpty()) {
                repo.getAddresses(token).collect { response ->
                    _addresses.value = response

                    if (response is ResponseState.Success<*>) {
                        val addressList = response.data as? List<Address>
                        if (addressList?.size == 1) {
                            val defaultKey = "${DEFAULT_ADDRESS_ID}_$token"
                            val alreadySaved =
                                SharedPreferenceImpl.getFromSharedPreferenceInGeneral("${DEFAULT_ADDRESS_ID}_$token")
                            if (alreadySaved.isNullOrEmpty()) {
                                val onlyAddress = addressList.first()
                                val cleanedId = onlyAddress.id?.stripTokenFromShopifyGid()
                                if (cleanedId != null) {
                                    SharedPreferenceImpl.saveToSharedPreferenceInGeneral("${DEFAULT_ADDRESS_ID}_$token", cleanedId)
                                }
                            }
                        }
                    }
                }
            } else {
                _addresses.value = ResponseState.Failure(Throwable("User token is missing"))
            }
        }
    }



    fun saveAddress(address: Address) {
        viewModelScope.launch {
            if (token != null) {
                _saveAddressState.value = ResponseState.Loading

                val flow = if (address.id != null) {
                    repo.updateAddress(address, token)
                } else {
                    repo.createAddress(address, token)
                }

                flow.collect {
                    _saveAddressState.value = it
                    if (it is ResponseState.Success<*>) {
                        loadAddresses()
                    }
                }
            }
        }
    }
    fun resetSaveAddressState() {
        _saveAddressState.value = ResponseState.Loading
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
//class AddressViewModelFactory(
//    private val application: Application,
//    private val addressRepo: IAddressRepo
//) : ViewModelProvider.AndroidViewModelFactory(application) {
//
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(AddressViewModel::class.java)) {
//            return AddressViewModel(application, addressRepo) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}