package com.example.buyva.viewmodel.category

import com.example.buyva.GetProductsByCategoryQuery
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.categories.ICategoryRepository
import com.example.buyva.features.categories.viewmodel.CategoryViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CategoryViewModelTest {

    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var categoryRepository: ICategoryRepository

    @Before
    fun setup() {
        categoryRepository = mockk()
        categoryViewModel = CategoryViewModel(categoryRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getProductByCategory_whenRepositoryReturnsData_getProductsOfCategorySuccess() = runTest {
        val node = mockk<GetProductsByCategoryQuery.Node>(relaxed = true)

        val edge = mockk<GetProductsByCategoryQuery.Edge> {
            every { this@mockk.node } returns node
        }

        val products = mockk<GetProductsByCategoryQuery.Products> {
            every { edges } returns listOf(edge)
        }

        val collectionByHandle = mockk<GetProductsByCategoryQuery.CollectionByHandle> {
            every { this@mockk.products } returns products
        }

        val response = mockk<GetProductsByCategoryQuery.Data> {
            every { this@mockk.collectionByHandle } returns collectionByHandle
        }

        coEvery { categoryRepository.getProductsByCategory("clothing") } returns flowOf(response)

        categoryViewModel.getProductByCategory("clothing")
        advanceUntilIdle()

        val state = categoryViewModel.productsByCategory.value
        assert(state is ResponseState.Success<*>)

        val productList = (state as ResponseState.Success<List<GetProductsByCategoryQuery.Node>>).data
        assertEquals(1, productList.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getProductByCategory_whenRepositoryThrowsException_setsFailureState() = runTest {
        val exception = RuntimeException("Something went wrong")
        coEvery { categoryRepository.getProductsByCategory("men") } throws exception

        categoryViewModel.getProductByCategory("men")
        advanceUntilIdle()

        val state = categoryViewModel.productsByCategory.value
        assert(state is ResponseState.Failure)

        val failure = state as ResponseState.Failure
        assertEquals("Something went wrong", failure.message.message)
    }

}