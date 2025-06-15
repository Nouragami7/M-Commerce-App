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
//fun DeliveryAddressListScreen(
//  //  addresses: List<AddressModel>,
//  //  onEditClick: (AddressModel) -> Unit,
//  //  onDeleteClick: (AddressModel) -> Unit,
//    onAddNewClick: () -> Unit
//) {
//    Box(modifier = Modifier.fillMaxSize()) {
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp)
//        ) {
//            items(addresses) { address ->
//                AddressCard(
//                    address = address,
//                    onEditClick = { onEditClick(address) },
//                    onDeleteClick = { onDeleteClick(address) }
//                )
//                Spacer(modifier = Modifier.height(12.dp))
//            }
//        }
//
//        FloatingActionButton(
//            onClick = {},
//            containerColor = Sea,
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .padding(16.dp)
//        ) {
//            Icon(Icons.Default.Add, contentDescription = "Add Address", tint = Color.White)
//        }
//    }
//}

@Composable
fun DeliveryAddressListScreen(
    onBackClick: () -> Unit = {},
    onAddressClick: () -> Unit = {}
) {

    Box(modifier = Modifier.fillMaxSize().padding(bottom = 100.dp)) {
        FloatingActionButton(
            onClick = {onAddressClick()},
            containerColor = Cold,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add Address",
                tint = Color.White
            )
        }
    }
}


//@Composable
//fun DeliveryAddressListScreen(
//    onBackClick: () -> Unit = {},
//    onAddressClick: () -> Unit = {},
//    //  addresses: List<Address> = sampleAddresses,
//    //  onEditClick: (Address) -> Unit = {},
//    //  onDeleteClick: (Address) -> Unit = {}
//) {
//    Scaffold(
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = { onAddressClick() },
//                containerColor = Color(0xFF1E88E5),
//                contentColor = Color.White
//            ) {
//                Icon(Icons.Default.Add, contentDescription = "Add Address")
//            }
//        }
//    ) { paddingValues ->
//        LazyColumn(
//            contentPadding = paddingValues,
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color(0xFFF5F5F5))
//        ) {
//            items(addresses) { address ->
//                AddressItem(
//                    address = address,
//                    onEditClick = {
//                        // onEditClick(address)
//                    },
//                    onDeleteClick = {
//                        //  onDeleteClick(address)
//                    }
//                )
//            }
//        }
//    }
//}
