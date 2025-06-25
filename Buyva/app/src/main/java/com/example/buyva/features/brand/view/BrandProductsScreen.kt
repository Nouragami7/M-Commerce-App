package com.example.buyva.features.brand.view

import ProductSection
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.buyva.ProductsByCollectionQuery
import com.example.buyva.data.datasource.remote.RemoteDataSourceImpl
import com.example.buyva.data.datasource.remote.graphql.ApolloService
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.home.HomeRepositoryImpl
import com.example.buyva.features.brand.viewmodel.BrandFactory
import com.example.buyva.features.brand.viewmodel.BrandViewModel
import com.example.buyva.features.favourite.viewmodel.FavouriteScreenViewModel
import com.example.buyva.features.home.view.SearchBarWithCartIcon
import com.example.buyva.navigation.navbar.NavigationBar
import com.example.buyva.ui.theme.ubuntuMedium
import com.example.buyva.utils.components.LoadingIndicator
import com.example.buyva.utils.components.PriceFilterSlider
import com.example.buyva.utils.components.ScreenTitle


@Composable
fun BrandProductsScreen(
    brandId : String,
    brandName: String,
    imageUrl: String,
    onBack: () -> Unit,
    onProductClick: (String) -> Unit,
    favouriteViewModel: FavouriteScreenViewModel ?,
    onSearchClick: () -> Unit = {},
   // onTextChanged: (String) -> Unit


) {

    val egyptianBound = 300f
    val westernBound = 200f

    var showSlider by remember { mutableStateOf(true) }
    var maxPrice by remember { mutableFloatStateOf(egyptianBound) }

    val brandFactory = BrandFactory(
        HomeRepositoryImpl(RemoteDataSourceImpl(ApolloService.client))
    )

    val brandViewModel: BrandViewModel = viewModel(factory = brandFactory)

    val productsOfBrand by brandViewModel.productsOfBrand.collectAsStateWithLifecycle()



    LaunchedEffect(Unit) {
        NavigationBar.mutableNavBarState.value = false
        brandViewModel.getProductsByBrand(brandId)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(state = rememberScrollState())
    ) {
        ScreenTitle("Brand Products")
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 12.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
           AsyncImage(
               model = imageUrl,
               contentDescription = "Brand Image",
               modifier = Modifier
                   .size(35.dp)
                   .clip(CircleShape)
           )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = brandName,
                style = MaterialTheme.typography.titleLarge,
                fontFamily = ubuntuMedium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        SearchBarWithCartIcon(onSearchClick = onSearchClick)



        Spacer(modifier = Modifier.height(12.dp))

        AnimatedVisibility(visible = showSlider) {
            PriceFilterSlider(
                maxPrice = maxPrice,
                onPriceChange = { maxPrice = it },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (val state = productsOfBrand) {
            is ResponseState.Failure -> {
                Text(text = state.message.toString())
            }
            ResponseState.Loading -> LoadingIndicator()
            is ResponseState.Success<*> -> {
                val products = (state as? ResponseState.Success<List<ProductsByCollectionQuery.Node>>)?.data
                if (products != null) {
                    val filteredProducts = products.filter {
                        val priceString = it.variants.edges.firstOrNull()?.node?.price?.amount?.toString()
                        val price = priceString?.toFloatOrNull() ?: 0f
                        price <= maxPrice
                    }

                    ProductSection(products = filteredProducts, onProductClick = onProductClick, favouriteViewModel = favouriteViewModel)
                }

            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {

    }
}
