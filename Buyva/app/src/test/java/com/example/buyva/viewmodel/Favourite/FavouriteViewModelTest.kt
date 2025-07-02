package com.example.buyva.viewmodel.Favourite

import com.example.buyva.GetFavouriteProductsByIdsQuery
import com.example.buyva.data.repository.favourite.FavouriteRepository
import com.example.buyva.features.favourite.viewmodel.FavouriteScreenViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class FavouriteScreenViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private lateinit var repository: FavouriteRepository
    private lateinit var viewModel: FavouriteScreenViewModel

    private val sampleProduct = mockk<GetFavouriteProductsByIdsQuery.OnProduct>(relaxed = true).apply {
        every { id } returns "product_1"
    }

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        Dispatchers.setMain(testDispatcher)

    }

    @Test
    fun `toggleFavourite adds product if not already favourite`() = runTest {
        val fakeFlow = MutableSharedFlow<List<GetFavouriteProductsByIdsQuery.OnProduct>>(replay = 1)
        coEvery { repository.getFavourites() } returns fakeFlow
        coEvery { repository.addFavourite(any()) } just runs

        fakeFlow.emit(emptyList())

        viewModel = FavouriteScreenViewModel(repository)
        advanceUntilIdle()

        viewModel.toggleFavourite("product_1")
        advanceUntilIdle()

        coVerify { repository.addFavourite("product_1") }
    }


    @Test
    fun `observeFavourites updates state with products`() = testScope.runTest {
        val sampleProduct = mockk<GetFavouriteProductsByIdsQuery.OnProduct>(relaxed = true)
        every { sampleProduct.id } returns "product_1"

        val fakeFlow = MutableSharedFlow<List<GetFavouriteProductsByIdsQuery.OnProduct>>(replay = 1)
        fakeFlow.emit(listOf(sampleProduct))

        coEvery { repository.getFavourites() } returns fakeFlow


        val viewModel = FavouriteScreenViewModel(repository)
        advanceUntilIdle()

        assertEquals(1, viewModel.favouriteProducts.value.size)
        assertEquals("product_1", viewModel.favouriteProducts.value.first().id)
    }

    @Test
    fun `toggleFavourite removes product if already favourite`() = runTest {
        val fakeFlow = MutableSharedFlow<List<GetFavouriteProductsByIdsQuery.OnProduct>>(replay = 1)

        repository = mockk(relaxed = true)
        coEvery { repository.getFavourites() } returns fakeFlow
        coEvery { repository.removeFavourite("product_1") } just runs

        viewModel = FavouriteScreenViewModel(repository)

        val job = launch {
            viewModel.favouriteProducts.collect{}
        }

        advanceUntilIdle()

        fakeFlow.emit(listOf(sampleProduct))
        advanceUntilIdle()

        println("DEBUG isFavourite: ${viewModel.isFavourite("product_1")}")
        assert(viewModel.isFavourite("product_1"))

        viewModel.toggleFavourite("product_1")
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.removeFavourite("product_1") }

        job.cancel()
    }

    @Test
    fun `toggleFavourite adds product if not favourite`() = runTest {
        val fakeFlow = MutableSharedFlow<List<GetFavouriteProductsByIdsQuery.OnProduct>>(replay = 1)
        coEvery { repository.getFavourites() } returns fakeFlow
        coEvery { repository.addFavourite(any()) } just runs

        viewModel = FavouriteScreenViewModel(repository)
        fakeFlow.emit(emptyList())
        advanceUntilIdle()

        viewModel.toggleFavourite("product_1")
        advanceUntilIdle()

        coVerify { repository.addFavourite("product_1") }
    }
}
