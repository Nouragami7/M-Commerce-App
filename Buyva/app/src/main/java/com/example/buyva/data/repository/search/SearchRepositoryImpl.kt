// SearchRepositoryImpl.kt
package com.example.buyva.data.repository.search

import com.example.buyva.data.datasource.remote.RemoteDataSource
import com.example.buyva.data.model.UiProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchRepositoryImpl(
    private val remoteDataSource: RemoteDataSource
) : ISearchRepository {

    override fun getAllProducts(): Flow<List<UiProduct>> {
        return remoteDataSource.getBrandsAndProduct().map { data ->
            data?.products?.edges?.mapNotNull { edge ->
                edge.node?.let { node ->
                    // Extract the first variant's price
                    val price = node.variants.edges.firstOrNull()?.node?.price?.amount
                        ?.toString()?.toFloatOrNull() ?: 0f

                    // Extract the featured image URL
                    val imageUrl = node.featuredImage?.url ?: ""

                    UiProduct(
                        id = node.id,
                        title = node.title ?: "Untitled",
                        imageUrl = imageUrl.toString(),
                        price = price
                    )
                }
            } ?: emptyList()
        }
    }

    override fun searchProducts(query: String): Flow<List<UiProduct>> {
        return remoteDataSource.searchProducts(query).map { products ->
            products.map { product ->
                UiProduct(
                    id = product.id,
                    title = product.title,
                    imageUrl = product.imageUrl,
                    price = product.price
                )
            }
        }
    }
}