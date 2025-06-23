//package com.example.buyva.utils.mappers
//
//
//import com.example.buyva.GetProductsByIdsQuery
//import com.example.buyva.data.model.FavouriteProduct
//
//fun GetProductsByIdsQuery.Data.toFavouriteProductList(): List<FavouriteProduct> {
//    return nodes.filterIsInstance<GetProductsByIdsQuery.Node.Product>().map { product ->
//        FavouriteProduct(
//            id = product.id,
//            title = product.title,
//            imageUrl = product.featuredImage?.url?.toString() ?: "",
//            price = product.variants.edges.firstOrNull()?.node?.price?.amount.toString()
//        )
//    }
//}
