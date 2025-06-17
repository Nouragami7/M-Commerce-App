package com.example.buyva.data.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf

data class CartItem(
    val imageRes: Int,
    val name: String,
    val price: Double,
    var quantity: MutableState<Int> = mutableIntStateOf(1)
)