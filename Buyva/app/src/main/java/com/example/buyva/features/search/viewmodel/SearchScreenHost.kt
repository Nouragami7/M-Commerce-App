package com.example.buyva.features.search.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.example.buyva.data.datasource.remote.graphql.ApolloService
import com.example.buyva.data.datasource.remote.RemoteDataSourceImpl
import com.example.buyva.data.repository.search.SearchRepositoryImpl
import com.example.buyva.features.favourite.viewmodel.FavouriteScreenViewModel
import com.example.buyva.features.search.viewmodel.SearchViewModel

@Composable
fun SearchScreenHost(
    navController: NavController,
    favouriteViewModel: FavouriteScreenViewModel,
    onProductClick: (String) -> Unit
) {
    val apolloClient = remember { ApolloService.client }
    val remoteDataSource = remember { RemoteDataSourceImpl(apolloClient) }
    val repository = remember { SearchRepositoryImpl(remoteDataSource) }
    val searchViewModel = remember { SearchViewModel(repository) }

    SearchScreen(
        searchViewModel = searchViewModel,
        favouriteViewModel = favouriteViewModel,
        onProductClick = onProductClick,
        onBack = { navController.popBackStack() }
    )
}