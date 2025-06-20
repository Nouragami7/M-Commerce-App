package com.example.buyva.navigation

import CartScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.buyva.data.datasource.remote.RemoteDataSourceImpl
import com.example.buyva.data.datasource.remote.graphql.ApolloService
import com.example.buyva.data.repository.home.HomeRepositoryImpl
import com.example.buyva.features.ProductInfo.View.ProductInfoScreen
import com.example.buyva.features.authentication.login.view.LoginScreenHost
import com.example.buyva.features.authentication.login.view.WelcomeScreen
import com.example.buyva.features.authentication.signup.view.SignupScreenHost
import com.example.buyva.features.brand.view.BrandProductsScreen
import com.example.buyva.features.categories.view.CategoryScreen
import com.example.buyva.features.favourite.view.FavouriteScreen
import com.example.buyva.features.home.view.HomeScreen
import com.example.buyva.features.orderdetails.view.OrderDetailsScreen
import com.example.buyva.features.profile.addressdetails.view.AddressDetails
import com.example.buyva.features.profile.addresseslist.view.DeliveryAddressListScreen
import com.example.buyva.features.profile.map.view.MapScreen
import com.example.buyva.features.profile.map.viewmodel.MapViewModel
import com.example.buyva.features.profile.profileoptions.view.ProfileScreen
import com.example.yourapp.ui.screens.OrderScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupNavHost(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
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
        composable<ScreensRoute.HomeScreen> { HomeScreen(
            onCartClick = { navController.navigate(ScreensRoute.CartScreen) },
            onBrandClick = { brandId, brandTitle, brandImage ->
                navController.currentBackStackEntry?.savedStateHandle?.apply {
                    set("brandID", brandId)
                    set("brandName", brandTitle)
                    set("brandImage", brandImage)
                }
                println("Brand ID--------------------: $brandId")
                println("Brand ID--------------------: $brandTitle")
                println("Brand ID--------------------: $brandImage")
                navController.navigate(ScreensRoute.BrandProductsScreen(brandId, brandTitle, brandImage))
            }
            ,
            onProductClick = { productId ->
                navController.navigate("productInfo/$productId")
            }
        ) }
        composable<ScreensRoute.CartScreen> { CartScreen() }
        composable<ScreensRoute.CategoriesScreen> { CategoryScreen(
            onCartClick = { navController.navigate(ScreensRoute.CartScreen)},
            onProductClick = { navController.navigate(ScreensRoute.ProductInfoScreen) }
        ) }
        composable<ScreensRoute.FavouritesScreen> { FavouriteScreen() }

        composable<ScreensRoute.ProfileScreen> {
            ProfileScreen(
                onSettingsClick = {
                    //navController.navigate(ScreensRoute.AddressDetailsScreen)
                },
                onAddressClick = {
                    navController.navigate(ScreensRoute.DeliveryAddressListScreen)
                },
                onOrdersClick = {
                    navController.navigate(ScreensRoute.OrderScreen)
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
                onAddressDetailsClick = {  address ->
                    navController.navigate(ScreensRoute.AddressDetails(0.0, 0.0, address ?: ""))
                },
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

        composable<ScreensRoute.BrandProductsScreen> { entry ->
            val id = entry.arguments?.getString("brandID") ?: ""
            val name = entry.arguments?.getString("brandName") ?: "Adidas"
            val image = entry.arguments?.getString("brandImage") ?: ""

            BrandProductsScreen(
                brandId = id,
                brandName = name,
                imageUrl = image,
                onBack = { navController.popBackStack() },

                // 👇 التعديل هنا
                onProductClick = { productId ->
                    val encodedId = URLEncoder.encode(productId, StandardCharsets.UTF_8.toString())
                    navController.navigate("productInfo/$encodedId")
                }
            )
        }

        composable("productInfo/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable

            val repository = remember {
                HomeRepositoryImpl(RemoteDataSourceImpl(ApolloService.client))
            }

            ProductInfoScreen(productId = productId, repository = repository, navController = navController)
        }


        composable<ScreensRoute.OrderScreen> { OrderScreen(
            onBack = { navController.popBackStack() },
            onOrderClick = {navController.navigate(ScreensRoute.OrderDetailsScreen(it))}
        ) }

        composable<ScreensRoute.OrderDetailsScreen> { OrderDetailsScreen(
            onBack = { navController.popBackStack() },
            onProductClick = { navController.navigate(ScreensRoute.ProductInfoScreen) }
        ) }


    }
}

