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

}