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


//    fun isCustomerHasCart(email: String) {
//        viewModelScope.launch {
//            try {
//                val hasCart = firebaseRepository.isCustomerHasCart(email)
//                _hasCart.value = ApiState.Success(hasCart)
//            } catch (e: Exception) {
//                _hasCart.value = ApiState.Failure(e)
//            }
//        }
//    }
//
//    fun addCustomerCart(email: String, cartId: String) {
//        viewModelScope.launch {
//            try {
//                firebaseRepository.addCustomerCart(email, cartId)
//            } catch (e: Exception) {
//                Log.e(TAG, "Error adding customer cart: ", e)
//            }
//        }
//    }
//
//
//    fun getCartByCustomer(email: String) {
//        viewModelScope.launch {
//            _customerCart.value = ApiState.Loading
//            try {
//                val cartId = firebaseRepository.getCartByCustomer(email)
//                _customerCart.value = ApiState.Success(cartId)
//            } catch (e: Exception) {
//                _customerCart.value = ApiState.Failure(e)
//                Log.e(TAG, "Error getting customer cart: ", e)
//            }
//        }
//    }
//
//    fun addProductToCart(productId: String, quantity: Int, variantId: String) {
//
//        viewModelScope.launch {
//            repository.addToCartById(productId, quantity, variantId).collect {
//                _addingToCart.value = it
//            }
//        }
//    }
//
//    suspend fun readCustomerEmail(): String {
//
//        return repository.readEmailFromSharedPreferences(USER_EMAIL)
//    }
//
//    fun writeCartId(cartId: String) {
//        viewModelScope.launch {
//            repository.writeCartIdToSharedPreferences(cartId, CART_ID)
//        }
//    }
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

