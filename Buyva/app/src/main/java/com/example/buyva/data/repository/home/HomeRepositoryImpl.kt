package com.example.buyva.data.repository.home

import com.example.buyva.BrandsAndProductsQuery
import com.example.buyva.GetProductByIdQuery
import com.example.buyva.ProductsByCollectionQuery
import com.example.buyva.admin.GetDiscountAmountDetailsQuery
import com.example.buyva.data.datasource.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(private val remoteDataSource: RemoteDataSource) :
    IHomeRepository {
    override fun getBrandsAndProduct(): Flow<BrandsAndProductsQuery.Data?> {
        return remoteDataSource.getBrandsAndProduct().catch {
            emit(null)
        }
    }

    override fun getProductById(productId: String): Flow<GetProductByIdQuery.Data?> {
        return remoteDataSource.getProductById(productId).catch {
            emit(null)
        }
    }

    override fun getProductsByBrand(collectionId: String): Flow<ProductsByCollectionQuery.Data?> {
        return remoteDataSource.getProductsByCollection(collectionId).catch {
            emit(null)
        }
    }

    override suspend fun getDiscountDetails(): Flow<GetDiscountAmountDetailsQuery.Data> {
        return remoteDataSource.getDiscountDetails()
    }

}