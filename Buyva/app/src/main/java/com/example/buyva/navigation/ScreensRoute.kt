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
    data class AddressDetails(val address: String, val editable: Boolean, val prefillData: String?, val city: String? = null, val country: String? = null) :ScreensRoute()
    @Serializable
    data object  DeliveryAddressListScreen: ScreensRoute()
    @Serializable
    data object  MapScreen: ScreensRoute()
    @Serializable
    data class BrandProductsScreen(val brandID: String,val brandName: String, val brandImage: String): ScreensRoute()
    @Serializable
    data object ProductInfoScreen:ScreensRoute()
    @Serializable
    data object OrderScreen: ScreensRoute()
    @Serializable
    data object OrderDetailsScreen: ScreensRoute()
    @Serializable
    data object SettingsScreen: ScreensRoute()
    @Serializable
    data object PaymentScreen: ScreensRoute()
    @Serializable
    data object CheckoutScreen: ScreensRoute()
    @Serializable
    data object SearchScreen : ScreensRoute()




}