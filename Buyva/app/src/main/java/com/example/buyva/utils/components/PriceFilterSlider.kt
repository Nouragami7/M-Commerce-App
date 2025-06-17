package com.example.buyva.utils.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buyva.ui.theme.Cold


@Composable
fun PriceFilterSlider(
    maxPrice: Float,
    onPriceChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Slider(
            value = maxPrice,
            onValueChange = onPriceChange,
            valueRange = 0f..3000f,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleY = 0.6f
                },
            colors = SliderDefaults.colors(
                thumbColor = Cold,
                activeTrackColor = Cold,
                inactiveTrackColor = Color.LightGray
            )
        )
        Text(
            text = "Max Price: ${maxPrice.toInt()} EGP",
            modifier = Modifier.align(Alignment.End),
            fontSize = 12.sp,
            color = Color.DarkGray
        )
    }
}

