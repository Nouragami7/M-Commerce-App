package com.example.buyva

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.buyva.navigation.SetupNavHost
import com.example.buyva.navigation.navbar.NavigationBar
import com.example.buyva.navigation.navbar.NavigationBar.ShowCurvedNavBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
           // SplashScreen()
            MainScreen()
        }
    }
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val isNavBarVisible = NavigationBar.mutableNavBarState.collectAsStateWithLifecycle()
     Scaffold (
         bottomBar = {
             when (isNavBarVisible.value) {

                 true -> {
                     Log.e("TAG", "nav bar visible: " )
                     ShowCurvedNavBar(navController)
                 }
                 false -> {
                     Log.e("TAG", "nav bar not visible: " )
                 }
                 null -> {
                     Log.e("TAG", "nav bar null: " )
                 }
             }
         }

     ){
         _ -> Box (modifier = Modifier.fillMaxSize()){
             SetupNavHost(navController = navController)

     }

     }
}

}

