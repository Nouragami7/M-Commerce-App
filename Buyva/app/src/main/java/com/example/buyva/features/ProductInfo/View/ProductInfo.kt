package com.example.buyva.features.ProductInfo.View

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.buyva.GetProductByIdQuery
import com.example.buyva.R
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.home.IHomeRepository
import com.example.buyva.features.ProductInfo.viewmodel.ProductInfoViewModel
import com.example.buyva.features.ProductInfo.viewmodel.ProductInfoViewModelFactory
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.Gray
import com.example.buyva.ui.theme.Sea
import com.example.buyva.navigation.navbar.NavigationBar
import com.example.buyva.utils.components.ScreenTitle
@Composable
fun ProductInfoScreen(
    productId: String,
    repository: IHomeRepository // مرره من الـ NavGraph
) {
    val factory = remember { ProductInfoViewModelFactory(repository) }
    val viewModel: ProductInfoViewModel = viewModel(factory = factory)
    val state by viewModel.product.collectAsState()

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
                ProductInfoContent(product)
            }
        }
    }
}
@Composable
fun ProductInfoContent(product: GetProductByIdQuery.Product) {
    var selectedImage by remember { mutableStateOf<String?>(null) }
    var isFavorite by remember { mutableStateOf(false) }
    var showAllReviews by remember { mutableStateOf(false) }
    var isAddedToCart by remember { mutableStateOf(false) }

    val images = product.images.edges.mapNotNull { it.node.originalSrc?.toString() }
    val title = product.title
    val vendor = product.vendor
    val description = product.description
    val price = product.variants.edges.firstOrNull()?.node?.priceV2?.amount ?: "0.0"
    val currency = product.variants.edges.firstOrNull()?.node?.priceV2?.currencyCode?.name ?: ""
    val inStock = (product.totalInventory ?: 0) > 0

    val reviews = listOf(
        Review("Youssef", 5, "Amazing quality!"),
        Review("Sara", 4, "Very comfy and stylish."),
        Review("Omar", 5, "Perfect for school."),
        Review("Mona", 3, "Good quality.")
    )
    val reviewsToShow = if (showAllReviews) reviews else reviews.take(2)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
        ) {

            // 🔁 صور المنتج
            Box(modifier = Modifier.fillMaxWidth()) {
                ImageCarousel(images = images, onImageClick = { selectedImage = it })
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color.Red else Color.Gray,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(28.dp)
                        .clickable { isFavorite = !isFavorite }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Gray),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(vendor ?: "", color = Color.Gray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("$price $currency", color = Cold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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

            // 🔘 الأزرار الجانبية
            Text("Select Size", Modifier.padding(start = 16.dp), fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("S", "M", "L", "XL").forEach {
                    OutlinedButton(onClick = { }) {
                        Text(it)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Select Color", Modifier.padding(start = 16.dp), fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ColorOption(Color.White, "White")
                ColorOption(Color.Black, "Black")
                ColorOption(Color.Red, "Red")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ⭐ المراجعات
            Text("Reviews", Modifier.padding(start = 16.dp), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Column(Modifier.padding(horizontal = 16.dp)) {
                reviewsToShow.forEach {
                    ReviewItem(it)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                TextButton(onClick = { showAllReviews = !showAllReviews }) {
                    Text(if (showAllReviews) "Show Less" else "More Reviews", color = Sea)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // 🛒 زر الإضافة للسلة
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            OutlinedButton(
                onClick = { isAddedToCart = !isAddedToCart },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isAddedToCart) Sea else Color.White,
                    contentColor = if (isAddedToCart) Color.White else Sea
                ),
                border = BorderStroke(1.dp, Sea)
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

@Composable
fun ColorOption(color: Color, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.dp, Color.Gray)
                .background(color)
        )
        Text(label, fontSize = 12.sp)
    }
}

data class Review(val name: String, val rating: Int, val comment: String)

@Composable
fun ReviewItem(review: Review) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(review.name, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                repeat(review.rating) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(review.comment)
        }
    }
}
@Composable
fun ImageCarousel(images: List<String>, onImageClick: (String) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 8.dp)
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onImageClick(images[page]) }
            ) {
                AsyncImage(
                    model = images[page],
                    contentDescription = "Product Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Row(
            Modifier
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

@Composable
fun FullscreenImageViewer(imageUrl: String, onDismiss: () -> Unit) {
    var scale by remember { mutableStateOf(1f) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { onDismiss() }
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ -> scale *= zoom }
            },
        contentAlignment = Alignment.Center
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

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=360dp,height=800dp,dpi=440",
    name = "Product Info Screen Preview"
)
@Composable
fun PreviewProductInfoScreen() {
    MaterialTheme {
        ProductInfoScreen(
            productId = TODO(),
            repository = TODO()
        )
    }
}
