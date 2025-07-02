package com.example.buyva.repository.search

import com.example.buyva.BrandsAndProductsQuery
import com.example.buyva.data.datasource.remote.RemoteDataSource
import com.example.buyva.data.model.UiProduct
import com.example.buyva.data.repository.search.ISearchRepository
import com.example.buyva.data.repository.search.SearchRepositoryImpl
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith

class SearchRepositoryTest {

    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var searchRepository: ISearchRepository

    @Before
    fun setUp() {
        remoteDataSource = mockk()
        searchRepository = SearchRepositoryImpl(remoteDataSource)
    }

    @Test
    fun getAllProducts_whenRemoteReturnsData_returnsMappedUiProducts() = runTest {
        val expectedUiProduct = UiProduct(
            id = "gid://shopify/Product/1",
            title = "Test Product",
            imageUrl = "https://example.com/image.jpg",
            price = 99.99f,
            vendor = "Nike"
        )
        val mockNode = mockk<com.example.buyva.BrandsAndProductsQuery.Node>(relaxed = true) {
            coEvery { id } returns expectedUiProduct.id
            coEvery { title } returns expectedUiProduct.title
            coEvery { vendor } returns expectedUiProduct.vendor
            coEvery { featuredImage?.url } returns expectedUiProduct.imageUrl

            coEvery { variants.edges } returns listOf(
                com.example.buyva.BrandsAndProductsQuery.Edge1(
                    node = mockk {
                        coEvery { price.amount.toString() } returns expectedUiProduct.price.toString()
                    }
                )
            )
        }

        val responseData = com.example.buyva.BrandsAndProductsQuery.Data(
            brands = com.example.buyva.BrandsAndProductsQuery.Brands(emptyList()),
            products = com.example.buyva.BrandsAndProductsQuery.Products(
                edges = listOf(
                    com.example.buyva.BrandsAndProductsQuery.Edge(
                        node = mockNode
                    )
                )
            )
        )

        coEvery {
            remoteDataSource.getBrandsAndProduct()
        } returns flowOf(responseData)

        val result = searchRepository.getAllProducts()

        result.collect { list ->
            assertEquals(1, list.size)
            assertEquals(expectedUiProduct.title, list.first().title)
        }
    }

    @Test
    fun getAllProducts_whenRemoteFails_throwsException() = runTest {
        coEvery {
            remoteDataSource.getBrandsAndProduct()
        } returns flow { throw RuntimeException("Simulated error") }

        try {
            searchRepository.getAllProducts().collect{}
            assert(false) { "Expected RuntimeException but none was thrown" }
        } catch (e: RuntimeException) {
            assertEquals("Simulated error", e.message)
        }
    }


    @Test
    fun searchProducts_whenRemoteReturnsData_returnsUiProductList() = runTest {
        val query = "shoes"
        val expectedList = listOf(
            UiProduct(
                id = "gid://shopify/Product/1",
                title = "Running Shoes",
                imageUrl = "https://example.com/shoe.jpg",
                price = 120.0f,
                vendor = "Nike"
            ),
            UiProduct(
                id = "gid://shopify/Product/2",
                title = "Casual Shoes",
                imageUrl = "https://example.com/casual.jpg",
                price = 90.0f,
                vendor = "Adidas"
            )
        )

        coEvery { remoteDataSource.searchProducts(query) } returns flowOf(expectedList)

        val result = searchRepository.searchProducts(query)

        result.collect { list ->
            assertEquals(expectedList, list)
        }
    }


    @Test
    fun searchProducts_whenRemoteFails_throwsException() = runTest {
        val query = "boots"

        coEvery {
            remoteDataSource.searchProducts(query)
        } returns flow { throw RuntimeException("GraphQL failure") }

        try {
            searchRepository.searchProducts(query).collect{}
            assert(false) { "Expected exception but none was thrown" }
        } catch (e: RuntimeException) {
            assertEquals("GraphQL failure", e.message)
        }
    }
}

