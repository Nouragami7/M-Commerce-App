package com.example.buyva.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.buyva.features.cart.view.CartScreen
import com.example.buyva.features.categories.view.CategoryScreen
import com.example.buyva.features.favourite.view.FavouriteScreen
import com.example.buyva.features.home.view.HomeScreen
import com.example.buyva.features.profile.view.ProfileScreen

@Composable
fun SetupNavHost(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = ScreensRoute.HomeScreen
    ) {
        composable<ScreensRoute.HomeScreen> {
            HomeScreen()
        }
        composable<ScreensRoute.CartScreen> {
            CartScreen()
        }
        composable<ScreensRoute.CategoriesScreen> {
            CategoryScreen()
        }
        composable<ScreensRoute.FavouritesScreen> {
            FavouriteScreen()
        }
        composable<ScreensRoute.ProfileScreen> {
            ProfileScreen()
        }

    }
}


