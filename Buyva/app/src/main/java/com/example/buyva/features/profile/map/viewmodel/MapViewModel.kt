package com.example.buyva.features.profile.map.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.model.LatLng

import java.io.IOException
import java.util.Locale

class MapViewModel : ViewModel() {

    private val _searchResults = mutableStateListOf<Address>()
    val searchResults: List<Address> get() = _searchResults

    fun setSearchResults(results: List<Address>) {
        _searchResults.clear()
        _searchResults.addAll(results)
    }

    fun selectLocation(address: Address): LatLng {
        return LatLng(address.latitude, address.longitude)
    }
}


