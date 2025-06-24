package com.example.buyva.utils.functions

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.buyva.data.model.Address
import com.example.buyva.data.model.CartItem
import com.example.buyva.data.model.OrderItem
import com.example.buyva.features.cart.cartList.viewmodel.PaymentViewModel
import com.google.firebase.auth.FirebaseAuth

fun createOrder(
    cartItems: SnapshotStateList<CartItem>,
    defaultAddress: Address?,
    paymentViewModel: PaymentViewModel,
    context: Context
) {
    val email = FirebaseAuth.getInstance().currentUser?.email ?: ""
    if (email.isNotBlank() && cartItems.isNotEmpty() && defaultAddress != null) {
        Log.d("DraftOrder", "Creating draft order after Stripe payment...")
        val orderItem = OrderItem(
            email = email, address = defaultAddress!!, cartItems = cartItems
        )
        paymentViewModel.createDraftOrder(orderItem)

    } else {

        Toast.makeText(context, "Cannot create order", Toast.LENGTH_SHORT)
            .show()
    }


    Toast.makeText(context, "Payment Successful", Toast.LENGTH_SHORT).show()
}
