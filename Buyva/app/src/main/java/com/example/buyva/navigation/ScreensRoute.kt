package com.example.buyva.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class ScreensRoute{
    @Serializable
    data object WelcomeScreen : ScreensRoute()

    @Serializable
    data object LoginScreen : ScreensRoute()

    @Serializable
    data object SignUpScreen : ScreensRoute()

    @Serializable
    data object HomeScreen: ScreensRoute()
    @Serializable
    data object CartScreen: ScreensRoute()
    @Serializable
    data object CategoriesScreen: ScreensRoute()
    @Serializable
    data object FavouritesScreen: ScreensRoute()
    @Serializable
    data object ProfileScreen: ScreensRoute()
    @Serializable
    data class AddressDetails(val lat: Double, val lon: Double, val address: String): ScreensRoute()
    @Serializable
    data object  DeliveryAddressListScreen: ScreensRoute()
    @Serializable
    data object  MapScreen: ScreensRoute()


}