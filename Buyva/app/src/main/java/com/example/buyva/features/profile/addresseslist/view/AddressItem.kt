package com.example.buyva.features.profile.addresseslist.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import com.example.buyva.data.model.Address
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.Gray
import com.example.buyva.ui.theme.Sea
import com.example.buyva.ui.theme.Teal

@Composable
fun AddressItem(
    address: Address,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Gray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "${address.firstName} ${address.lastName}",
                style = MaterialTheme.typography.titleMedium,
                color = Cold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = address.address,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onDeleteClick,
                    border = BorderStroke(1.dp, Cold),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Cold)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                    Spacer(Modifier.width(6.dp))
                    Text("Delete")
                }
            }
        }
    }
}

