package com.example.buyva.repository.cart
import com.example.buyva.GetCartDetailsQuery
import com.example.buyva.data.datasource.remote.RemoteDataSource
import com.example.buyva.data.model.CartItem
import com.example.buyva.data.model.SelectedOption
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.cart.CartRepo
import com.example.buyva.data.repository.cart.CartRepoImpl
import com.example.buyva.utils.constants.USER_TOKEN
import com.example.buyva.utils.sharedpreference.SharedPreference
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CartRepoImplTest {

    private lateinit var cartRepo: CartRepo
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var sharedPreference: SharedPreference

    @Before
    fun setUp() {
        remoteDataSource = mockk()
        sharedPreference = mockk()
        cartRepo = CartRepoImpl(remoteDataSource, sharedPreference)
    }

    @Test
    fun getCartProducts_callsRemoteDataSource_returnsResponse() = runTest {
        val cartId = "cart123"
        val expectedResponse = ResponseState.Success(mockk<GetCartDetailsQuery.Data>())
        coEvery { remoteDataSource.getCartDetails(cartId) } returns flowOf(expectedResponse)
        val result = cartRepo.getCartProducts(cartId)
          result.collect { response ->
              assertEquals(expectedResponse, response)
          }
        coVerify { remoteDataSource.getCartDetails(cartId) }
    }
    @Test
    fun createCart_callsRemoteDataSource_returnsResponse(): Unit = runTest {
        val email = "test@example.com"
        val token = "token123"
        val expectedResponse = ResponseState.Success("cartCreated")

coEvery {
    remoteDataSource.createCart(email, token)
} returns flowOf(expectedResponse)
val result = cartRepo.createCart(email, token)
        result.collect { response ->
            assertEquals(expectedResponse, response)
        }
    }
    @Test
    fun getCartProductList_success_returnsMappedCartItems() = runTest {
        val cartId = "cart123"

        val featuredImage = mockk<GetCartDetailsQuery.FeaturedImage>(relaxed = true)
        every { featuredImage.url } returns "image1.jpg"

        val product = mockk<GetCartDetailsQuery.Product>(relaxed = true)
        every { product.id } returns "product1"
        every { product.title } returns "Product 1"
        every { product.featuredImage } returns featuredImage

        val price = mockk<GetCartDetailsQuery.Price>(relaxed = true)
        every { price.amount } returns java.math.BigDecimal("10.99")

        val variant = mockk<GetCartDetailsQuery.OnProductVariant>(relaxed = true)
        every { variant.id } returns "variant1"
        every { variant.title } returns "Product Variant"
        every { variant.price } returns price
        every { variant.quantityAvailable } returns 10
        every { variant.selectedOptions } returns listOf(
            GetCartDetailsQuery.SelectedOption("Color", "Red"),
            GetCartDetailsQuery.SelectedOption("Size", "M")
        )
        every { variant.product } returns product

        val merchandise = mockk<GetCartDetailsQuery.Merchandise>(relaxed = true)
        every { merchandise.onProductVariant } returns variant

        val node = mockk<GetCartDetailsQuery.Node>(relaxed = true)
        every { node.id } returns "line1"
        every { node.quantity } returns 2
        every { node.merchandise } returns merchandise

        val edge = mockk<GetCartDetailsQuery.Edge>(relaxed = true)
        every { edge.node } returns node

        val lines = mockk<GetCartDetailsQuery.Lines>(relaxed = true)
        every { lines.edges } returns listOf(edge)

        val cart = mockk<GetCartDetailsQuery.Cart>(relaxed = true)
        every { cart.id } returns "cart123"
        every { cart.lines } returns lines


        coEvery { remoteDataSource.getCartDetails(cartId) } returns flowOf(ResponseState.Success(cart))

        val resultList = cartRepo.getCartProductList(cartId).toList()

        val expectedCartItem = CartItem(
            lineId = "line1",
            id = "product1",
            variantId = "variant1",
            title = "Product 1",
            imageUrl = "image1.jpg",
            price = 10.99,
            quantity = 2,
            quantityAvailable = 10,
            selectedOptions = listOf(
                SelectedOption("Color", "Red"),
                SelectedOption("Size", "M")
            )
        )

        assertEquals(ResponseState.Loading, resultList[0])
        assertEquals(ResponseState.Success(listOf(expectedCartItem)), resultList[1])
    }

    @Test
    fun getCartProductList_successWithNullCart_returnsFailure() = runTest {
        val cartId = "cart123"

        coEvery { remoteDataSource.getCartDetails(cartId) } returns flowOf(ResponseState.Success(null))

        val resultList = cartRepo.getCartProductList(cartId).toList()

        assertEquals(ResponseState.Loading, resultList[0])
        assertTrue(resultList[1] is ResponseState.Failure)
        assertEquals("Cart is null", (resultList[1] as ResponseState.Failure).message.message)
    }
    @Test
    fun getCartProductList_remoteReturnsFailure_returnsFailure() = runTest {
        val cartId = "cart123"
        val exception = Exception("Network error")

        coEvery { remoteDataSource.getCartDetails(cartId) } returns flowOf(ResponseState.Failure(exception))

        val resultList = cartRepo.getCartProductList(cartId).toList()

        assertEquals(ResponseState.Loading, resultList[0])
        assertTrue(resultList[1] is ResponseState.Failure)
        assertEquals("Unknown error", (resultList[1] as ResponseState.Failure).message.message)
    }

    @Test
    fun getCartProductList_remoteReturnsLoading_emitsLoadingTwice() = runTest {
        val cartId = "cart123"

        coEvery { remoteDataSource.getCartDetails(cartId) } returns flowOf(ResponseState.Loading)

        val resultList = cartRepo.getCartProductList(cartId).toList()

        assertEquals(ResponseState.Loading, resultList[0])
        assertEquals(ResponseState.Loading, resultList[1])
    }


    @Test
    fun writeCartIdToSharedPreferences_savesValueSuccessfully() = runTest {
        val key = "cartId"
        val value = "abc123"

        coEvery { sharedPreference.saveToSharedPreferenceInGeneral(key, value) } just Runs

        cartRepo.writeCartIdToSharedPreferences(key, value)

        coVerify { sharedPreference.saveToSharedPreferenceInGeneral(key, value) }
    }
    @Test
    fun readUserToken_returnsStoredToken() = runTest {
        val token = "userToken123"
coEvery {
    sharedPreference.saveToSharedPreferenceInGeneral(USER_TOKEN, token)
}
        coEvery { sharedPreference.getFromSharedPreferenceInGeneral(USER_TOKEN) } returns token

        val result = cartRepo.readUserToken()

        assertEquals(token, result)
    }
    @Test
    fun readUserToken_returnsEmptyStringIfNull() = runTest {
        coEvery { sharedPreference.getFromSharedPreferenceInGeneral(USER_TOKEN) } returns null

        val result = cartRepo.readUserToken()

        assertEquals("", result)
    }
    @Test
    fun removeProductFromCart_returnsSuccessResponse() = runTest {
        val cartId = "cart123"
        val lineId = "line123"
        val expectedResponse = ResponseState.Success(Unit)

        coEvery { remoteDataSource.removeProductFromCart(cartId, lineId) } returns flowOf(expectedResponse)

        val result = cartRepo.removeProductFromCart(cartId, lineId).toList()

        assertEquals(listOf(expectedResponse), result)
    }
    @Test
    fun removeProductFromCart_returnsFailure() = runTest {
        val cartId = "cart123"
        val lineId = "line123"
        val error = Throwable("Network error")

        coEvery {
            remoteDataSource.removeProductFromCart(cartId, lineId)
        } returns flowOf(ResponseState.Failure(error))

        val result = cartRepo.removeProductFromCart(cartId, lineId).toList()

        assertTrue(result[0] is ResponseState.Failure)
        assertEquals(error.message, (result[0] as ResponseState.Failure).message?.message)
    }
    @Test
    fun writeCartIdToSharedPreferences_emptyKeyOrValue_stillCallsSave() = runTest {
        val key = ""
        val value = ""

        coEvery { sharedPreference.saveToSharedPreferenceInGeneral(key, value) } just Runs

        cartRepo.writeCartIdToSharedPreferences(key, value)

        coVerify { sharedPreference.saveToSharedPreferenceInGeneral(key, value) }
    }

    @Test
    fun updateCartLine_returnsFailure() = runTest {
        val cartId = "cart123"
        val lineId = "line123"
        val quantity = 5
        val error = Throwable("Invalid line")

        coEvery {
            remoteDataSource.updateCartLine(cartId, lineId, quantity)
        } returns flowOf(ResponseState.Failure(error))

        val result = cartRepo.updateCartLine(cartId, lineId, quantity).toList()

        assertTrue(result[0] is ResponseState.Failure)
    }


    @Test
    fun updateCartLine_returnsExpectedResponse() = runTest {
        val cartId = "cart123"
        val lineId = "line123"
        val quantity = 3
        val expectedResponse = ResponseState.Success(Unit)

        coEvery { remoteDataSource.updateCartLine(cartId, lineId, quantity) } returns flowOf(expectedResponse)

        val result = cartRepo.updateCartLine(cartId, lineId, quantity).toList()

        assertEquals(listOf(expectedResponse), result)
    }


    @Test
    fun addToCartById_returnsExpectedResponse() = runTest {
        val cartId = "cart123"
        val quantity = 2
        val variantId = "variant456"
        val expectedResponse = ResponseState.Success(Unit)

        coEvery {
            remoteDataSource.addToCartById(cartId, quantity, variantId)
        } returns flowOf(expectedResponse)

        val result = cartRepo.addToCartById(cartId, quantity, variantId).toList()

        assertEquals(listOf(expectedResponse), result)
    }

    @Test
    fun addToCartById_returnsFailureResponse() = runTest {
        val cartId = "cart123"
        val quantity = 2
        val variantId = "variant456"
        val error = Throwable("Failed to add to cart")

        coEvery {
            remoteDataSource.addToCartById(cartId, quantity, variantId)
        } returns flowOf(ResponseState.Failure(error))

        val result = cartRepo.addToCartById(cartId, quantity, variantId).toList()

        assertTrue(result[0] is ResponseState.Failure)
        assertEquals("Failed to add to cart", (result[0] as ResponseState.Failure).message?.message)
    }


}
