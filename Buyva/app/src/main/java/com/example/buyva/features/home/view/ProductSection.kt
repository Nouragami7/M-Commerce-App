import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.ubuntuMedium
import kotlinx.coroutines.delay
import com.example.buyva.R
import com.example.buyva.features.categories.view.Product

@Composable
fun ProductSection(products: List<Product>) {
    val density = LocalDensity.current.density

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val rows = products.chunked(2)

        rows.forEachIndexed { rowIndex, row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEachIndexed { index, product ->
                    key(product.id) {
                        var visible by remember { mutableStateOf(false) }

                        LaunchedEffect(product.id) {
                            visible = false
                            delay(50)
                            visible = true
                        }

                        val enterAnimation = when (product.id % 4) {
                            0 -> fadeIn(animationSpec = tween(600)) + slideInVertically(
                                initialOffsetY = { it * 2 },
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                            )
                            1 -> fadeIn(animationSpec = tween(700)) + slideInHorizontally(
                                initialOffsetX = { -it },
                                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                            )
                            2 -> fadeIn(animationSpec = tween(500)) + expandVertically()
                            else -> fadeIn(animationSpec = tween(800)) + scaleIn()
                        }

                        AnimatedVisibility(
                            visible = visible,
                            enter = enterAnimation,
                            exit = fadeOut(),
                            modifier = Modifier
                                .weight(1f)
                                .graphicsLayer {
                                    rotationY = if (index % 2 == 0) 10f else -10f
                                    cameraDistance = 12f * density
                                }
                        ) {
                            ProductCard(product = product)
                        }
                    }
                }

                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}


@Composable
fun ProductCard(product: Product, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(vertical = 4.dp)
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Image(
                painter = painterResource(id = product.imageResId),
                contentDescription = product.category,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.category,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                fontFamily = ubuntuMedium
            )
            Text(
                text = product.price,
                color = Cold,
                fontSize = 13.sp
            )
        }
    }
}
