package com.example.buyva.features.search.view

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.buyva.features.favourite.viewmodel.FavouriteScreenViewModel
import com.example.buyva.features.search.viewmodel.SearchViewModel

@Composable
fun SearchScreenHost(
    navController: NavController,
    favouriteViewModel: FavouriteScreenViewModel,
    onProductClick: (String) -> Unit
) {

    val searchViewModel : SearchViewModel = hiltViewModel()

    SearchScreen(
        searchViewModel = searchViewModel,
        favouriteViewModel = favouriteViewModel,
        onProductClick = onProductClick,
        onBack = { navController.popBackStack() }
    )
}