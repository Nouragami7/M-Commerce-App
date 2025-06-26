package com.example.buyva.features.cart.cartList.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.buyva.GetCartDetailsQuery
import com.example.buyva.data.model.Address
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.adresses.IAddressRepo
import com.example.buyva.data.repository.cart.CartRepo
import com.example.buyva.utils.constants.DEFAULT_ADDRESS_ID
import com.example.buyva.utils.constants.USER_TOKEN
import com.example.buyva.utils.extensions.stripTokenFromShopifyGid
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
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


//    fun getCartDetails() {
//        viewModelScope.launch {
//Log.i("1", "cart id in view model: $cartId")
//            if (cartId != null) {
//                cartRepo.getCartProducts(cartId).collect { response ->
//                    _cartProducts.value = response
//                    Log.i("1", "getCartDetails: $response")
//                    if (response is ResponseState.Success<*>) {
//                        cartLinesId = ((response.data as GetCartDetailsQuery.Data).cart?.lines?.edges
//                            ?.map { edge ->
//                                edge.node.id
//                            } ?: emptyList()) as MutableStateFlow<List<String>>
//                    }
//                }
//            }
//            }
//        }


        fun showCart() {
            if (cartId.isNullOrBlank()) {  //first time
                _cartProducts.value = ResponseState.Failure(Throwable("Add first item to the cart!"))
                return
            }
            viewModelScope.launch {
                _cartProducts.value = ResponseState.Loading
                try {
                    if (cartId != null) {
                        cartRepo.getCartProductList(cartId )
                            .collect { response ->
                                _cartProducts.value = response
                                Log.i("1", "showCart: ${ResponseState.Success(response)}")
                            }
                    }
                } catch (e: Exception) {
                    _cartProducts.value = ResponseState.Failure(Throwable(e.message ?: "Unknown"))
                }
            }

        }



    fun removeProductFromCart(productLineId: String) {
        viewModelScope.launch {
            val cartId = this@CartViewModel.cartId ?: return@launch
            cartRepo.removeProductFromCart(cartId, productLineId).collect { response ->
                if (response is ResponseState.Success<*>) {
                   showCart()
                    Log.d("1", "Item removed successfully")
                } else if (response is ResponseState.Failure) {
                    Log.e("1", "Remove failed: ${response.message}")
                }
            }
        }
    }


    fun clearCart() {
        viewModelScope.launch {
            val cartId = this@CartViewModel.cartId ?: return@launch

            cartRepo.getCartProducts(cartId).collect { response ->
                if (response is ResponseState.Success<*>) {
                    val cart = response.data as? GetCartDetailsQuery.Cart
                    val lineIds = cart?.lines?.edges?.map { it.node.id } ?: emptyList()

                    lineIds.forEach { lineId ->
                        cartRepo.removeProductFromCart(cartId, lineId).collect { removeResponse ->
                            if (removeResponse is ResponseState.Failure) {
                                Log.e("1", "Failed to remove item: ${removeResponse.message}")
                            }
                        }
                    }
                   showCart()
                    Log.d("1", "Cart cleared successfully")
                } else if (response is ResponseState.Failure) {
                    Log.e("1", "Failed to load cart for clearing: ${response.message}")
                }
            }
        }
    }


    fun loadDefaultAddress() {
         val defaultAddressId = SharedPreferenceImpl.getFromSharedPreferenceInGeneral("${DEFAULT_ADDRESS_ID}_$token")

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
