package com.example.buyva.repository.home

import com.example.buyva.BrandsAndProductsQuery
import com.example.buyva.GetProductByIdQuery
import com.example.buyva.ProductsByCollectionQuery
import com.example.buyva.admin.GetDiscountAmountDetailsQuery
import com.example.buyva.data.datasource.remote.RemoteDataSource
import com.example.buyva.data.repository.home.HomeRepositoryImpl
import com.example.buyva.data.repository.home.IHomeRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class HomeRepositoryTest {


    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var homeRepository: IHomeRepository

    @Before
    fun setUp() {
        remoteDataSource = mockk()
        homeRepository = HomeRepositoryImpl(remoteDataSource)
    }


    @Test
    fun getBrandsAndProduct_callsRemoteDataSource_returnBrandsAndProductsData() = runTest {
        val expectedData = mockk<BrandsAndProductsQuery.Data>()

        every {
            remoteDataSource.getBrandsAndProduct()

        } returns flowOf(expectedData)


        val result = homeRepository.getBrandsAndProduct()

        result.collect { data ->
            assertEquals(expectedData, data)
        }


    }


    @Test
    fun getBrandsAndProduct_whenRemoteFails_emitsNull() = runTest {
        coEvery {
            remoteDataSource.getBrandsAndProduct()
        } returns flow {
            throw RuntimeException("Simulated failure")
        }

        val result = homeRepository.getBrandsAndProduct()

        result.collect { data ->
            assertEquals(null, data)
        }
    }


    @Test
    fun getProductById_callsRemoteDataSource_returnProductByIdData() = runTest {
        val expectedData = mockk<GetProductByIdQuery.Data>()
        val productId = "123"
        every {
            remoteDataSource.getProductById(productId)
        } returns flowOf(expectedData)

        val result = homeRepository.getProductById(productId)

        result.collect { data ->
            assertEquals(expectedData, data)
        }
    }

    @Test
    fun getProductById_whenRemoteFails_emitsNull() = runTest {
        val productId = "123"
        coEvery {
            remoteDataSource.getProductById(productId)
        } returns flow {
            throw RuntimeException("Simulated failure")
        }
        val result = homeRepository.getProductById(productId)

        result.collect { data ->
            assertEquals(null, data)
        }
    }


    @Test
    fun getProductsByBrand_callsRemoteDataSource_returnProductsByBrandData() = runTest {
        val expectedData = mockk<ProductsByCollectionQuery.Data>()
        val collectionId = "123"

        every {
            remoteDataSource.getProductsByCollection(collectionId)
        } returns flowOf(expectedData)

        val result = homeRepository.getProductsByBrand(collectionId)

        result.collect { data ->
            assertEquals(expectedData, data)
        }

    }

    @Test
    fun getProductsByBrand_whenRemoteFails_emitsNull() = runTest {
        val collectionId = "123"
        coEvery {
            remoteDataSource.getProductsByCollection(collectionId)
        } returns flow {
            throw RuntimeException("Simulated failure")
        }
        val result = homeRepository.getProductsByBrand(collectionId)

        result.collect { data ->
            assertEquals(null, data)
        }
    }

    @Test
    fun getDiscountDetails_callsRemoteDataSource_returnDiscountDetailsData() = runTest {
        val expectedData = mockk<GetDiscountAmountDetailsQuery.Data>()
        coEvery {
            remoteDataSource.getDiscountDetails()

        } returns flowOf(expectedData)

        val result = homeRepository.getDiscountDetails()

        result.collect { data ->
            assertEquals(expectedData, data)
        }

    }


    @Test
    fun getDiscountDetails_whenRemoteThrows_exceptionIsPropagated() = runTest {
        coEvery {
            remoteDataSource.getDiscountDetails()
        } returns flow {
            throw RuntimeException("Simulated GraphQL error")
        }

        val result = homeRepository.getDiscountDetails()

        try {
            result.collect {}
            assert(false) { "Exception was expected but not thrown" }
        } catch (e: RuntimeException) {
            assertEquals("Simulated GraphQL error", e.message)
        }
    }


}