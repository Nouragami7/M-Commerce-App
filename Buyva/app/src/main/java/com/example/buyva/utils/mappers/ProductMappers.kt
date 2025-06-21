package com.example.buyva.utils.mappers

import com.example.buyva.GetProductByIdQuery
import com.example.buyva.data.model.FavouriteProduct

fun GetProductByIdQuery.Product.toFavouriteProduct(): FavouriteProduct {
    val imageUrl = this.images.edges.firstOrNull()?.node?.originalSrc?.toString() ?: ""
    val price = this.variants.edges.firstOrNull()?.node?.price?.amount?.toString() ?: "0.0"

    return FavouriteProduct(
        id = this.id,
        title = this.title,
        imageUrl = imageUrl,
        price = price
    )
}
