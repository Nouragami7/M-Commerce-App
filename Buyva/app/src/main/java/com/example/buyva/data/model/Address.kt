package com.example.buyva.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Address(
    val id: String? = null,
    val firstName: String,
    val lastName: String,
    val address1: String,
    val address2: String,
    val country: String,
    val city: String,
    val phone: String
)
