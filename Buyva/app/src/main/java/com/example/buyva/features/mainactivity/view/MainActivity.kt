package com.example.buyva.features.mainactivity.view

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.buyva.R
import com.example.buyva.features.SplashScreen
import com.example.buyva.features.authentication.login.viewmodel.UserSessionManager
import com.example.buyva.navigation.ScreensRoute
import com.example.buyva.navigation.SetupNavHost
import com.example.buyva.navigation.navbar.NavigationBar
import com.example.buyva.navigation.navbar.NavigationBar.ShowCurvedNavBar
import com.example.buyva.utils.components.EmptyScreen
import com.example.buyva.utils.functions.ConnectivityObserver
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserSessionManager.init(applicationContext)

        setContent {
            var displaySplashScreen by remember { mutableStateOf(true) }
            var startDestination by remember { mutableStateOf("splash") }
            LaunchedEffect(Unit) {
                val firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                val isLoggedIn = firebaseUser != null && firebaseUser.isEmailVerified
                val loggedIn = SharedPreferenceImpl.getFromSharedPreference(
                    context = this@MainActivity,
                    "SignIn"
                )
                UserSessionManager.setGuestMode(!isLoggedIn)

                startDestination = if (isLoggedIn && loggedIn == "true") {
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

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun MainScreen(startDestination: String) {
        val navController = rememberNavController()
        val isNavBarVisible = NavigationBar.mutableNavBarState.collectAsStateWithLifecycle()
        val context = LocalContext.current
        val connectivityObserver = ConnectivityObserver(context)
        val isConnected by connectivityObserver.isConnected.collectAsStateWithLifecycle()

        Scaffold(bottomBar = {
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
            Box(modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)) {
                SetupNavHost(navController = navController, startDestination = startDestination)
            }
            if (!isConnected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.95f)),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyScreen(
                        text = "No internet connection",
                        fontSize = 24.sp,
                        animation = R.raw.no_internet
                    )
                }
            }

        }
    }

}

