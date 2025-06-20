package com.example.buyva.features.categories.view

import ProductSection
import SearchBarWithCartIcon
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
import com.example.buyva.ui.theme.DarkGray
import com.example.buyva.ui.theme.Gray
import com.example.buyva.utils.components.LoadingIndicator
import com.example.buyva.utils.components.PriceFilterIcon
import com.example.buyva.utils.components.PriceFilterSlider
import com.example.buyva.utils.components.ScreenTitle

@Composable
fun CategoryScreen(
    onCartClick: () -> Unit,
    onProductClick: (String) -> Unit
) {
    var maxPrice by remember { mutableFloatStateOf(2522f) }
    var selectedCategory by remember { mutableStateOf("Men") }
    var showSlider by remember { mutableStateOf(false) }
    val selectedBackground = DarkGray.copy(alpha = 0.20f)
    val unselectedBackground = Color.Transparent

    val viewModelFactory = CategoryFactory(
        CategoryRepositoryImpl(RemoteDataSourceImpl(ApolloService.client))
    )
    val categoryViewModel: CategoryViewModel = viewModel(factory = viewModelFactory)

    val productsByCategory by categoryViewModel.productsByCategory.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
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
                onCartClick = onCartClick
            )
            Spacer(modifier = Modifier.height(12.dp))

            AnimatedVisibility(visible = showSlider) {
                PriceFilterSlider(
                    maxPrice = maxPrice,
                    onPriceChange = { maxPrice = it },
                    modifier = Modifier.fillMaxWidth()
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
                                backgroundColor = if (selectedCategory == category.name) selectedBackground else unselectedBackground

                            ) {
                                selectedCategory = category.name
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    when(val state = productsByCategory){
                        is ResponseState.Failure -> {
                            Text(state.message.toString())
                        }
                        ResponseState.Loading -> LoadingIndicator()
                        is ResponseState.Success <*> -> {
                            val products = state.data as List<GetProductsByCategoryQuery.Node>
                            ProductSection(products = products, onProductClick = onProductClick)
                        }
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
            PriceFilterIcon(
                onToggle = { showSlider = !showSlider }
            )
        }
    }
}

