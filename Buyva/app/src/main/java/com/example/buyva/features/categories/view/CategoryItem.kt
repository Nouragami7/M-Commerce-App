package com.example.buyva.features.categories.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buyva.data.model.Category
import com.example.buyva.ui.theme.Cold


@Composable
fun CategoryItem(
    category: Category,
    isSelected: Boolean,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf<String?>(null) }

    val filtersWithIcons = listOf(
        "Clothes" to Icons.Filled.Checkroom,
        "Shoes" to Icons.Filled.DirectionsRun,
        "Glasses" to Icons.Filled.RemoveRedEye
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    expanded = !expanded
                    onClick()
                }
                .padding(vertical = 6.dp, horizontal = 8.dp)
        ) {
            Text(
                text = category.name,
                fontSize = 16.sp,
                color = when {
                    isSelected -> Cold
                    selectedFilter != null -> Color.DarkGray
                    else -> Color.Gray
                },
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = if (expanded) Cold else Color.Gray,
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(start = 12.dp, top = 8.dp)
            ) {
                filtersWithIcons.forEach { (filter, icon) ->
                    val isFilterSelected = filter == selectedFilter

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isFilterSelected) Cold.copy(alpha = 0.1f) else Color.Transparent
                            )
                            .clickable {
                                selectedFilter = if (isFilterSelected) null else filter
                            }
                            .padding(vertical = 6.dp, horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = filter,
                            tint = if (isFilterSelected) Cold else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = filter,
                            fontSize = 16.sp,
                            color = if (isFilterSelected) Cold else Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }

                }
            }
        }
    }
}

