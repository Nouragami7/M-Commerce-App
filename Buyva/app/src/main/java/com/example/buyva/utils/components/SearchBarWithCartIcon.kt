// SearchBarWithCartIcon.kt - Simplified
package com.example.buyva.features.home.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.buyva.R
import com.example.buyva.ui.theme.Cold

@Composable
fun SearchBarWithCartIcon(
    onCartClick: () -> Unit = {},
    onSearchClick: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 9.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            placeholder = { Text("Search") },
            leadingIcon = {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
            },
            readOnly = true,
            enabled = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.LightGray,
                disabledIndicatorColor = Color.LightGray,
                cursorColor = Color.Black
            )
            ,
            singleLine = true,
            trailingIcon = null,
            interactionSource = remember { MutableInteractionSource() }.also { source ->
                LaunchedEffect(source) {
                    source.interactions.collect {
                        if (it is androidx.compose.foundation.interaction.PressInteraction.Release) {
                            onSearchClick()
                        }
                    }
                }
            }
        )


        Spacer(modifier = Modifier.width(8.dp))

        IconButton(onClick = onCartClick) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_shopping_cart_24),
                contentDescription = "Cart",
                tint = Cold,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}