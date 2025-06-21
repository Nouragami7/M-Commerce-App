package com.example.buyva.utils.components

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.delay

@Composable
fun AnimatedProductItem(
    id: String,
    index: Int,
    product: Any,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current.density
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        visible = false
        delay(50)
        visible = true
    }

    val enterAnimation = when (id.hashCode() % 4) {
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
            modifier = modifier
                .graphicsLayer {
                    rotationY = if (index % 2 == 0) 10f else -10f
                    cameraDistance = 12f * density
                }
        ) {
            ProductCard(product = product, onProductClick = onProductClick)
        }
    }


