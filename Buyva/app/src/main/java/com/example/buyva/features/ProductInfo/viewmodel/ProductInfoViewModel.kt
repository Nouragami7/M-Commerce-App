package com.example.buyva.features.ProductInfo.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.buyva.GetProductByIdQuery
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.AuthRepository
import com.example.buyva.data.repository.cart.CartRepo
import com.example.buyva.data.repository.home.IHomeRepository
import com.example.buyva.utils.constants.CART_ID
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductInfoViewModel(
    application: Application,
    private val repository: IHomeRepository,
    private val authRepository: AuthRepository,
    private val cartRepo: CartRepo
) : AndroidViewModel(application) {

    private val _cartId = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val cartId: StateFlow<ResponseState> = _cartId
    private val _addingToCart = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val addingToCart: StateFlow<ResponseState> = _addingToCart

    private val _product = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val product: StateFlow<ResponseState> = _product

    fun fetchProduct(productId: String) {
        viewModelScope.launch {
            try {
                repository.getProductById(productId).collect { response ->
                    val product = response?.product
                    if (product != null) {
                        _product.value = ResponseState.Success(product)
                    } else {
                        _product.value = ResponseState.Failure(Throwable("Product is null"))
                    }
                }
            } catch (e: Exception) {
                _product.value = ResponseState.Failure(e)
            }
        }
    }
    fun addToCartById(email: String, cartId: String?, quantity: Int, variantId: String) {
        viewModelScope.launch {
            val cartKey = "CART_ID_${email.lowercase()}"
            val actualCartId = cartId ?: run {
                val newCartId = createCart(email)
                SharedPreferenceImpl.saveToSharedPreferenceInGeneral(cartKey, newCartId)
                newCartId
            }

            try {
                cartRepo.addToCartById(actualCartId, quantity, variantId).collect { response ->
                    _addingToCart.value = response
                    Log.d("1", "Add to cart response: $response")
                }
            } catch (e: Exception) {
                Log.d("1", "Error adding to cart: $e")
            }
        }
    }

    private suspend fun createCart(email: String): String {
        var newCartId = ""
        cartRepo.createCart(email, cartRepo.readUserToken()).collect { response ->
            _cartId.value = response
            if (response is ResponseState.Success<*>) {
                newCartId = response.data.toString()
                Log.d("1", "Cart created: $newCartId")
            }
        }
        if (newCartId.isEmpty()) throw Exception("Cart creation failed")
        return newCartId
    }


}

class ProductInfoViewModelFactory(
    private val application: Application,
    private val repository: IHomeRepository,
    private val authRepository: AuthRepository,
    private val cartRepo: CartRepo
) : ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductInfoViewModel::class.java)) {
            return ProductInfoViewModel(application, repository, authRepository,cartRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

