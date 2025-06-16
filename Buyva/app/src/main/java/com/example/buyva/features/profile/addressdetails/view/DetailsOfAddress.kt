package com.example.buyva.features.profile.addressdetails.view

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.Gray
import com.example.buyva.ui.theme.Sea
import com.example.buyva.ui.theme.Teal

@Composable
fun AddressDetails(
    lat: Double,
    lon: Double,
    address: String?,
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    Log.d("AddressDetails", "lat: $lat, lon: $lon, address: $address")
    val firstName = remember { mutableStateOf("") }
    val lastName = remember { mutableStateOf("") }
    val phoneNumber = remember { mutableStateOf("") }
    val streetAddress = remember { mutableStateOf("") }
    val buildingNumber = remember { mutableStateOf("") }
    val floorNumber = remember { mutableStateOf("") }

    val textFieldHeight = 56.dp
    val labelFontSize = 14.sp
    val inputFontSize = 14.sp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add Delivery Address",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Cold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = firstName.value,
                onValueChange = { firstName.value = it },
                label = { Text("First Name", fontSize = labelFontSize) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                textStyle = TextStyle(fontSize = inputFontSize),
                modifier = Modifier
                    .weight(1f)
                    .height(textFieldHeight),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Sea,
                    focusedLabelColor = Sea
                )
            )

            OutlinedTextField(
                value = lastName.value,
                onValueChange = { lastName.value = it },
                label = { Text("Last Name", fontSize = labelFontSize) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                textStyle = TextStyle(fontSize = inputFontSize),
                modifier = Modifier
                    .weight(1f)
                    .height(textFieldHeight),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Sea,
                    focusedLabelColor = Sea
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phoneNumber.value,
            onValueChange = { phoneNumber.value = it },
            label = { Text("Phone Number", fontSize = labelFontSize) },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            textStyle = TextStyle(fontSize = inputFontSize),
            modifier = Modifier
                .fillMaxWidth()
                .height(textFieldHeight),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Sea,
                focusedLabelColor = Sea
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = streetAddress.value,
            onValueChange = { streetAddress.value = it },
            label = { Text("Street Address", fontSize = labelFontSize) },
            leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
            textStyle = TextStyle(fontSize = inputFontSize),
            modifier = Modifier
                .fillMaxWidth()
                .height(textFieldHeight),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Sea,
                focusedLabelColor = Sea
            )
        )

        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(
            value = buildingNumber.value,
            onValueChange = { buildingNumber.value = it },
            label = { Text("Building No.", fontSize = labelFontSize) },
            leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = TextStyle(fontSize = inputFontSize),
            modifier = Modifier
                .fillMaxWidth()
                .height(textFieldHeight),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Sea,
                focusedLabelColor = Sea
            )
        )
        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(
            value = floorNumber.value,
            onValueChange = { floorNumber.value = it },
            label = { Text("Floor No.", fontSize = labelFontSize) },
            leadingIcon = { Icon(Icons.Default.Stairs, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = TextStyle(fontSize = inputFontSize),
            modifier = Modifier
                .fillMaxWidth()
                .height(textFieldHeight),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Sea,
                focusedLabelColor = Sea
            )
        )


        Spacer(modifier = Modifier.height(70.dp))

        Button(
            onClick = {  onSaveClick()},
            modifier = Modifier
                .width(280.dp)
                .height(60.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Sea, Cold)
                        ),
                        shape = RoundedCornerShape(30.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Save Address",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
