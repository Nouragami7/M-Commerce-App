package com.example.buyva.viewmodel.brand

import com.example.buyva.ProductsByCollectionQuery
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.home.IHomeRepository
import com.example.buyva.features.brand.viewmodel.BrandViewModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class BrandViewModelTest {
    private lateinit var homeRepository: IHomeRepository
    private lateinit var viewModel: BrandViewModel
    private val testDispatcher = StandardTestDispatcher()


    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        homeRepository = mockk()
        viewModel = BrandViewModel(homeRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getProductsByBrand_whenRepositoryReturnsData_getProductsOfBrandSuccess() = runTest {
        val productNode = mockk<ProductsByCollectionQuery.Node>(relaxed = true)

        val edge = mockk<ProductsByCollectionQuery.Edge> {
            coEvery { node } returns productNode
        }

        val products = mockk<ProductsByCollectionQuery.Products> {
            coEvery { edges } returns listOf(edge)
        }

        val collection = mockk<ProductsByCollectionQuery.Collection> {
            coEvery { this@mockk.products } returns products
        }

        val data = mockk<ProductsByCollectionQuery.Data> {
            coEvery { this@mockk.collection } returns collection
        }

        coEvery { homeRepository.getProductsByBrand("123") } returns flowOf(data)

        viewModel.getProductsByBrand("123")
        advanceUntilIdle()

        val state = viewModel.productsOfBrand.value
        assert(state is ResponseState.Success<*>)
        val productList =
            (state as ResponseState.Success<List<ProductsByCollectionQuery.Node>>).data
        assertEquals(1, productList.size)


    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getProductsByBrand_whenRepositoryReturnsNull_getFailure() = runTest {
        val data = mockk<ProductsByCollectionQuery.Data> {
            coEvery { collection } returns null
        }

        coEvery { homeRepository.getProductsByBrand("123") } returns flowOf(data)

        viewModel.getProductsByBrand("123")
        advanceUntilIdle()

        val state = viewModel.productsOfBrand.value
        assert(state is ResponseState.Failure)
    }
}