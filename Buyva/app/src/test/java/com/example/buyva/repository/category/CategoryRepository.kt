package com.example.buyva.repository.category

import com.example.buyva.GetProductsByCategoryQuery
import com.example.buyva.data.datasource.remote.RemoteDataSource
import com.example.buyva.data.repository.categories.CategoryRepositoryImpl
import com.example.buyva.data.repository.categories.ICategoryRepository
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CategoryRepository {

    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var categoryRepository: ICategoryRepository


    @Before
    fun setUp() {
        remoteDataSource = mockk()
        categoryRepository = CategoryRepositoryImpl(remoteDataSource)
    }

    //Test success
    @Test
    fun getProductsByCategory_callsRemoteDataSource_returnProductsByCategoryData() = runTest {
        val expectedData = mockk<GetProductsByCategoryQuery.Data>()
        val handle = "men"

        coEvery {
            remoteDataSource.getProductsByCategory(handle)
        } returns flowOf(expectedData)

        val result = categoryRepository.getProductsByCategory(handle)

        result.collect { data ->
            assertEquals(expectedData, data)
        }

    }

    //Test failure
    @Test
    fun getProductsByCategory_callsRemoteDataSource_returnNull() = runTest {
        val expectedData = null
        val handle = "women"

        coEvery {
            remoteDataSource.getProductsByCategory(handle)
        } returns flow{
            throw RuntimeException("Simulated GraphQL error")
        }


        val result = categoryRepository.getProductsByCategory(handle)

        result.collect { data ->
            assertEquals(expectedData, data)
        }

    }
}