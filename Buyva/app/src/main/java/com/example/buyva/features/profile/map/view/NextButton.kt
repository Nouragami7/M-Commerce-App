package com.example.buyva.features.profile.map.view

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.buyva.ui.theme.Cold
import com.google.android.gms.maps.model.LatLng

@Composable
fun ConfirmButton(
    selectedLocation: LatLng?,
    updatedAddress: String?,
    back: () -> Unit,
    onSelected: (String?) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(top = 550.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Cold,
                contentColor = Color.White
            ),
            onClick = {
                selectedLocation?.let {
                    Log.d("1", "Selected Location: $it")
                    onSelected(updatedAddress)
                }
            }
        )
        {
            Text("Next")
        }
    }
}
