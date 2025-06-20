package com.example.buyva.features.brand.view


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.buyva.ProductsByCollectionQuery
import kotlinx.coroutines.delay

@Composable
fun BrandOfProduct(products: List<ProductsByCollectionQuery.Node>, onProductClick: (String) -> Unit) {
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

                        val enterAnimation = when (product.id.hashCode() % 4) {
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
                            BrandProduct (product = product, onProductClick = onProductClick)
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


