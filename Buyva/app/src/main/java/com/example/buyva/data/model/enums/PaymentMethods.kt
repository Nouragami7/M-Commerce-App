package com.example.buyva.data.model.enums

enum class PaymentMethod(val displayName: String) {
    CashOnDelivery("Cash On Delivery"),
    PayWithCard("Pay With Card")
}
//sealed class ResponseState<out T> {
//    object Loading : ResponseState<Nothing>()
//    data class Success<T>(val data: T) : ResponseState<T>()
//    data class Failure(val message: String) : ResponseState<Nothing>()
//}
