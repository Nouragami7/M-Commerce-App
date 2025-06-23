package com.example.buyva.features.cart.cartList.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.buyva.GetCartDetailsQuery
import com.example.buyva.data.model.Address
import com.example.buyva.data.model.CartItem
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.AuthRepository
import com.example.buyva.data.repository.adresses.IAddressRepo
import com.example.buyva.data.repository.cart.CartRepo
import com.example.buyva.data.repository.home.IHomeRepository
import com.example.buyva.features.profile.addressdetails.viewmodel.AddressViewModel
import com.example.buyva.utils.constants.DEFAULT_ADDRESS_ID
import com.example.buyva.utils.constants.USER_TOKEN
import com.example.buyva.utils.extensions.stripTokenFromShopifyGid
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel(
    application: Application,
    private val cartRepo: CartRepo,
    private val addressRepo: IAddressRepo
) : AndroidViewModel(application) {

    private val _cartProducts = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val cartProducts: StateFlow<ResponseState> = _cartProducts
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
    val cartId = SharedPreferenceImpl.getFromSharedPreferenceInGeneral("CART_ID_${userEmail.lowercase()}")
   private var cartLinesId = MutableStateFlow<List<String>>(emptyList())
    private val _defaultAddress = MutableStateFlow<Address?>(null)
    val defaultAddress: StateFlow<Address?> = _defaultAddress

    private val token = SharedPreferenceImpl.getFromSharedPreferenceInGeneral(USER_TOKEN)
    private val defaultAddressId = SharedPreferenceImpl.getFromSharedPreferenceInGeneral("${DEFAULT_ADDRESS_ID}_$token")


    init {
        showCart()
    }

    fun getCartDetails() {
        viewModelScope.launch {
            cartRepo.getCartProducts(cartId ?: "No cart Id Found!").collect { response ->
                _cartProducts.value = response
                Log.i("1", "getCartDetails: $response")
                if (response is ResponseState.Success<*>) {
                     cartLinesId = ((response.data as GetCartDetailsQuery.Data).cart?.lines?.edges
                         ?.map { edge ->
                             edge.node.id
                         } ?: emptyList()) as MutableStateFlow<List<String>>
                        }
                }
            }
        }


    fun showCart() {
        viewModelScope.launch {
            cartRepo.getCartProductList(cartId ?: "No cart Id Found!")
                .collect { response ->
                    Log.i("1", "showCart: $response")
                    _cartProducts.value = response
                }
        }
    }

    fun removeProductFromCart(productVariantId: String) {
        viewModelScope.launch {
            val cartId = this@CartViewModel.cartId ?: return@launch
            cartRepo.removeProductFromCart(cartId, productVariantId).collect { response ->
                if (response is ResponseState.Success<*>) {
                    showCart()
                    Log.d("1", "Item removed successfully")
                } else if (response is ResponseState.Failure) {
                    Log.e("1", "Remove failed: ${response.message}")
                }
            }
        }
    }
    fun loadDefaultAddress() {
        if (token.isNullOrBlank()) return

        viewModelScope.launch {
            addressRepo.getAddresses(token).collect { response ->
                if (response is ResponseState.Success<*>) {
                    val addressList = response.data as? List<Address> ?: emptyList()
                    val default = addressList.find { it.id?.stripTokenFromShopifyGid() == defaultAddressId }
                    _defaultAddress.value = default
                } else if (response is ResponseState.Failure) {
                    Log.e("1", "Failed to load addresses: ${response.message}")
                }
            }
        }
    }


}


class CartViewModelFactory(
    private val application: Application,
    private val cartRepo: CartRepo,
    private val addressRepo: IAddressRepo
) : ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(application, cartRepo, addressRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
