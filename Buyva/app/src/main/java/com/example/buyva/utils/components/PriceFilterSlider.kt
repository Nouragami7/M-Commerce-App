package com.example.buyva.utils.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buyva.ui.theme.Cold


@Composable
fun PriceFilterSlider(
    minPrice: Float?,
    maxPrice: Float,
    onPriceChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    currency: String
) {

    var sliderValue by remember { mutableFloatStateOf(maxPrice) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        if (minPrice != null) {
            Slider(
                value = sliderValue,
                onValueChange = {
                    sliderValue = it
                    onPriceChange(it)
                },
                valueRange = minPrice..maxPrice,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { scaleY = 0.6f },
                colors = SliderDefaults.colors(
                    thumbColor = Cold,
                    activeTrackColor = Cold,
                    inactiveTrackColor = Color.LightGray
                )
            )
        }

        Text(
            text = "Price: ${"%.2f".format(sliderValue)} $currency",
            modifier = Modifier.align(Alignment.End),
            fontSize = 12.sp,
            color = Color.DarkGray,
            fontWeight = FontWeight.Bold
        )
    }
}



