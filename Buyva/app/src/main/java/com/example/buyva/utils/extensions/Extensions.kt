package com.example.buyva.utils.extensions

import android.location.Address

fun Address.getFullAddress(): String {
    return (0..maxAddressLineIndex).joinToString(", ") { getAddressLine(it) }
}

fun String.stripTokenFromShopifyGid(): String {
    return this.substringBefore("?")
}
