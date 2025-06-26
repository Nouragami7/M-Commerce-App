package com.example.buyva.data.repository.home

import com.example.buyva.BrandsAndProductsQuery
import com.example.buyva.GetProductByIdQuery
import com.example.buyva.ProductsByCollectionQuery
import com.example.buyva.admin.GetDiscountAmountDetailsQuery
import kotlinx.coroutines.flow.Flow

interface IHomeRepository {
    fun getBrandsAndProduct(): Flow<BrandsAndProductsQuery.Data?>
    fun getProductById(productId: String): Flow<GetProductByIdQuery.Data?>
    fun getProductsByBrand(collectionId: String): Flow<ProductsByCollectionQuery.Data?>
      suspend fun getDiscountDetails(): Flow<GetDiscountAmountDetailsQuery.Data>

    }