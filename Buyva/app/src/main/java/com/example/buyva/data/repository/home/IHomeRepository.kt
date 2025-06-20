package com.example.buyva.data.repository.home

import com.example.buyva.BrandsAndProductsQuery
import com.example.buyva.GetProductByIdQuery
import kotlinx.coroutines.flow.Flow

interface IHomeRepository {
    fun getBrandsAndProduct(): Flow<BrandsAndProductsQuery.Data?>
    fun getProductById(productId: String): Flow<GetProductByIdQuery.Data?>


}