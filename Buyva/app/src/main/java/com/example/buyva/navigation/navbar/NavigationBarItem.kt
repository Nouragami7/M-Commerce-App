package com.example.buyva.navigation.navbar

import androidx.annotation.DrawableRes
import com.example.buyva.R
import com.example.buyva.navigation.ScreensRoute

data class NavigationBarItem(
    val route: ScreensRoute,
    @DrawableRes val icon: Int,
    @DrawableRes val selectedIcon: Int,
    val id: Int
){
    companion object{
        val menuItems = listOf(
            NavigationBarItem(ScreensRoute.HomeScreen, R.drawable.ic_baseline_home_24, R.drawable.ic_baseline_home_avd, 1),
            NavigationBarItem(ScreensRoute.CategoriesScreen, R.drawable.baseline_category_24, R.drawable.ic_baseline_category_avd,2),
            NavigationBarItem(ScreensRoute.FavouritesScreen, R.drawable.baseline_favorite_24, R.drawable.ic_baseline_fav_avd,3),
            NavigationBarItem(ScreensRoute.ProfileScreen, R.drawable.baseline_person_24, R.drawable.ic_baseline_profile_avd,5)

        )
    }
}