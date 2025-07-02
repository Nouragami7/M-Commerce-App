package com.example.buyva.features.cart.cartList.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buyva.admin.type.DraftOrderInput
import com.example.buyva.data.model.OrderItem
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.paymentRepo.PaymentRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import toDraftOrderInput
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repository: PaymentRepo
) : ViewModel() {

    private val _clientSecret = MutableStateFlow<String?>(null)

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _orderState = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val order: StateFlow<ResponseState> = _orderState


    private val _completeOrderState = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val completeOrderState: StateFlow<ResponseState> = _completeOrderState

    fun initiatePaymentFlow(
        amount: Int,
        currency: String = "usd",
        onClientSecretReady: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.createPaymentIntent(amount, currency,"card")
               // Log.d("1", "Response code: ${response.code()}")
                if (response.isSuccessful) {
                    val secret = response.body()?.get("client_secret")?.asJsonPrimitive?.asString
                //    Log.d("1", "Client secret: $secret")
                    if (!secret.isNullOrBlank()) {
                        _clientSecret.value = secret
                        onClientSecretReady(secret)
                    } else {
                    //    Log.d("1", "client_secret is null or blank")
                        _error.value = "Missing client_secret in response"
                    }
                } else {
                    val errorMsg = response.errorBody()?.string()
                  //  Log.e("1", "Stripe API error: $errorMsg")
                    _error.value = "Error: $errorMsg"
                }
            } catch (e: Exception) {
                //Log.e("1", "Network exception: ${e.message}", e)
                _error.value = "Exception: ${e.localizedMessage ?: "Unknown exception"}"
            }
        }
    }


    fun createDraftOrder(orderItem: OrderItem) {
        viewModelScope.launch {
            try {
                val draftOrderInput: DraftOrderInput = orderItem.toDraftOrderInput()
                repository.createDraftOrder(draftOrderInput).collect { response ->
                    _orderState.value = response
                 //   Log.d("OrderVM", "Order creation response: $response")
                }
            } catch (e: Exception) {
                _orderState.value = ResponseState.Failure(Throwable(e.message))
               // Log.e("OrderRepoVM", "Error creating draft order: ${e.message}", e)
            }
        }
    }

    fun completeDraftOrder(draftOrderId: String) {
        viewModelScope.launch {
            try {
                repository.completeDraftOrder(draftOrderId).collect { result ->
                  //  Log.d("OrderVM", "Draft order completed: ${result.draftOrderComplete?.draftOrder?.id}")
                    _completeOrderState.value = ResponseState.Success("Order completed successfully!")
                }
            } catch (e: Exception) {
                _completeOrderState.value = ResponseState.Failure(Throwable(e.message))
              //  Log.e("OrderVM", "Error completing draft order: ${e.message}", e)
            }
        }
    }
    fun resetOrderCompleteState() {
        _completeOrderState.value = ResponseState.Loading
    }


}
