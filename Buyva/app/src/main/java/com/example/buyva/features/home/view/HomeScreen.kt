package com.example.buyva.features.home.view

import OfferBanner
import ProductSection
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buyva.BrandsAndProductsQuery
import com.example.buyva.data.datasource.remote.RemoteDataSourceImpl
import com.example.buyva.data.datasource.remote.graphql.ApolloService
import com.example.buyva.data.model.DiscountBanner
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.home.HomeRepositoryImpl
import com.example.buyva.features.favourite.viewmodel.FavouriteScreenViewModel
import com.example.buyva.features.home.viewmodel.HomeFactory
import com.example.buyva.features.home.viewmodel.HomeViewModel
import com.example.buyva.navigation.navbar.NavigationBar
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.ubuntuMedium
import com.example.buyva.utils.components.LoadingIndicator
import com.example.buyva.utils.components.ScreenTitle
import com.example.buyva.utils.constants.CURRENCY_RATE
import com.example.buyva.utils.constants.CURRENCY_UNIT
import com.example.buyva.utils.constants.USER_TOKEN
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl

@Composable
fun HomeScreen(
    onCartClick: () -> Unit = {},
    onBrandClick: (String,String, String) -> Unit = { _, _ ,_-> },
    onSearchClick: () -> Unit = {},
  //  onTextChanged: (String) -> Unit,
    onProductClick: (String) -> Unit = {},
    favouriteViewModel: FavouriteScreenViewModel ?

) {
    val viewModelFactory = HomeFactory(
        HomeRepositoryImpl(RemoteDataSourceImpl(ApolloService.client))
    )
    val homeViewModel: HomeViewModel = viewModel(factory = viewModelFactory)
    val currencyRate : Double = SharedPreferenceImpl.getLongFromSharedPreferenceInGeneral(CURRENCY_RATE)
    val currencyUnit : String =
        SharedPreferenceImpl.getFromSharedPreferenceInGeneral(CURRENCY_UNIT).toString()

    SharedPreferenceImpl.getFromSharedPreferenceInGeneral(USER_TOKEN)
    Log.i("1", "HomeScreen: ${SharedPreferenceImpl.getFromSharedPreferenceInGeneral(USER_TOKEN)}")
    val brandsAndProducts by homeViewModel.brandsAndProducts.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        NavigationBar.mutableNavBarState.value = true
        homeViewModel.getBrandsAndProduct()
        homeViewModel.fetchDiscounts()
    }
    val banners by homeViewModel.discountBanners.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
            .padding(bottom = 60.dp)
    ) {

        ScreenTitle("BuyVa")
        SearchBarWithCartIcon(onCartClick,onSearchClick = onSearchClick)
        Spacer(modifier = Modifier.height(16.dp))
            if (banners.isNotEmpty()) {
                OfferBanner(banner = banners)
            } else {
//                OfferBanner(
//                banner = listOf(
//                    DiscountBanner("Use Code: TEST1 for 10% Off", 10, "ACTIVE", "2025-06-25T12:00Z","2025-06-25T12:00Z"),
//                    DiscountBanner("Use Code: TEST2 for 20% Off", 20, "ACTIVE", "2025-06-25T12:00Z","2025-06-25T12:00Z")
//                ) )
//                CircularProgressIndicator(
//                    modifier = Modifier.padding(16.dp)
//                )
                LoadingIndicator()
                }
        Spacer(modifier = Modifier.height(16.dp))

        when (val state = brandsAndProducts) {
            is ResponseState.Failure -> {
                Text(text = state.message.toString())
            }
            ResponseState.Loading -> LoadingIndicator()
            is ResponseState.Success<*> -> {
                val (brands, products) = state.data as Pair<List<BrandsAndProductsQuery.Node3>, List<BrandsAndProductsQuery.Node>>
                val filteredBrands = brands.filter { it.title.lowercase() != "home page" }
                println("id +++++++++++++++++++++++ $filteredBrands.get(0).id")
                BrandSection(
                    brands = filteredBrands, onBrandClick = onBrandClick
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "For You",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Cold,
                    fontFamily = ubuntuMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProductSection(
                    products = products,
                    onProductClick = onProductClick,
                    favouriteViewModel = favouriteViewModel
                )
            }
        }
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewBuyVaHomeScreen() {
//        HomeScreen()
//
//}

