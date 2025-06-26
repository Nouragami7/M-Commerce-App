package com.example.buyva.features.profile.map.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.example.buyva.features.profile.map.viewmodel.MapViewModel
import com.example.buyva.utils.extensions.getFullAddress
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.io.IOException
import java.util.Locale


@Composable
fun MapScreen(
    back: () -> Unit,
    mapViewModel: MapViewModel,
    onSelected: (address: String, city: String?, country: String?) -> Unit
) {

    val context = LocalContext.current
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var updatedAddress by remember { mutableStateOf<String?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(30.0444, 31.2357), 5f) // default Cairo
    }

    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fusedClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLatLng, 15f)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                selectedLocation = latLng
                val geocoder = Geocoder(context, Locale.getDefault())
                val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                updatedAddress = addressList?.firstOrNull()?.getFullAddress()
            }
        ) {
            selectedLocation?.let {
                Marker(state = rememberMarkerState(position = it), title = "Selected Location")
            }
        }

        Column(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)) {

            SearchBar(
                query = searchQuery,
                onQueryChange = {
                    searchQuery = it
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val results = try {
                        geocoder.getFromLocationName(it, 5) ?: emptyList()
                    } catch (e: IOException) {
                        emptyList()
                    }
                    mapViewModel.setSearchResults(results)
                },
                searchResults = mapViewModel.searchResults,
                onAddressClick = { address ->
                    val latLng = mapViewModel.selectLocation(address)
                    selectedLocation = latLng
                    updatedAddress = address.getFullAddress()
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
                }
            )

            updatedAddress?.let {
                LocationCard(it)
            }
        }

        ConfirmButton(selectedLocation, updatedAddress, back) { address, city, country ->
            if (address != null) {
                onSelected(address, city, country)
            }
        }
        }
    }






