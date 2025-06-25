package com.example.buyva.data.model

data class DiscountBanner(
    val code: String,
    val percentage: Int,
    val status: String,
    val startsAt: String,
    val endsAt: String?
)
