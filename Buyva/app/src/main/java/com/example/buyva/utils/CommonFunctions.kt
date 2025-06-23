package com.example.buyva.utils

import android.content.Context
import android.location.Geocoder
import android.net.Uri
import com.example.buyva.data.model.Address
import kotlinx.serialization.json.Json
import java.util.Locale

fun addressToJsonString(address: Address): String {
    return Uri.encode(Json.encodeToString(address))
}
fun jsonStringToAddress(jsonString: String): Address? {
    return try {
        val decoded = Uri.decode(jsonString)
        Json.decodeFromString<Address>(decoded)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
fun getCityAndCountryFromLocation(context: Context, lat: Double, lon: Double): Pair<String?, String?> {
    val geocoder = Geocoder(context, Locale.getDefault())
    return try {
        val addresses = geocoder.getFromLocation(lat, lon, 1)
        if (!addresses.isNullOrEmpty()) {
            val city = addresses[0].locality
            val country = addresses[0].countryCode
            Pair(city, country)
        } else {
            Pair(null, null)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Pair(null, null)
    }
}
