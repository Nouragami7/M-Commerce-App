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
import com.example.buyva.R

@Composable
fun ProductInfoScreen(modifier: Modifier = Modifier) {
    var selectedImage by remember { mutableStateOf<Int?>(null) }
    var isFavorite by remember { mutableStateOf(false) }
    var showAllReviews by remember { mutableStateOf(false) }
    var isAddedToCart by remember { mutableStateOf(false) }


    val allReviews = listOf(
        Review("Youssef", 5, "Amazing quality!"),
        Review("Sara", 4, "Very comfy and stylish."),
        Review("Ali", 4, "Great fit!"),
        Review("Lina", 3, "Looks nice."),
        Review("Omar", 5, "Perfect for school."),
        Review("Rana", 4, "Nice design."),
        Review("Salma", 5, "My son loves it."),
        Review("Tarek", 4, "Worth the price."),
        Review("Mona", 3, "Good quality."),
        Review("Ahmed", 5, "Highly recommended.")
    )

    val reviewsToShow = if (showAllReviews) allReviews else allReviews.take(2)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                ImageCarousel(
                    images = listOf(
                       R.drawable.shoe_placeholder,
                        R.drawable.shoe_placeholder,
                        R.drawable.shoe_placeholder
                    ),
                    onImageClick = { clickedImage -> selectedImage = clickedImage }
                )

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
                colors = CardDefaults.cardColors(containerColor = Color.LightGray),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "ADIDAS | KID'S STAN SMITH",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text("ADIDAS", color = Color.Gray)
                    Text("2000.0 EGP", color = Color(0xFF9C27B0), fontWeight = FontWeight.Bold)
                    Text("In Stock", color = Color(0xFF4CAF50), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "The Stan Smith owned the tennis court in the '70s. Today it runs the streets with the same clean, classic style...",
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Select Size",
                modifier = Modifier.padding(start = 16.dp),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("1", "2", "3", "4").forEach { size ->
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.defaultMinSize(minWidth = 48.dp)
                    ) {
                        Text(size)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Select Color",
                modifier = Modifier.padding(start = 16.dp),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ColorOption(Color.White, "White")
                ColorOption(Color.Black, "Black")
                ColorOption(Color.Red, "Red")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Reviews",
                modifier = Modifier.padding(start = 16.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                reviewsToShow.forEach {
                    ReviewItem(it)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                TextButton(onClick = { showAllReviews = !showAllReviews }) {
                    Text(
                        text = if (showAllReviews) "Show Less" else "More Reviews",
                        color = Color(0xFF48A6A7)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            OutlinedButton(
                onClick = {
                    isAddedToCart = !isAddedToCart
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isAddedToCart) Color(0xFF48A6A7) else Color.White,
                    contentColor = if (isAddedToCart) Color.White else Color(0xFF48A6A7)
                ),
                border = BorderStroke(1.dp, Color(0xFF48A6A7))
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add to Cart", fontWeight = FontWeight.Bold)
            }
        }
    }

    selectedImage?.let { imageRes ->
        FullscreenImageViewer(imageRes = imageRes) {
            selectedImage = null
        }
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
fun ImageCarousel(images: List<Int>, onImageClick: (Int) -> Unit) {
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
                Image(
                    painter = painterResource(id = images[page]),
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
                        .background(if (index == pagerState.currentPage) Color(0xFF9C27B0) else Color.LightGray)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@Composable
fun FullscreenImageViewer(imageRes: Int, onDismiss: () -> Unit) {
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
        Image(
            painter = painterResource(id = imageRes),
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
        ProductInfoScreen()
    }
}
