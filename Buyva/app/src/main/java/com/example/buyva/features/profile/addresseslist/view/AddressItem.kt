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
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.Gray
import com.example.buyva.ui.theme.Sea
import com.example.buyva.ui.theme.Teal

//@Composable
//fun AddressItem(
//  //  address: Address,
//    onEditClick: () -> Unit,
//    onDeleteClick: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .padding(12.dp)
//            .fillMaxWidth(),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        shape = RoundedCornerShape(12.dp)
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text(address.placeName, style = MaterialTheme.typography.titleMedium)
//            Text("${address.city}, ${address.country}", style = MaterialTheme.typography.bodyMedium)
//            Text(address.fullAddress, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 8.dp),
//                horizontalArrangement = Arrangement.End
//            ) {
//                TextButton(onClick = onEditClick) {
//                    Icon(Icons.Default.Edit, contentDescription = "Edit")
//                    Spacer(Modifier.width(4.dp))
//                    Text("Edit")
//                }
//                TextButton(onClick = onDeleteClick) {
//                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
//                    Spacer(Modifier.width(4.dp))
//                    Text("Delete", color = Color.Red)
//                }
//            }
//        }
//    }
//}
