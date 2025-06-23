package com.example.buyva.data.repository.search

import com.example.buyva.BrandsAndProductsQuery
import com.example.buyva.data.datasource.remote.RemoteDataSource
import com.example.buyva.data.model.UiProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchRepositoryImpl(
    private val remoteDataSource: RemoteDataSource
) : ISearchRepository {

    override fun getAllProducts(): Flow<List<UiProduct>> {
        return remoteDataSource.getBrandsAndProduct().map { data ->
            val products = data?.products?.edges?.mapNotNull { edge ->
                val node = edge.node ?: return@mapNotNull null

                val imageUrl = node.featuredImage?.url ?: ""
                val price = (node.variants.edges.firstOrNull()?.node?.price?.amount as? Number)?.toFloat() ?: 0f

                UiProduct(
                    id = node.id,
                    title = node.title ?: "",
                    imageUrl = imageUrl.toString(),
                    price = price
                )
            } ?: emptyList()

            products
        }
    }
    override fun searchProducts(query: String): Flow<List<UiProduct>> {
        return remoteDataSource.searchProducts(query).map {
            it ?: emptyList()
        }
    }
}

