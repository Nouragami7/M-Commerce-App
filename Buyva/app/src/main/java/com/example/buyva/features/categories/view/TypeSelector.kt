package com.example.buyva.features.categories.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buyva.ui.theme.Cold



@Composable
fun TypeSelector(
    types: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(types) { type ->
            Card(
                onClick = { onSelect(type) },
                colors = CardDefaults.cardColors(
                    containerColor = if (type == selected) Cold else Color.LightGray
                )
            ) {
                Text(
                    text = type,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}
