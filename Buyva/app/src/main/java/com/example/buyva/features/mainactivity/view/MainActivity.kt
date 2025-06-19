package com.example.buyva.features.mainactivity.view

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.buyva.features.SplashScreen
import com.example.buyva.features.authentication.signup.viewmodel.SignupViewModel
import com.example.buyva.navigation.ScreensRoute
import com.example.buyva.navigation.SetupNavHost
import com.example.buyva.navigation.navbar.NavigationBar
import com.example.buyva.navigation.navbar.NavigationBar.ShowCurvedNavBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var displaySplashScreen by remember { mutableStateOf(true) }
            var startDestination by remember { mutableStateOf("splash") }

            // Check auth state once when activity starts
            LaunchedEffect(Unit) {
                val context = this@MainActivity
                startDestination = if (SignupViewModel.isUserLoggedIn(context)) {
                    ScreensRoute.HomeScreen::class.qualifiedName!!
                } else {
                    ScreensRoute.WelcomeScreen::class.qualifiedName!!
                }
            }


                if (displaySplashScreen) {
                SplashScreen {
                    displaySplashScreen = false
                }
            } else {
                MainScreen(startDestination)
            }
        }
    }

    @Composable
    fun MainScreen(startDestination: String) {
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
                SetupNavHost(navController = navController, startDestination = startDestination)

            }

        }
    }

}

