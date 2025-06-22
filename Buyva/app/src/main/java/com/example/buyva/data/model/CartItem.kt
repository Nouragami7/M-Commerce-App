package com.example.buyva.data.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf


data class CartItem(
    val id: String,
    val title: String,
    val imageUrl: String,
    val price: Double,
    var quantity: Int
)
