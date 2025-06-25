package com.example.buyva.features.categories.view

import ProductSection
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buyva.GetProductsByCategoryQuery
import com.example.buyva.R
import com.example.buyva.data.datasource.remote.RemoteDataSourceImpl
import com.example.buyva.data.datasource.remote.graphql.ApolloService
import com.example.buyva.data.model.Category
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.categories.CategoryRepositoryImpl
import com.example.buyva.features.categories.viewmodel.CategoryFactory
import com.example.buyva.features.categories.viewmodel.CategoryViewModel
import com.example.buyva.features.favourite.viewmodel.FavouriteScreenViewModel
import com.example.buyva.features.home.view.SearchBarWithCartIcon
import com.example.buyva.ui.theme.DarkGray
import com.example.buyva.ui.theme.Gray
import com.example.buyva.utils.components.EmptyScreen
import com.example.buyva.utils.components.LoadingIndicator
import com.example.buyva.utils.components.PriceFilterSlider
import com.example.buyva.utils.components.ScreenTitle
import com.example.buyva.utils.sharedpreference.currency.CurrencyManager

//val imageUrl = node.featuredImage?.url ?: ""
//                val price = (node.variants.edges.firstOrNull()?.node?.price?.amount as? Number)?.toFloat() ?: 0f
@Composable
fun CategoryScreen(
    onCartClick: () -> Unit,
    onProductClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    //onTextChanged: (String) -> Unit,
    favouriteViewModel: FavouriteScreenViewModel ?
) {

    CurrencyManager.loadFromPreferences()

    val currentRate = CurrencyManager.currencyRate.value.toFloat()
    val currentCurrency = CurrencyManager.currencyUnit.value

    var minSliderValue by remember { mutableFloatStateOf(0f) }
    var maxSliderValue by remember { mutableFloatStateOf(0f) }
    var selectedPriceLimit by remember { mutableFloatStateOf(0f) }

    var selectedCategory by remember { mutableStateOf("Men") }
    val selectedBackground = DarkGray.copy(alpha = 0.20f)
    val unselectedBackground = Color.Transparent
    val selectedSubcategories = remember { mutableStateMapOf<String, String?>() }

    val viewModelFactory = CategoryFactory(
        CategoryRepositoryImpl(RemoteDataSourceImpl(ApolloService.client))
    )
    val categoryViewModel: CategoryViewModel = viewModel(factory = viewModelFactory)

    val productsByCategory by categoryViewModel.productsByCategory.collectAsStateWithLifecycle()

    LaunchedEffect(selectedCategory) {
        categoryViewModel.getProductByCategory(selectedCategory.lowercase())
    }

    val categories = listOf(
        Category("Men", R.drawable.man),
        Category("Women", R.drawable.woman),
        Category("Kid", R.drawable.logo),
        Category("Sale", R.drawable.logo)
    )


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ScreenTitle("Categories")
            SearchBarWithCartIcon(
                onCartClick = onCartClick,onSearchClick = onSearchClick)
            Spacer(modifier = Modifier.height(12.dp))

                AnimatedVisibility(visible = maxSliderValue > 0f) {
                    PriceFilterSlider(
                        minPrice = minSliderValue,
                        maxPrice = maxSliderValue,
                        onPriceChange = { selectedPriceLimit = it },
                        modifier = Modifier.fillMaxWidth(),
                        currency = currentCurrency
                    )
                }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .fillMaxHeight()
                        .background(Gray)
                        .padding(vertical = 16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        categories.forEach { category ->
                            CategoryItem(
                                category = category,
                                isSelected = selectedCategory == category.name,
                                selectedSubcategory = selectedSubcategories[category.name],
                                onSubcategorySelect = { filter ->
                                    selectedSubcategories[category.name] = filter
                                    selectedCategory = category.name
                                },
                                backgroundColor = if (selectedCategory == category.name) selectedBackground else unselectedBackground
                            ) {
                                selectedSubcategories.keys.forEach { key ->
                                    if (key != category.name) {
                                        selectedSubcategories[key] = null
                                    }
                                }
                                selectedCategory = category.name
                            }


                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    when (val state = productsByCategory) {

                        is ResponseState.Failure -> {
                            Text(state.message.toString())
                        }

                        ResponseState.Loading -> LoadingIndicator()

                        is ResponseState.Success<*> -> {
                            val products = (state.data as List<GetProductsByCategoryQuery.Node>)
                            val rawPrices = products.mapNotNull {
                                it.variants.edges.firstOrNull()?.node?.price?.amount?.toString()?.toDoubleOrNull()
                            }

                            val minRaw = rawPrices.minOrNull() ?: 0.0
                            val maxRaw = rawPrices.maxOrNull() ?: 0.0

                            val minConverted = (minRaw * currentRate).toFloat()
                            val maxConverted = (maxRaw * currentRate).toFloat()

                            LaunchedEffect(minConverted, maxConverted) {
                                if (minSliderValue == 0f && maxSliderValue == 0f && maxConverted > 0f) {
                                    minSliderValue = minConverted
                                    maxSliderValue = maxConverted
                                    selectedPriceLimit = maxConverted
                                }
                            }

                            val selectedSubcategory = selectedSubcategories[selectedCategory]

                            val filtered = products.filter { product ->
                                val productType = product.productType
                                val rawPrice = product.variants.edges.firstOrNull()?.node?.price?.amount?.toString()?.toDoubleOrNull() ?: 0.0
                                val convertedPrice = (rawPrice * currentRate).toFloat()

                                val matchesSubcategory = selectedSubcategory?.let {
                                    productType.equals(it, ignoreCase = true)
                                } ?: true

                                matchesSubcategory && convertedPrice in minSliderValue..selectedPriceLimit
                            }

                            if (filtered.isEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                EmptyScreen(
                                    "No products found for $selectedSubcategory",
                                    16.sp,
                                    animation = R.raw.emptycart
                                )
                            } else {
                                ProductSection(
                                    products = filtered,
                                    onProductClick = onProductClick,
                                    favouriteViewModel = favouriteViewModel
                                )
                            }
                        }



                    }
                }

            }
        }
    }
}

