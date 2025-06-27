package com.example.buyva.viewmodel.home

import com.example.buyva.BrandsAndProductsQuery
import com.example.buyva.admin.GetDiscountAmountDetailsQuery
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.home.IHomeRepository
import com.example.buyva.features.home.viewmodel.HomeViewModel
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

class HomeViewModelTest {

    private lateinit var homeRepository: IHomeRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        homeRepository = mockk()
        viewModel = HomeViewModel(homeRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getBrandsAndProduct_callsHomeRepository_returnsBrandsAndProducts() = runTest {
        val brands = listOf(mockk<BrandsAndProductsQuery.Node3>())
        val productNode = mockk<BrandsAndProductsQuery.Node>()
        val productEdge = mockk<BrandsAndProductsQuery.Edge> {
            every { node } returns productNode
        }

        val mockData = mockk<BrandsAndProductsQuery.Data> {
            every { this@mockk.brands.nodes } returns brands
            every { this@mockk.products.edges } returns listOf(productEdge)
        }


        coEvery {
            homeRepository.getBrandsAndProduct()
        } returns flowOf(mockData)

        viewModel.getBrandsAndProduct()

        advanceUntilIdle()
        val state = viewModel.brandsAndProducts.value

        assert(state is ResponseState.Success<*>)

        val successState =
            state as ResponseState.Success<Pair<List<BrandsAndProductsQuery.Node1>, List<BrandsAndProductsQuery.Node>>>
        assertEquals(brands, successState.data.first)
        assertEquals(listOf(productNode), successState.data.second)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getBrandsAndProduct_whenRepositoryThrowsDuringCollect_emitsFailure() = runTest {
        val data = mockk<BrandsAndProductsQuery.Data>()
        coEvery {
            homeRepository.getBrandsAndProduct()
        } returns flowOf(data)

        every { data.brands.nodes } throws RuntimeException("Simulation of caught error")

        viewModel.getBrandsAndProduct()
        advanceUntilIdle()

        val state = viewModel.brandsAndProducts.value
        assert(state is ResponseState.Failure)
        val failure = state as ResponseState.Failure
        assertEquals("Simulation of caught error", failure.message.message)
    }



    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun fetchDiscounts_whenRepositoryReturnsData_setsDiscountBanners() = runTest {
        val discountPercentage = mockk<GetDiscountAmountDetailsQuery.OnDiscountPercentage> {
            every { percentage } returns 0.1
        }

        val value = mockk<GetDiscountAmountDetailsQuery.Value> {
            every { onDiscountPercentage } returns discountPercentage
        }

        val customerGets = mockk<GetDiscountAmountDetailsQuery.CustomerGets> {
            every { this@mockk.value } returns value
        }

        val discountCodeBasic = mockk<GetDiscountAmountDetailsQuery.OnDiscountCodeBasic> {
            every { title } returns "SAVE10"
            every { status.rawValue } returns "ACTIVE"
            every { startsAt.toString() } returns "2024-01-01"
            every { endsAt?.toString() } returns "2024-12-31"
            every { this@mockk.customerGets } returns customerGets
        }

        val discountUnion = mockk<GetDiscountAmountDetailsQuery.Discount> {
            every { onDiscountCodeBasic } returns discountCodeBasic
        }

        val node = mockk<GetDiscountAmountDetailsQuery.Node> {
            every { discount } returns discountUnion
        }

        val edge = mockk<GetDiscountAmountDetailsQuery.Edge> {
            every { this@mockk.node } returns node
        }

        val discountNodes = mockk<GetDiscountAmountDetailsQuery.DiscountNodes> {
            every { edges } returns listOf(edge)
        }

        val data = mockk<GetDiscountAmountDetailsQuery.Data> {
            every { this@mockk.discountNodes } returns discountNodes
        }

        coEvery { homeRepository.getDiscountDetails() } returns flowOf(data)

        viewModel.fetchDiscounts()
        advanceUntilIdle()

        val result = viewModel.discountBanners.value
        println("DEBUG: discountBanners = $result")

        assertEquals(1, result.size)
        assertEquals("Use Code: SAVE10 for 10% Off", result[0].code)
        assertEquals(10, result[0].percentage)
        assertEquals("ACTIVE", result[0].status)
        assertEquals("2024-01-01", result[0].startsAt)
        assertEquals("2024-12-31", result[0].endsAt)
    }



}