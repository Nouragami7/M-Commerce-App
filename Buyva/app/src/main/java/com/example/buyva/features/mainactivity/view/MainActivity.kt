package com.example.buyva.features.mainactivity.view

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.buyva.BuildConfig
import com.example.buyva.features.SplashScreen
import com.example.buyva.navigation.SetupNavHost
import com.example.buyva.navigation.navbar.NavigationBar
import com.example.buyva.navigation.navbar.NavigationBar.ShowCurvedNavBar
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.ubuntuMedium
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.PaymentSheetResultCallback

class MainActivity : ComponentActivity()  {
    private lateinit var paymentSheet: PaymentSheet
    private lateinit var paymentSheetLauncher: PaymentSheet


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        PaymentConfiguration.init(
//            applicationContext,
//            BuildConfig.STRIPE_PUBLISHABLE_KEY
//        )

//        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
        setContent {
            var displaySplashScreen by remember { mutableStateOf(true) }
            if (displaySplashScreen) {
                SplashScreen {
                    displaySplashScreen = false
                }
            } else {
                MainScreen()
            }
//            MyComposeUI(
//                onPayClicked = {
//                    launchPayment()
//                }
//            )

        }
    }
//    private fun launchPayment() {
//        val configuration = PaymentSheet.Configuration(
//            merchantDisplayName = "Your App Name"
//        )
//
//        BuildConfig.STRIPE_SECRET_KEY.let {
//            paymentSheet.presentWithPaymentIntent(it, configuration)
//        }
//    }
//
//    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
//        when (paymentSheetResult) {
//            is PaymentSheetResult.Completed -> {
//Log.d("TAG", "onPaymentSheetResult:  $paymentSheetResult")              }
//            is PaymentSheetResult.Failed -> {
//                Log.d("TAG", "onPaymentSheetResult:  ${paymentSheetResult.error.localizedMessage}")
//            }
//            is PaymentSheetResult.Canceled -> {
//                Log.d("TAG", "onPaymentSheetResult cancelled")
//
//                Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//    @Composable
//    fun MyComposeUI(onPayClicked: () -> Unit) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(32.dp),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text("Proceed with Payment", style = MaterialTheme.typography.titleLarge)
//            Spacer(modifier = Modifier.height(16.dp))
//            Button(onClick = { onPayClicked() }) {
//                Text("Pay Now")
//            }
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun MainScreen() {
        val navController = rememberNavController()
        val isNavBarVisible = NavigationBar.mutableNavBarState.collectAsStateWithLifecycle()
        Scaffold(
            topBar = {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 12.dp)
//                ) {
//                    Text(
//                        text = "BuyVa",
//                        style = MaterialTheme.typography.headlineSmall,
//                        color = Cold,
//                        fontFamily = ubuntuMedium,
//                        modifier = Modifier.align(Alignment.Center)
//                    )
 //               }
            },
            bottomBar = {
            when (isNavBarVisible.value) {

                true -> {
                    Log.e("TAG", "nav bar visible: ")
                    ShowCurvedNavBar(navController)
                }

                false -> {
                    Log.e("TAG", "nav bar not visible: ")
                }
            }
        }

        ) { _ ->
            Box(modifier = Modifier.fillMaxSize().background(color = Color.White)) {
                SetupNavHost(navController = navController)

            }

        }
    }


}

