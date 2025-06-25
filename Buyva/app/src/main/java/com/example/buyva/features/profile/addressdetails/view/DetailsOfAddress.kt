package com.example.buyva.features.profile.addressdetails.view

import android.app.Application
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buyva.data.datasource.remote.RemoteDataSourceImpl
import com.example.buyva.data.datasource.remote.graphql.ApolloService
import com.example.buyva.data.model.Address
import com.example.buyva.data.repository.adresses.AddressRepoImpl
import com.example.buyva.features.profile.addressdetails.viewmodel.AddressViewModel
import com.example.buyva.features.profile.addressdetails.viewmodel.AddressViewModelFactory
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.Gray
import com.example.buyva.ui.theme.Sea
import com.example.buyva.ui.theme.Teal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buyva.data.datasource.remote.stripe.StripeClient
import com.example.buyva.utils.jsonStringToAddress
import kotlinx.serialization.Contextual


@Composable
fun AddressDetails(
    address: String?,
    city: String? = null,  //from map to add
    country: String? = null,  //from map to add
    editableTextFields: Boolean,
    prefillData: String? = null,  //from list to show and update
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
Log.d("1", "AddressDetails called address $address")
    val addressFromMap = remember { mutableStateOf(address) }

    val addressModel = remember(prefillData) {
        jsonStringToAddress(prefillData ?: "")
    }
Log.d("1", "AddressDetails called coutry  : ${country} from details and city  : ${city}")
    val viewModel: AddressViewModel = viewModel(
        factory = AddressViewModelFactory(
            application = LocalContext.current.applicationContext as Application,
            AddressRepoImpl(RemoteDataSourceImpl(ApolloService.client, StripeClient.api))
        )
    )

    val firstName = remember { mutableStateOf("") }
    val lastName = remember { mutableStateOf("") }
    val phoneNumber = remember { mutableStateOf("") }
    val streetAddress = remember { mutableStateOf("") }
    val buildingNumber = remember { mutableStateOf("") }
    val floorNumber = remember { mutableStateOf("") }
    val isEditable = remember { mutableStateOf(editableTextFields) }

    LaunchedEffect(prefillData) {
        val model = jsonStringToAddress(prefillData ?: "")
        model?.let {
            firstName.value = it.firstName
            lastName.value = it.lastName
            phoneNumber.value = it.phone
            streetAddress.value = it.address1
            val parts = it.address2.split(" ")
            buildingNumber.value = parts.getOrNull(2) ?: ""
            floorNumber.value = parts.getOrNull(5) ?: ""
            Log.d("1", "country  : ${it.country}    from details")
            Log.d("1", "city  : ${it.city}    from details")
            Log.d("1", "address1  : ${it.address1}    from details")

        }
    }


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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Delivery Address",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Cold
            )

            if (addressModel != null) {  //from list
                if (!isEditable.value) { // no edit
                    IconButton(onClick = { isEditable.value = true }) {
                        Icon(Icons.Default.Edit, tint = Cold, contentDescription = "Edit")
                    }
                } else {
                    TextButton(onClick = {
Log.d("1", "id  : ${addressModel.id}    from list")
                        val newAddress = Address(
                            id = addressModel.id ,
                            firstName = firstName.value,
                            lastName = lastName.value,
                            phone = phoneNumber.value,
                            address1 = streetAddress.value.toString(),
                            address2 = "Building number ${buildingNumber.value} Floor number ${floorNumber.value}",
                            city = addressModel.city,
                            country = addressModel.country
                        )
                        viewModel.saveAddress(newAddress)
                       onSaveClick()
                    }) {
                        Text("Save", color = Sea, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = firstName.value ?: "No data",
                onValueChange = { firstName.value = it },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Cold,
                    unfocusedBorderColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedLabelColor = Cold,
                ),
                enabled = isEditable.value,
                label = { Text("First Name", fontSize = labelFontSize) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                textStyle = TextStyle(fontSize = inputFontSize),
                modifier = Modifier.weight(1f).height(textFieldHeight)
            )


            OutlinedTextField(
                value = lastName.value,
                onValueChange = { lastName.value = it },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Cold,
                    unfocusedBorderColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedLabelColor = Cold,
                ),
                enabled = isEditable.value,
                label = { Text("Last Name", fontSize = labelFontSize) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                textStyle = TextStyle(fontSize = inputFontSize),
                modifier = Modifier.weight(1f).height(textFieldHeight)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phoneNumber.value,
            onValueChange = { phoneNumber.value = it },
            enabled = isEditable.value,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Cold,
                unfocusedBorderColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = Cold,
            ),
            label = { Text("Phone Number", fontSize = labelFontSize) },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            textStyle = TextStyle(fontSize = inputFontSize),
            modifier = Modifier.fillMaxWidth().height(textFieldHeight)
        )


        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = addressFromMap.value ?: streetAddress.value ?: "",
            onValueChange = { addressFromMap.value = it },
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Cold,
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = Cold,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Cold
            ),
            label = {
                Text(
                    text = "Address Information",
                    fontSize = labelFontSize,
                    fontWeight = FontWeight.Medium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Address",
                    tint = Cold
                )
            },
            textStyle = TextStyle(fontSize = inputFontSize),
            modifier = Modifier
                    .heightIn(min = 56.dp, max = 200.dp)
                .clip(RoundedCornerShape(12.dp))
                .fillMaxWidth()
                .background(Color.White),
            shape = RoundedCornerShape(12.dp),
            singleLine = false,
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(16.dp))



        OutlinedTextField(
            value = buildingNumber.value,
            onValueChange = { buildingNumber.value = it },
            enabled = isEditable.value,
            label = { Text("Building No.", fontSize = labelFontSize) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Cold,
                unfocusedBorderColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = Cold,
            ),
            leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = TextStyle(fontSize = inputFontSize),
            modifier = Modifier.fillMaxWidth().height(textFieldHeight)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = floorNumber.value,
            onValueChange = { floorNumber.value = it },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Cold,
                unfocusedBorderColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = Cold,
            ),
            enabled = isEditable.value,
            label = { Text("Floor No.", fontSize = labelFontSize) },
            leadingIcon = { Icon(Icons.Default.Stairs, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = TextStyle(fontSize = inputFontSize),
            modifier = Modifier.fillMaxWidth().height(textFieldHeight)
        )



        Spacer(modifier = Modifier.height(24.dp))

        if (isEditable.value && addressModel == null) {  // from map
            Button(
                onClick = {
                    val newAddress = Address(
                        firstName = firstName.value,
                        lastName = lastName.value,
                        phone = phoneNumber.value,
                        address1 = addressFromMap.value ?: "",
                        address2 = "Building number: ${buildingNumber.value} Floor number:  ${floorNumber.value}",
                        city = city ?: "",
                        country = country ?: ""
                    )

                    viewModel.saveAddress(newAddress)
                    onSaveClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Cold)
            ) {

                    Text(
                        text = "Save Address",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

