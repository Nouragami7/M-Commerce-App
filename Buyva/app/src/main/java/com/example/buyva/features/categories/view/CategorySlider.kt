package com.example.buyva.features.categories.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buyva.ui.theme.Cold


@Composable
fun CategorySlider(
    categories: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(categories) { category ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    shape = CircleShape,
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            if (selected == category) Cold else Color.LightGray
                        )
                        .clickable { onSelect(category) },
                    color = Color.Transparent
                ) {
                    // Replace with Image if available
                    Box(modifier = Modifier.fillMaxSize())
                }
                Text(text = category, fontSize = 12.sp)
            }
        }
    }
}
