package com.example.buyva.features.ProductInfo.View

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.buyva.GetProductByIdQuery
import com.example.buyva.R
import com.example.buyva.data.datasource.remote.RemoteDataSourceImpl
import com.example.buyva.data.datasource.remote.graphql.ApolloService
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.AuthRepository
import com.example.buyva.data.repository.cart.CartRepo
import com.example.buyva.data.repository.cart.CartRepoImpl
import com.example.buyva.data.repository.home.IHomeRepository
import com.example.buyva.features.ProductInfo.viewmodel.ProductInfoViewModel
import com.example.buyva.features.ProductInfo.viewmodel.ProductInfoViewModelFactory
import com.example.buyva.features.favourite.viewmodel.FavouriteScreenViewModel
import com.example.buyva.navigation.ScreensRoute
import com.example.buyva.navigation.navbar.NavigationBar
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.Gray
import com.example.buyva.ui.theme.Sea
import com.example.buyva.utils.components.CustomAlertDialog
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProductInfoScreen(
    navController: NavController, productId: String,
    //  variantId: String,
    repository: IHomeRepository, favouriteViewModel: FavouriteScreenViewModel?
) {
    Log.i("1", "ProductInfoScreen productId: $productId")
    // Log.i("1", "ProductInfoScreen variantId: $variantId")
    val application = LocalContext.current.applicationContext as Application
    val authRepo: AuthRepository = AuthRepository(FirebaseAuth.getInstance(), ApolloService.client)
    val cartRepo: CartRepo =
        CartRepoImpl(RemoteDataSourceImpl(ApolloService.client), SharedPreferenceImpl)
    val factory =
        remember { ProductInfoViewModelFactory(application, repository, authRepo, cartRepo) }
    val viewModel: ProductInfoViewModel = viewModel(factory = factory)
    val state by viewModel.product.collectAsState()
    val addingState by viewModel.addingToCart.collectAsState()

    LaunchedEffect(addingState) {
        if (addingState is ResponseState.Success<*>) {
            navController.navigate(ScreensRoute.CartScreen)
        }
    }


    LaunchedEffect(productId) {
        NavigationBar.mutableNavBarState.value = false
        viewModel.fetchProduct(productId)
    }

    when (val result = state) {
        is ResponseState.Loading -> {
            // show loading
        }

        is ResponseState.Failure -> {
            Text("Error: ${result.message.message}")
        }

        is ResponseState.Success<*> -> {
            val product = result.data as? GetProductByIdQuery.Product
            if (product != null) {

                ProductInfoContent(
                    product = product,
                    navController = navController,
                    favouriteViewModel = favouriteViewModel,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun ProductInfoContent(
    product: GetProductByIdQuery.Product,
    navController: NavController,
    favouriteViewModel: FavouriteScreenViewModel?,
    viewModel: ProductInfoViewModel
) {
    var selectedImage by remember { mutableStateOf<String?>(null) }
    val favouriteProducts by favouriteViewModel?.favouriteProducts?.collectAsState() ?: remember {
        mutableStateOf(
            emptyList()
        )
    }
    val isFavorite = favouriteProducts.any { it.id == product.id }
    var showDeleteAlert by remember { mutableStateOf(false) }

    var isAddedToCart by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
    val cartId =
        SharedPreferenceImpl.getFromSharedPreferenceInGeneral("CART_ID_${userEmail.lowercase()}")

    val images = product.images.edges.map { it.node.originalSrc.toString() }
    val title = product.title
    val vendor = product.vendor
    val description = product.description
    val inStock = (product.totalInventory ?: 0) > 0

    val allVariants = product.variants.edges.map { it.node }
    val allSelectedOptions = allVariants.flatMap { it.selectedOptions }
    val price = allVariants.firstOrNull()?.price?.amount ?: "0.0"
    val currency = allVariants.firstOrNull()?.price?.currencyCode?.name ?: ""

    val availableSizes =
        allSelectedOptions.filter { it.name.equals("Size", ignoreCase = true) }.map { it.value }
            .distinct()

    val availableColors =
        allSelectedOptions.filter { it.name.equals("Color", ignoreCase = true) }.map { it.value }
            .distinct()

    var selectedSize by remember { mutableStateOf(availableSizes.firstOrNull()) }
    var selectedColor by remember { mutableStateOf(availableColors.firstOrNull()) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() }, modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Product Info",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Sea
                )
            }

            // Image + Favourite
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .padding(8.dp)
                ) {
                    ImageCarousel(images = images, onImageClick = { selectedImage = it })

                    Icon(imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.Gray,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(28.dp)
                            .clickable {
                                if (isFavorite) showDeleteAlert = true
                                else favouriteViewModel?.toggleFavourite(product.id)
                            })

                    if (showDeleteAlert) {
                        CustomAlertDialog(title = "Remove from favorites",
                            message = "Are you sure you want to remove this product from favorites?",
                            confirmText = "Remove",
                            dismissText = "Cancel",
                            onConfirm = {
                                favouriteViewModel?.toggleFavourite(product.id)
                                showDeleteAlert = false
                            },
                            onDismiss = { showDeleteAlert = false })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Gray),
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(vendor, color = Color.Gray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "$price $currency",
                        color = Cold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        if (inStock) "In Stock" else "Out of Stock",
                        color = if (inStock) Color.Red else Color.Gray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(description, fontSize = 16.sp, lineHeight = 22.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sizes
            if (availableSizes.isNotEmpty()) {
                Text(
                    "Select Size",
                    Modifier.padding(start = 16.dp),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableSizes.forEach { size ->
                        OutlinedButton(
                            onClick = { selectedSize = size },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (selectedSize == size) Sea else Color.White,
                                contentColor = if (selectedSize == size) Color.White else Sea
                            ),
                            border = BorderStroke(1.dp, Sea)
                        ) {
                            Text(size)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Colors
            if (availableColors.isNotEmpty()) {
                Text(
                    "Select Color",
                    Modifier.padding(start = 16.dp),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    availableColors.forEach { colorName ->
                        val colorValue = colorFromName(colorName)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(
                                    2.dp,
                                    if (selectedColor == colorName) Sea else Color.LightGray,
                                    CircleShape
                                )
                                .background(colorValue)
                                .clickable { selectedColor = colorName })
                            Text(colorName, fontSize = 12.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Bottom Add to Cart
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            OutlinedButton(
                onClick = {
                    if (selectedSize == null || selectedColor == null) {
                        Toast.makeText(context, "Please select size and color", Toast.LENGTH_SHORT)
                            .show()
                        return@OutlinedButton
                    }

                    val matchedVariant =
                        product.variants.edges.map { it.node }.firstOrNull { variant ->
                            val options =
                                variant.selectedOptions.associate { it.name.lowercase() to it.value.lowercase() }
                            val selectedSizeMatch = options["size"] == selectedSize?.lowercase()
                            val selectedColorMatch = options["color"] == selectedColor?.lowercase()
                            selectedSizeMatch && selectedColorMatch
                        }

                    val productVariantId = matchedVariant?.id

                    if (productVariantId == null) {
                        Toast.makeText(
                            context,
                            "No variant matches selected size/color",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@OutlinedButton
                    }

                    isAddedToCart = true
                    viewModel.addToCartById(
                        email = userEmail,
                        cartId = cartId,
                        quantity = 1,
                        variantId = productVariantId
                    )
                    Toast.makeText(context, "Product added to cart", Toast.LENGTH_SHORT).show()
                    navController.currentBackStackEntry?.savedStateHandle?.set("REFRESH_CART", true)
                    navController.navigate(ScreensRoute.CartScreen)
                }, modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),

                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isAddedToCart) Sea else Color.White,
                    contentColor = if (isAddedToCart) Color.White else Sea
                ), border = BorderStroke(1.dp, Sea)
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add to Cart", fontWeight = FontWeight.Bold)
            }
        }
    }

    selectedImage?.let {
        FullscreenImageViewer(imageUrl = it) { selectedImage = null }
    }
}


fun colorFromName(name: String): Color {
    return when (name.lowercase()) {
        "black" -> Color.Black
        "white" -> Color.White
        "red" -> Color.Red
        "blue" -> Color.Blue
        "green" -> Color.Green
        "yellow" -> Color.Yellow
        else -> Color.Gray
    }
}

@Composable
fun ImageCarousel(images: List<String>, onImageClick: (String) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            if (images.isEmpty()) {
                // Placeholder
                Image(
                    painter = painterResource(id = R.drawable.bag1),
                    contentDescription = "No Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                HorizontalPager(
                    state = pagerState, modifier = Modifier.fillMaxSize()
                ) { page ->
                    AsyncImage(model = images[page],
                        contentDescription = "Product Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { onImageClick(images[page]) })
                }
            }
        }

        if (images.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(images.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == pagerState.currentPage) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (index == pagerState.currentPage) Cold else Color.LightGray)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }
    }
}

@Composable
fun FullscreenImageViewer(imageUrl: String, onDismiss: () -> Unit) {
    var scale by remember { mutableStateOf(1f) }
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
        .clickable { onDismiss() }
        .pointerInput(Unit) {
            detectTransformGestures { _, _, zoom, _ -> scale *= zoom }
        }, contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .graphicsLayer(scaleX = scale, scaleY = scale)
        )
    }
}

