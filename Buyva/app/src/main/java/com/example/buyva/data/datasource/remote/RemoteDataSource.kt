package com.example.buyva.data.datasource.remote

import com.example.buyva.BrandsAndProductsQuery
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    fun getBrandsAndProduct(): Flow<BrandsAndProductsQuery.Data?>
}