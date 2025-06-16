package com.example.buyva.navigation

import CartScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.buyva.features.authentication.login.view.LoginScreenHost
import com.example.buyva.features.authentication.login.view.WelcomeScreen
import com.example.buyva.features.categories.view.CategoryScreen
import com.example.buyva.features.favourite.view.FavouriteScreen
import com.example.buyva.features.home.view.HomeScreen
import com.example.buyva.features.profile.addressdetails.view.AddressDetails
import com.example.buyva.features.profile.addresseslist.view.DeliveryAddressListScreen
import com.example.buyva.features.profile.profileoptions.view.ProfileScreen
import com.example.buyva.features.authentication.signup.view.SignupScreenHost
import com.example.buyva.features.profile.map.view.MapScreen
import com.example.buyva.features.profile.map.viewmodel.MapViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = ScreensRoute.WelcomeScreen
    ) {
        composable<ScreensRoute.WelcomeScreen> {
            WelcomeScreen(
                onSignInClick = { navController.navigate(ScreensRoute.LoginScreen) },
                onSignUpClick = { navController.navigate(ScreensRoute.SignUpScreen) },
                onSkipClick = {
                    navController.navigate(ScreensRoute.HomeScreen) {
                        popUpTo(0)
                    }
                }
            )
        }


        composable<ScreensRoute.LoginScreen> {
            LoginScreenHost(
                onSignUpClick = { navController.navigate(ScreensRoute.SignUpScreen) },
                onSuccess = {
                    navController.navigate(ScreensRoute.HomeScreen) {
                        popUpTo(0) // clear backstack
                    }
                }
            )
        }

        composable<ScreensRoute.SignUpScreen> {
            SignupScreenHost(
                onSignInClick = { navController.navigate(ScreensRoute.LoginScreen) },
                onSuccess = {
                    navController.navigate(ScreensRoute.HomeScreen) {
                        popUpTo(0)
                    }
                }
            )
        }
        composable<ScreensRoute.HomeScreen> { HomeScreen() }
        composable<ScreensRoute.CartScreen> { CartScreen() }
        composable<ScreensRoute.CategoriesScreen> { CategoryScreen() }
        composable<ScreensRoute.FavouritesScreen> { FavouriteScreen() }
        composable<ScreensRoute.ProfileScreen> {
            ProfileScreen(
                onSettingsClick = {
                    //navController.navigate(ScreensRoute.AddressDetailsScreen)
                },
                onAddressClick = {
                    navController.navigate(ScreensRoute.DeliveryAddressListScreen)
                }
            )
        }
        composable<ScreensRoute.AddressDetails> {
            AddressDetails(
                lat = it.arguments?.getDouble("lat") ?: 0.0,
                lon = it.arguments?.getDouble("lon") ?: 0.0,
                address = it.arguments?.getString("address") ?: "",
                onBackClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() }
                )
        }
        composable<ScreensRoute.DeliveryAddressListScreen> {
            DeliveryAddressListScreen(
                onBackClick = { navController.popBackStack() },
                onAddressClick = {
                    navController.navigate(ScreensRoute.MapScreen)
                }

            )
        }
        composable<ScreensRoute.MapScreen> {
            val mapViewModel = MapViewModel()
            MapScreen(
                back = { navController.popBackStack() },
                mapViewModel = mapViewModel,
                onSelected = { lat, lon, address ->
                    navController.navigate(ScreensRoute.AddressDetails(lat, lon, address ?: ""))
                }

            )
        }


    }
}

