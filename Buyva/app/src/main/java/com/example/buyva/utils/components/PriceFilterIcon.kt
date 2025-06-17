package com.example.buyva.utils.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.buyva.R
import com.example.buyva.ui.theme.Cold
import androidx.compose.ui.Modifier

@Composable
fun PriceFilterIcon(
    onToggle: () -> Unit
) {
    FloatingActionButton(
        onClick = { onToggle() },
        containerColor = Cold,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(bottom = 60.dp, end = 20.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_filter_icon),
            contentDescription = "Toggle Price Filter",
            tint = Color.White
        )
    }
}
