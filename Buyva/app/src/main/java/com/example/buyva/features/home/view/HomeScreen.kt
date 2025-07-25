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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.buyva.BrandsAndProductsQuery
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.features.cart.cartList.viewmodel.CartViewModel
import com.example.buyva.features.favourite.viewmodel.FavouriteScreenViewModel
import com.example.buyva.features.home.viewmodel.HomeViewModel
import com.example.buyva.features.profile.addressdetails.viewmodel.AddressViewModel
import com.example.buyva.navigation.navbar.NavigationBar
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.ubuntuMedium
import com.example.buyva.utils.components.LoadingIndicator
import com.example.buyva.utils.components.ScreenTitle
import com.example.buyva.utils.constants.USER_TOKEN
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl

@Composable
fun HomeScreen(
    onCartClick: () -> Unit = {},
    onBrandClick: (String, String, String) -> Unit = { _, _, _ -> },
    onSearchClick: () -> Unit = {},
    onProductClick: (String) -> Unit = {},
    favouriteViewModel: FavouriteScreenViewModel?

) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val homeViewModel: HomeViewModel = hiltViewModel(
    )
//    val addressViewModel: CartViewModel = hiltViewModel()
//    addressViewModel.clearCart()

    SharedPreferenceImpl.getFromSharedPreferenceInGeneral(USER_TOKEN)
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
        SearchBarWithCartIcon(onCartClick, onSearchClick = onSearchClick)
        Spacer(modifier = Modifier.height(16.dp))
        if (banners.isNotEmpty()) {
            OfferBanner(banner = banners, snackbarHostState, scope = scope)
        } else {
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

