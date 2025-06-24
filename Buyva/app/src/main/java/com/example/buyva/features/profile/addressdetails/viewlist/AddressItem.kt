package com.example.buyva.features.profile.addressdetails.viewlist

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.buyva.data.model.Address
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.Gray
import com.example.buyva.utils.addressToJsonString
import com.example.buyva.utils.components.CustomAlertDialog
import com.example.buyva.utils.constants.DEFAULT_ADDRESS_ID
import com.example.buyva.utils.constants.USER_TOKEN
import com.example.buyva.utils.extensions.stripTokenFromShopifyGid
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl

@Composable
fun AddressItem(
    address: Address,
    isDefault: Boolean,
    onSetDefault: () -> Unit,
    onAddressDetailsClick: (String?, String?) -> Unit,
    onDeleteClick: () -> Unit
) {
    Log.d("1", "AddressItem called ${address.country}  and ${address.city}")
    var showDeleteDialog by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Gray),
    ) {
        Row(
            modifier = Modifier
                .clickable { onAddressDetailsClick(address.address1,addressToJsonString(address)) }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Name",
                        tint = Color(0xFF006A71),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${address.firstName} ${address.lastName}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = Color(0xFF006A71)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.Top) {

                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = address.address1,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        if (address.address2.isNotBlank()) {
                            Text(
                                text = address.address2,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${address.city}, ${address.country}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                    }
                }



            }
            Column(
                modifier = Modifier.padding(end = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = { onSetDefault() })
                    {
                    Icon(
                        imageVector = if (isDefault) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Set as Default",
                        tint = if (isDefault) Cold else Color.Gray
                    )
                }
                Text(
                    text = if (isDefault) "Default" else "Set",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isDefault) Cold else Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Cold
                    )
                }
                Text(
                    text = "Delete",
                    style = MaterialTheme.typography.labelSmall,
                    color = Cold
                )
            }

        }
    }

    if (showDeleteDialog) {
        CustomAlertDialog(
            title = "Delete Address",
            message = "Are you sure you want to delete this address?",
            onConfirm = {
                showDeleteDialog = false
                onDeleteClick()
            },
            onDismiss = { showDeleteDialog = false },
            confirmText = "Delete",
            dismissText = "Cancel"
        )
    }
}


