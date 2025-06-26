package com.example.buyva.features.profile.map.view

import android.location.Address
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.buyva.ui.theme.Cold
import com.example.buyva.utils.extensions.getFullAddress

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    searchResults: List<Address>,
    onAddressClick: (Address) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {

                    TextField(
                        value = query,
                        onValueChange = onQueryChange,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Cold
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search for places", color = Cold) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Cold
                            )
                        },
                        singleLine = true
                    )

            if (searchResults.isNotEmpty()) {
                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp)) {
                    items(searchResults) { address ->
                        val fullAddress = address.getFullAddress()
                        Text(
                            text = fullAddress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAddressClick(address); onQueryChange(fullAddress) }
                                .padding(12.dp),
                            color = Color(0xFF006A71),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Divider()
                    }
                }
            }
        }
    }
}
