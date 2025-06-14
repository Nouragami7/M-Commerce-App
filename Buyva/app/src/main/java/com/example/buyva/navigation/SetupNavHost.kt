package com.example.buyva.navigation

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
import com.example.buyva.features.authentication.signup.view.SignupScreenHost
import com.example.buyva.features.cart.view.CartScreen
import com.example.buyva.features.categories.view.CategoryScreen
import com.example.buyva.features.favourite.view.FavouriteScreen
import com.example.buyva.features.home.view.HomeScreen
import com.example.buyva.features.profile.view.ProfileScreen

@Composable
fun SetupNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = ScreensRoute.HomeScreen
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
        composable<ScreensRoute.ProfileScreen> { ProfileScreen() }
    }
}

