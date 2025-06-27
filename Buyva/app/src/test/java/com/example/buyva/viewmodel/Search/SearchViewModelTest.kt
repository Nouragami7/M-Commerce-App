@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.buyva.viewmodel.Search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.buyva.data.model.UiProduct
import com.example.buyva.data.repository.search.ISearchRepository
import com.example.buyva.features.search.viewmodel.SearchViewModel
import com.example.buyva.utils.sharedpreference.currency.CurrencyManager
import io.mockk.*
import androidx.compose.runtime.State

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: SearchViewModel
    private lateinit var repository: ISearchRepository

    private val sampleProduct = UiProduct(
        id = "1",
        title = "Shoes",
        imageUrl = "",
        price = 100.0f,
        vendor = "Nike"
    )

    private val currencyFlow = MutableStateFlow(1.0)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        repository = mockk(relaxed = true)

        mockkObject(CurrencyManager)

        // حل مشكلة type mismatch هنا
        every { CurrencyManager.currencyRate } returns object : State<Double> {
            override val value: Double
                get() = currencyFlow.value
        }

        coEvery { repository.getAllProducts() } returns flowOf(listOf(sampleProduct))
        coEvery { repository.searchProducts(any()) } returns flowOf(listOf(sampleProduct))

        viewModel = SearchViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `performSearch with query updates searchResults and filteredProducts`() = runTest {
        viewModel.performSearch("Shoe")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.searchResults.size)
        assertEquals("Shoes", state.searchResults.first().title)
        assertEquals(1, state.filteredProducts.size)
    }

    @Test
    fun `performSearch with empty query returns allProducts`() = runTest {
        viewModel.performSearch("")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.searchResults.size)
        assertEquals("Shoes", state.searchResults.first().title)
        assertEquals(1, state.filteredProducts.size)
    }

    @Test
    fun `updateSelectedPriceLimit updates uiState and filters products`() = runTest {
        viewModel.performSearch("")
        advanceUntilIdle()

        viewModel.updateSelectedPriceLimit(80f)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(80f, state.selectedPriceLimit)
        assertEquals(0, state.filteredProducts.size)
    }

}
