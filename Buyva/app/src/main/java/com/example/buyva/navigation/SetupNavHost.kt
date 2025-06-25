package com.example.buyva.navigation

import CartScreen
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.buyva.data.datasource.remote.RemoteDataSourceImpl
import com.example.buyva.data.datasource.remote.currency.CurrencyApiService
import com.example.buyva.data.datasource.remote.currency.CurrencyRetrofitClient
import com.example.buyva.data.datasource.remote.graphql.ApolloService
import com.example.buyva.data.repository.AuthRepository
import com.example.buyva.data.repository.currency.CurrencyRepo
import com.example.buyva.data.repository.favourite.FavouriteRepositoryImpl
import com.example.buyva.data.repository.home.HomeRepositoryImpl
import com.example.buyva.data.repository.search.SearchRepositoryImpl
import com.example.buyva.features.ProductInfo.View.ProductInfoScreen
import com.example.buyva.features.authentication.login.view.LoginScreenHost
import com.example.buyva.features.authentication.login.view.WelcomeScreen
import com.example.buyva.features.authentication.signup.view.SignupScreenHost
import com.example.buyva.features.authentication.signup.viewmodel.LogoutViewModel
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
import com.example.buyva.features.profile.currency.viewmodel.CurrencyViewModelFactory
import com.example.buyva.features.profile.map.view.MapScreen
import com.example.buyva.features.profile.map.viewmodel.MapViewModel
import com.example.buyva.features.profile.profileoptions.view.ProfileScreen
import com.example.buyva.features.search.view.SearchScreen
import com.example.buyva.features.search.viewmodel.SearchViewModel
import com.example.yourapp.ui.screens.OrderScreen
import com.google.firebase.auth.FirebaseAuth
import com.omarinc.shopify.network.currency.CurrencyRemoteDataSourceImpl
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupNavHost(
    navController: NavHostController,
    startDestination: String,
) {
    val apolloClient = remember { ApolloService.client }
    val sharedOrderViewModel: SharedOrderViewModel = viewModel()
    val context = LocalContext.current

    val logoutViewModel = remember {
        LogoutViewModel(
            AuthRepository(
                FirebaseAuth.getInstance(), apolloClient
            )
        )
    }

    NavHost(
        navController = navController, startDestination = startDestination
    ) {
        composable<ScreensRoute.WelcomeScreen> {
            WelcomeScreen(onSignInClick = { navController.navigate(ScreensRoute.LoginScreen) },
                onSignUpClick = { navController.navigate(ScreensRoute.SignUpScreen) },
                onSkipClick = {
                    Toast.makeText(context, "Login required to continue", Toast.LENGTH_SHORT).show()
                    navController.navigate(ScreensRoute.HomeScreen)
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
            val currentUser = FirebaseAuth.getInstance().currentUser
            val favouriteViewModel = remember(currentUser?.uid) {
                currentUser?.let {
                    FavouriteScreenViewModel(FavouriteRepositoryImpl(apolloClient))
                }
            }

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
            CartScreen(onBackClick = { navController.navigate(ScreensRoute.HomeScreen) },
                onCheckoutClick = { navController.navigate(ScreensRoute.CheckoutScreen) },
                onNavigateToOrders = { navController.navigate(ScreensRoute.OrderScreen) },
                onNavigateToAddresses = { navController.navigate(ScreensRoute.DeliveryAddressListScreen) },
                onNavigateToProductInfo = { productId ->
                    val encodedId = Uri.encode(productId)
                    navController.navigate("productInfo/$encodedId")
                }

            )
        }

        composable<ScreensRoute.CategoriesScreen> {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val favouriteViewModel = remember(currentUser?.uid) {
                currentUser?.let {
                    FavouriteScreenViewModel(FavouriteRepositoryImpl(apolloClient))
                }
            }

                CategoryScreen(
                    onCartClick = { navController.navigate(ScreensRoute.CartScreen) },
                    onProductClick = { productId ->
                        navController.navigate("productInfo/$productId")
                    },
                    onSearchClick = { navController.navigate(ScreensRoute.SearchScreen) },

                    favouriteViewModel = favouriteViewModel
                )


        }

        composable<ScreensRoute.FavouritesScreen> {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val favouriteViewModel = remember(currentUser?.uid) {
                currentUser?.let {
                    FavouriteScreenViewModel(FavouriteRepositoryImpl(apolloClient))
                }
            }

                FavouriteScreen(
                    viewModel = favouriteViewModel, navController = navController
                )

        }

        composable<ScreensRoute.BrandProductsScreen> { entry ->
            val id = entry.arguments?.getString("brandID") ?: ""
            val name = entry.arguments?.getString("brandName") ?: "Adidas"
            val image = entry.arguments?.getString("brandImage") ?: ""

            val currentUser = FirebaseAuth.getInstance().currentUser
            val favouriteViewModel = remember(currentUser?.uid) {
                currentUser?.let {
                    FavouriteScreenViewModel(FavouriteRepositoryImpl(apolloClient))
                }
            }

                BrandProductsScreen(
                    brandId = id,
                    brandName = name,
                    imageUrl = image,
                    onBack = { navController.popBackStack() },
                    onProductClick = { productId ->
                        navController.navigate("productInfo/$productId")
                    },
                    onSearchClick = {
                        val encodedBrand =
                            URLEncoder.encode(name, StandardCharsets.UTF_8.toString())
                        navController.navigate("search?brand=$encodedBrand")
                    },

                    favouriteViewModel = favouriteViewModel
                )
        }

        composable("productInfo/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            val variantId = backStackEntry.arguments?.getString("variantId") ?: ""
            val currentUser = FirebaseAuth.getInstance().currentUser
            val favouriteViewModel = remember(currentUser?.uid) {
                currentUser?.let {
                    FavouriteScreenViewModel(FavouriteRepositoryImpl(apolloClient))
                }
            }
            val repository = remember {
                HomeRepositoryImpl(RemoteDataSourceImpl(ApolloService.client))
            }
                ProductInfoScreen(
                    productId = productId, repository = repository, navController = navController,
                    //  variantId = variantId ,
                    favouriteViewModel = favouriteViewModel
                )

        }

        composable<ScreensRoute.ProfileScreen> {
            ProfileScreen(
                logoutViewModel = logoutViewModel,
                onFavClick = {
                    navController.navigate(ScreensRoute.FavouritesScreen)
                },
                onAddressClick = {
                    navController.navigate(ScreensRoute.DeliveryAddressListScreen)
                },
                onOrdersClick = {
                    navController.navigate(ScreensRoute.OrderScreen)
                },
                onLoggedOut = {
                    navController.navigate(ScreensRoute.WelcomeScreen) {
                        popUpTo(0)
                    }
                },
                onCurrencyClick ={
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
                    navController.navigate(ScreensRoute.DeliveryAddressListScreen)
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
            OrderScreen(onBack = { navController.popBackStack() }, onOrderClick = { selectedOrder ->
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
            val currentUser = FirebaseAuth.getInstance().currentUser
            val favouriteViewModel = remember(currentUser?.uid) {
                currentUser?.let {
                    FavouriteScreenViewModel(FavouriteRepositoryImpl(apolloClient))
                }
            }

            val remoteDataSource = remember { RemoteDataSourceImpl(apolloClient) }
            val searchRepository = remember { SearchRepositoryImpl(remoteDataSource) }
            val searchViewModel = remember { SearchViewModel(searchRepository) }

            if (favouriteViewModel != null) {
                SearchScreen(searchViewModel = searchViewModel,
                    favouriteViewModel = favouriteViewModel,
                    onProductClick = { productId ->
                        val encodedId =
                            URLEncoder.encode(productId, StandardCharsets.UTF_8.toString())
                        navController.navigate("productInfo/$encodedId")
                    },
                    onBack = {
                        navController.popBackStack()
                    })
            }
        }

        composable("search?brand={brand}") { backStackEntry ->
            val brand = backStackEntry.arguments?.getString("brand") ?: ""

            val currentUser = FirebaseAuth.getInstance().currentUser
            val favouriteViewModel = remember(currentUser?.uid) {
                currentUser?.let {
                    FavouriteScreenViewModel(FavouriteRepositoryImpl(apolloClient))
                }
            }

            val remoteDataSource = remember { RemoteDataSourceImpl(apolloClient) }
            val searchRepository = remember { SearchRepositoryImpl(remoteDataSource) }
            val searchViewModel = remember { SearchViewModel(searchRepository) }

            if (favouriteViewModel != null) {
                SearchScreen(brandFilter = brand,
                    searchViewModel = searchViewModel,
                    favouriteViewModel = favouriteViewModel,
                    onProductClick = { productId ->
                        val encodedId =
                            URLEncoder.encode(productId, StandardCharsets.UTF_8.toString())
                        navController.navigate("productInfo/$encodedId")
                    },
                    onBack = {
                        //  searchViewModel.clearSearch()
                        navController.popBackStack()
                    })
            }
        }



        composable<ScreensRoute.CurrencyScreen> {
            val viewModel: CurrencyViewModel = viewModel(
                factory = CurrencyViewModelFactory(
                    CurrencyRepo(
                        CurrencyRemoteDataSourceImpl(CurrencyRetrofitClient.getInstance().create(CurrencyApiService::class.java)
                        )

                    )
                )
            )

            CurrencyScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                })
        }


    }

}