// SearchRepositoryImpl.kt
package com.example.buyva.data.repository.search

import com.example.buyva.data.datasource.remote.RemoteDataSource
import com.example.buyva.data.model.UiProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchRepositoryImpl
    @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : ISearchRepository {

    override fun getAllProducts(): Flow<List<UiProduct>> {
        return remoteDataSource.getBrandsAndProduct().map { data ->
            data?.products?.edges?.mapNotNull { edge ->
                edge.node?.let { node ->
                    val price = node.variants.edges.firstOrNull()?.node?.price?.amount
                        ?.toString()?.toFloatOrNull() ?: 0f

                    val imageUrl = node.featuredImage?.url ?: ""

                    UiProduct(
                        id = node.id,
                        title = node.title ?: "Untitled",
                        imageUrl = imageUrl.toString(),
                        price = price,
                        vendor = node.vendor ?: "Unknown"


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
                    price = product.price,
                    vendor = product.vendor
                )
            }
        }
    }
}