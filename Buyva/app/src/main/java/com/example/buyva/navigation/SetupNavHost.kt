package com.example.buyva.navigation

import CartScreen
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.buyva.data.datasource.remote.RemoteDataSourceImpl
import com.example.buyva.data.datasource.remote.graphql.ApolloAdmin
import com.example.buyva.data.datasource.remote.graphql.ApolloService
import com.example.buyva.data.datasource.remote.stripe.StripeClient
import com.example.buyva.data.repository.home.HomeRepositoryImpl
import com.example.buyva.features.ProductInfo.View.ProductInfoScreen
import com.example.buyva.features.authentication.login.view.GuestRestrictionScreen
import com.example.buyva.features.authentication.login.view.LoginScreenHost
import com.example.buyva.features.authentication.login.view.WelcomeScreen
import com.example.buyva.features.authentication.login.viewmodel.UserSessionManager
import com.example.buyva.features.authentication.signup.view.SignupScreenHost
import com.example.buyva.features.brand.view.BrandProductsScreen
import com.example.buyva.features.categories.view.CategoryScreen
import com.example.buyva.features.favourite.view.FavouriteScreen
import com.example.buyva.features.favourite.viewmodel.FavouriteScreenViewModel
import com.example.buyva.features.home.view.HomeScreen
import com.example.buyva.features.orderdetails.view.OrderDetailsScreen
import com.example.buyva.features.orderdetails.viewmodel.SharedOrderViewModel
import com.example.buyva.features.profile.addressdetails.view.AddressDetails
import com.example.buyva.features.profile.addressdetails.viewlist.DeliveryAddressListScreen
import com.example.buyva.features.profile.currency.viewcurrency.CurrencyScreen
import com.example.buyva.features.profile.currency.viewmodel.CurrencyViewModel
import com.example.buyva.features.profile.map.view.MapScreen
import com.example.buyva.features.profile.map.viewmodel.MapViewModel
import com.example.buyva.features.profile.profileoptions.view.ProfileScreen
import com.example.buyva.features.search.view.SearchScreen
import com.example.buyva.features.search.viewmodel.SearchViewModel
import com.example.yourapp.ui.screens.OrderScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupNavHost(
    navController: NavHostController,
    startDestination: String,
) {
    val sharedOrderViewModel: SharedOrderViewModel = viewModel()



    NavHost(
        navController = navController, startDestination = startDestination
    ) {
        composable<ScreensRoute.WelcomeScreen> {
            WelcomeScreen(onSignInClick = { navController.navigate(ScreensRoute.LoginScreen) },
                onSignUpClick = { navController.navigate(ScreensRoute.SignUpScreen) },
                onSkipClick = {
                    UserSessionManager.setGuestMode(true)
                    navController.navigate(ScreensRoute.HomeScreen) {
                        popUpTo(0)
                    }
                })
        }


        composable<ScreensRoute.LoginScreen> {
            LoginScreenHost(onSignUpClick = { navController.navigate(ScreensRoute.SignUpScreen) },
                onSuccess = {
                    navController.navigate(ScreensRoute.HomeScreen) {
                        popUpTo(0)
                    }
                })
        }

        composable<ScreensRoute.SignUpScreen> {
            SignupScreenHost(onSignInClick = { navController.navigate(ScreensRoute.LoginScreen) },
                onSuccess = {
                    navController.navigate(ScreensRoute.HomeScreen) {
                        popUpTo(0)
                    }
                })
        }
        composable<ScreensRoute.HomeScreen> {
            val favouriteViewModel =
                if (!UserSessionManager.isGuest()) hiltViewModel<FavouriteScreenViewModel>() else null

            HomeScreen(onCartClick = { navController.navigate(ScreensRoute.CartScreen) },
                onBrandClick = { brandId, brandTitle, brandImage ->
                    navController.navigate(
                        ScreensRoute.BrandProductsScreen(
                            brandId, brandTitle, brandImage
                        )
                    )
                },
                onProductClick = { productId ->
                    navController.navigate("productInfo/$productId")
                },
                onSearchClick = { navController.navigate(ScreensRoute.SearchScreen) },
                favouriteViewModel = favouriteViewModel
            )
        }




        composable<ScreensRoute.CartScreen> {
            if (UserSessionManager.isGuest()) {
                GuestRestrictionScreen(onSignIn = { navController.navigate(ScreensRoute.LoginScreen) },
                    onSignUp = { navController.navigate(ScreensRoute.SignUpScreen) },
                    onContinue = {
                        navController.navigate(ScreensRoute.HomeScreen) {
                            popUpTo(ScreensRoute.CartScreen) { inclusive = true }
                        }
                    })
            } else {
                CartScreen(
                    onBackClick = { navController.popBackStack() },
                    onCheckoutClick = { navController.navigate(ScreensRoute.CheckoutScreen) },
                    onNavigateToOrders = { navController.navigate(ScreensRoute.OrderScreen) },
                    onNavigateToAddresses = { navController.navigate(ScreensRoute.DeliveryAddressListScreen) },
                    onNavigateToProductInfo = { productId, size, value ->
                        val encodedId = Uri.encode(productId)
                        val encodedSize = Uri.encode(size) as String
                        val encodedValue = Uri.encode(value) as String
                        navController.navigate("productInfo/$encodedId?size=$encodedSize&color=$encodedValue")

                    },

                    )
            }
        }


        composable<ScreensRoute.CategoriesScreen> {
            val favouriteViewModel =
                if (!UserSessionManager.isGuest()) hiltViewModel<FavouriteScreenViewModel>() else null

            CategoryScreen(onCartClick = { navController.navigate(ScreensRoute.CartScreen) },
                onProductClick = { productId ->
                    navController.navigate("productInfo/$productId")
                },
                onSearchClick = { navController.navigate(ScreensRoute.SearchScreen) },
                favouriteViewModel = favouriteViewModel
            )
        }


        composable<ScreensRoute.FavouritesScreen> {
            if (UserSessionManager.isGuest()) {
                GuestRestrictionScreen(onSignIn = { navController.navigate(ScreensRoute.LoginScreen) },
                    onSignUp = { navController.navigate(ScreensRoute.SignUpScreen) },
                    onContinue = {
                        navController.navigate(ScreensRoute.HomeScreen) {
                            popUpTo(ScreensRoute.FavouritesScreen) { inclusive = true }
                        }
                    })
            } else {
                val favouriteViewModel = hiltViewModel<FavouriteScreenViewModel>()

                FavouriteScreen(
                    viewModel = favouriteViewModel, navController = navController
                )
            }
        }


        composable<ScreensRoute.BrandProductsScreen> { entry ->
            val id = entry.arguments?.getString("brandID") ?: ""
            val name = entry.arguments?.getString("brandName") ?: "Adidas"
            val image = entry.arguments?.getString("brandImage") ?: ""

            val favouriteViewModel =
                if (!UserSessionManager.isGuest()) hiltViewModel<FavouriteScreenViewModel>() else null

            BrandProductsScreen(
                brandId = id,
                brandName = name,
                imageUrl = image,
                onBack = { navController.popBackStack() },
                onProductClick = { productId ->
                    navController.navigate("productInfo/$productId")
                },
                onSearchClick = {
                    val encodedBrand = URLEncoder.encode(name, StandardCharsets.UTF_8.toString())
                    navController.navigate("search?brand=$encodedBrand")
                },
                favouriteViewModel = favouriteViewModel
            )
        }


        composable("productInfo/{productId}?size={size}&color={color}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            val variantId = backStackEntry.arguments?.getString("variantId") ?: ""
            val size = backStackEntry.arguments?.getString("size")?.takeIf { it.isNotEmpty() }
            val color = backStackEntry.arguments?.getString("color")?.takeIf { it.isNotEmpty() }
            val favouriteViewModel = if (!UserSessionManager.isGuest()) {
                hiltViewModel<FavouriteScreenViewModel>()
            } else {
                null
            }
            val repository = remember {
                HomeRepositoryImpl(
                    RemoteDataSourceImpl(
                        ApolloService.client, ApolloAdmin, StripeClient.api
                    )
                )
            }
            ProductInfoScreen(
                productId = productId,
                repository = repository,
                navController = navController,
                favouriteViewModel = favouriteViewModel,
                size = size,
                color = color
            )

        }

        composable<ScreensRoute.ProfileScreen> {
            ProfileScreen(onFavClick = {
                navController.navigate(ScreensRoute.FavouritesScreen)
            }, onAddressClick = {
                navController.navigate(ScreensRoute.DeliveryAddressListScreen)
            }, onOrdersClick = {
                navController.navigate(ScreensRoute.OrderScreen)
            }, onLoggedOut = {
                navController.navigate(ScreensRoute.WelcomeScreen) {
                    popUpTo(0)
                }
            }, onCurrencyClick = {
                navController.navigate(ScreensRoute.CurrencyScreen)
            })
        }

        composable<ScreensRoute.AddressDetails> { backStackEntry ->
            AddressDetails(address = backStackEntry.toRoute<ScreensRoute.AddressDetails>().address,
                city = backStackEntry.toRoute<ScreensRoute.AddressDetails>().city,
                country = backStackEntry.toRoute<ScreensRoute.AddressDetails>().country,
                editableTextFields = backStackEntry.toRoute<ScreensRoute.AddressDetails>().editableTextFields,
                prefillData = backStackEntry.toRoute<ScreensRoute.AddressDetails>().prefillData,
                onSaveClick = {
                    navController.popBackStack()
                    navController.popBackStack()

                }
            )
        }

        composable<ScreensRoute.DeliveryAddressListScreen> {
            DeliveryAddressListScreen(onBackClick = { navController.popBackStack() },
                onAddressDetailsClick = { address, prefillData ->
                    navController.navigate(
                        ScreensRoute.AddressDetails(
                            address = address ?: "",
                            editableTextFields = false,
                            prefillData = prefillData,
                        )
                    )
                },
                onAddressClick = {
                    navController.navigate(ScreensRoute.MapScreen)
                })
        }

        composable<ScreensRoute.MapScreen> {
            val mapViewModel = MapViewModel()
            MapScreen(back = { navController.popBackStack() },
                mapViewModel = mapViewModel,
                onSelected = { address, city, country ->
                    navController.navigate(
                        ScreensRoute.AddressDetails(
                            address = address,
                            city = city,
                            country = country,
                            editableTextFields = true,
                            prefillData = ""
                        )
                    )

                })
        }

        composable<ScreensRoute.OrderScreen> {
            OrderScreen(onBack = {
                navController.navigateUp()
            }, onOrderClick = { selectedOrder ->
                sharedOrderViewModel.setOrder(selectedOrder)
                navController.navigate(ScreensRoute.OrderDetailsScreen)

            }

            )
        }

        composable<ScreensRoute.OrderDetailsScreen> {
            OrderDetailsScreen(sharedOrderViewModel = sharedOrderViewModel,
                onBack = { navController.popBackStack() })
        }

        composable<ScreensRoute.SettingsScreen> { /* Placeholder */ }
        composable<ScreensRoute.PaymentScreen> { /* Placeholder */ }

        composable<ScreensRoute.SearchScreen> {
            val favouriteViewModel =
                if (!UserSessionManager.isGuest()) hiltViewModel<FavouriteScreenViewModel>() else null
            val searchViewModel = hiltViewModel<SearchViewModel>()

            SearchScreen(searchViewModel = searchViewModel,
                favouriteViewModel = favouriteViewModel,
                onProductClick = { productId ->
                    val encodedId = URLEncoder.encode(productId, StandardCharsets.UTF_8.toString())
                    navController.navigate("productInfo/$encodedId")
                },
                onBack = {
                    navController.popBackStack()
                })
        }


        composable("search?brand={brand}") { backStackEntry ->
            val brand = backStackEntry.arguments?.getString("brand") ?: ""

            val favouriteViewModel =
                if (!UserSessionManager.isGuest()) hiltViewModel<FavouriteScreenViewModel>() else null
            val searchViewModel = hiltViewModel<SearchViewModel>()

            SearchScreen(brandFilter = brand,
                searchViewModel = searchViewModel,
                favouriteViewModel = favouriteViewModel,
                onProductClick = { productId ->
                    val encodedId = URLEncoder.encode(productId, StandardCharsets.UTF_8.toString())
                    navController.navigate("productInfo/$encodedId")
                },
                onBack = {
                    navController.popBackStack()
                })
        }




        composable<ScreensRoute.CurrencyScreen> {
            val viewModel: CurrencyViewModel = hiltViewModel()

            CurrencyScreen(viewModel = viewModel, onBackClick = {
                navController.popBackStack()
            })
        }


    }

}