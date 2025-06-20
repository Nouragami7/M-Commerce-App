package com.example.buyva.features.cart.payment.view

import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buyva.data.remote.RemoteDataSourceImpl
import com.example.buyva.data.remote.StripeClient
import com.example.buyva.data.repository.paymentRepo.PaymentRepoImpl
import com.example.buyva.features.cart.payment.viewmodel.PaymentViewModel
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import com.example.buyva.BuildConfig
import com.example.buyva.features.cart.payment.viewmodel.PaymentViewModelFactory

@Composable
fun CheckoutScreen(
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    // ✅ Initialize Stripe before using the PaymentSheet
    LaunchedEffect(Unit) {
        PaymentConfiguration.init(context, BuildConfig.STRIPE_PUBLISHABLE_KEY)
    }

    // ✅ Stripe PaymentSheet launcher - must be remembered early
    val paymentSheet = rememberPaymentSheet(
        paymentResultCallback = { result ->
            when (result) {
                is PaymentSheetResult.Completed -> {
                    Toast.makeText(context, "Payment Successful", Toast.LENGTH_SHORT).show()
                }
                is PaymentSheetResult.Canceled -> {
                    Toast.makeText(context, "Payment Cancelled", Toast.LENGTH_SHORT).show()
                }
                is PaymentSheetResult.Failed -> {
                    Toast.makeText(context, "Payment Failed: ${result.error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )

    val paymentViewModel: PaymentViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PaymentViewModel(
                    PaymentRepoImpl(RemoteDataSourceImpl(StripeClient.api))
                ) as T
            }
        }
    )

                Log.d("1", "Pay button clicked")
                paymentViewModel.initiatePaymentFlow(
                    amount = 200,
                    onClientSecretReady = { secret ->
                        Log.d("1", "Client secret: $secret")
                        paymentSheet.presentWithPaymentIntent(
                            paymentIntentClientSecret = secret,
                            configuration = PaymentSheet.Configuration(
                                merchantDisplayName = "BuyNest"
                            )
                        )
                    }
                )
                Log.d("1", "Pay button clicked")



}




