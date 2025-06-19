package com.example.buyva.data.repository.home

import com.example.buyva.BrandsAndProductsQuery
import kotlinx.coroutines.flow.Flow

interface IHomeRepository {
    fun getBrandsAndProduct(): Flow<BrandsAndProductsQuery.Data?>
}