package com.example.buyva.features.cart.payment.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.buyva.data.repository.paymentRepo.PaymentRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.gson.JsonObject
import com.google.gson.JsonElement

class PaymentViewModel(
    private val repository: PaymentRepo
) : ViewModel() {

    private val _clientSecret = MutableStateFlow<String?>(null)

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun initiatePaymentFlow(
        amount: Int,
        currency: String = "usd",
        onClientSecretReady: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.createPaymentIntent(amount, currency)
                Log.d("1", "Response code: ${response.code()}")
                if (response.isSuccessful) {
                    val secret = response.body()?.get("client_secret")?.toString()
                    Log.d("1", "Client secret: $secret")
                    if (!secret.isNullOrBlank()) {
                        _clientSecret.value = secret
                        onClientSecretReady(secret)
                    } else {
                        Log.d("1", "client_secret is null or blank")
                        _error.value = "Missing client_secret in response"
                    }
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Log.e("1", "Stripe API error: $errorMsg")
                    _error.value = "Error: $errorMsg"
                }
            } catch (e: Exception) {
                Log.e("1", "Network exception: ${e.message}", e)
                _error.value = "Exception: ${e.localizedMessage ?: "Unknown exception"}"
            }
        }
    }

}

class PaymentViewModelFactory(
    private val repository: PaymentRepo
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PaymentViewModel(repository) as T
    }
}