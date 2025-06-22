package com.example.buyva.features.cart.cartList.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.buyva.GetCartDetailsQuery
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.AuthRepository
import com.example.buyva.data.repository.cart.CartRepo
import com.example.buyva.data.repository.home.IHomeRepository
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel(
    application: Application,
    private val cartRepo: CartRepo
) : AndroidViewModel(application) {

    private val _cartProducts = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val cartProducts: StateFlow<ResponseState> = _cartProducts
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
    val cartId = SharedPreferenceImpl.getFromSharedPreferenceInGeneral("CART_ID_${userEmail.lowercase()}")

    fun showCart() {
        viewModelScope.launch {
            cartRepo.getCartProductList(cartId ?: "No cart Id Found!")
                .collect { response ->
                    Log.i("1", "showCart: $response")
                    _cartProducts.value = response
                }
        }
    }
}
class CartViewModelFactory(
    private val application: Application,
    private val cartRepo: CartRepo
) : ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(application, cartRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
