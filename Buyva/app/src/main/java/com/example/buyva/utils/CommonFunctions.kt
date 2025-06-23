package com.example.buyva.utils

import android.net.Uri
import com.example.buyva.data.model.Address
import kotlinx.serialization.json.Json

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