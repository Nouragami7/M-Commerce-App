package com.example.buyva.viewmodel.cart

import android.app.Application
import com.example.buyva.GetCartDetailsQuery
import com.example.buyva.data.model.Address
import com.example.buyva.data.model.CartItem
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.adresses.IAddressRepo
import com.example.buyva.data.repository.cart.CartRepo
import com.example.buyva.features.cart.cartList.viewmodel.CartViewModel
import com.example.buyva.utils.constants.DEFAULT_ADDRESS_ID
import com.example.buyva.utils.constants.USER_TOKEN
import com.example.buyva.utils.extensions.stripTokenFromShopifyGid
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.bouncycastle.util.test.SimpleTest.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull


@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    private lateinit var viewModel: CartViewModel
    private lateinit var cartRepository: CartRepo
    private lateinit var addressRepository: IAddressRepo
    private lateinit var application: Application
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        cartRepository = mockk(relaxed = false)
        addressRepository = mockk(relaxed = false)
        application = mockk(relaxed = true)

        val firebaseUser = mockk<FirebaseUser> {
            every { email } returns "test@example.com"
        }
        val firebaseAuth = mockk<FirebaseAuth> {
            every { currentUser } returns firebaseUser
        }
        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns firebaseAuth

        // Mock SharedPreferences
        mockkObject(SharedPreferenceImpl)
        every { SharedPreferenceImpl.getFromSharedPreferenceInGeneral(any()) } returns "cart123"
        every { SharedPreferenceImpl.getFromSharedPreferenceInGeneral(USER_TOKEN) } returns "test_token"

        // Mock extension function
       // mockkStatic("com.example.buyva.utils.extensions")
       // every { any<String>().stripTokenFromShopifyGid() } answers { firstArg<String>().substringBefore("?") }

        viewModel = CartViewModel(application, cartRepository, addressRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }


    @Test
    fun `showCart when cart id is blank sets failure`() = testScope.runTest {
        val email = "test@example.com"
        val user = mockk<FirebaseUser> {
            every { this@mockk.email } returns email
        }
        val auth = mockk<FirebaseAuth> {
            every { currentUser } returns user
        }
        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns auth

        mockkObject(SharedPreferenceImpl)
        every { SharedPreferenceImpl.getFromSharedPreferenceInGeneral("CART_ID_$email") } returns null

        viewModel = CartViewModel(application, cartRepository, addressRepository)
        viewModel.showCart()
        advanceUntilIdle()

        val state = viewModel.cartProducts.value
        assert(state is ResponseState.Failure)
        assertEquals("Add first item to the cart!", (state as ResponseState.Failure).message.message)
    }



    @Test
    fun `showCart when successful sets products`() = testScope.runTest {
        val testProducts = listOf(mockk<CartItem>())

        coEvery { cartRepository.getCartProductList("cart123") } returns flowOf(
            ResponseState.Success<List<CartItem>>(testProducts)
        )

        viewModel.showCart()
        advanceUntilIdle()

        assert(viewModel.cartProducts.value is ResponseState.Success<*>)
        assertEquals(testProducts, (viewModel.cartProducts.value as ResponseState.Success<*>).data)
    }

//    @Test
//    fun `showCart handles repository error`() = testScope.runTest {
//        coEvery { cartRepository.getCartProductList("cart123") } returns flowOf(
//            ResponseState.Failure(Throwable("Network error"))
//        )
//
//        viewModel.showCart()
//        advanceUntilIdle()
//
//        assert(viewModel.cartProducts.value is ResponseState.Failure)
//        assertEquals("Network error", (viewModel.cartProducts.value as ResponseState.Failure))
//    }

    // Test cases for removeProductFromCart()
    @Test
    fun `removeProductFromCart when successful calls showCart`() = testScope.runTest {
        val testProducts = listOf(mockk<CartItem>())

        coEvery { cartRepository.removeProductFromCart("cart123", any()) } returns flowOf(
            ResponseState.Success<Unit>(Unit)
        )
        coEvery { cartRepository.getCartProductList("cart123") } returns flowOf(
            ResponseState.Success<List<CartItem>>(testProducts)
        )

        viewModel.removeProductFromCart("line123")
        advanceUntilIdle()

        assert(viewModel.cartProducts.value is ResponseState.Success<*>)
    }

    @Test
    fun `removeProductFromCart handles failure`() = testScope.runTest {
        coEvery { cartRepository.removeProductFromCart("cart123", any()) } returns flowOf(
            ResponseState.Failure(Throwable("Remove failed"))
        )

        viewModel.removeProductFromCart("line123")
        advanceUntilIdle()

        // Verify no crash occurred
        coVerify(exactly = 0) { cartRepository.getCartProductList(any()) }
    }

    // Test cases for clearCart()
    @Test
    fun `clearCart when successful removes items and refreshes cart`() = testScope.runTest {
        val cart = mockk<GetCartDetailsQuery.Cart>()
        val edge = mockk<GetCartDetailsQuery.Edge>()
        val node = mockk<GetCartDetailsQuery.Node>()

        every { node.id } returns "lineId1"
        every { edge.node } returns node
        every { cart.lines.edges } returns listOf(edge)

        coEvery { cartRepository.getCartProducts("cart123") } returns flowOf(
            ResponseState.Success<GetCartDetailsQuery.Cart>(cart)
        )
        coEvery { cartRepository.removeProductFromCart("cart123", "lineId1") } returns flowOf(
            ResponseState.Success<Unit>(Unit)
        )
        coEvery { cartRepository.getCartProductList("cart123") } returns flowOf(
            ResponseState.Success<List<CartItem>>(emptyList())
        )

        viewModel.clearCart()
        advanceUntilIdle()

        assert(viewModel.cartProducts.value is ResponseState.Success<*>)
    }

    @Test
    fun `clearCart handles getCartProducts failure`() = testScope.runTest {
        coEvery { cartRepository.getCartProducts("cart123") } returns flowOf(
            ResponseState.Failure(Throwable("Load failed"))
        )

        viewModel.clearCart()
        advanceUntilIdle()

        // Verify no removal attempts were made
        coVerify(exactly = 0) { cartRepository.removeProductFromCart(any(), any()) }
    }

    @Test
    fun `clearCart handles removeProduct failure`() = testScope.runTest {
        val cart = mockk<GetCartDetailsQuery.Cart>()
        val edge = mockk<GetCartDetailsQuery.Edge>()
        val node = mockk<GetCartDetailsQuery.Node>()

        every { node.id } returns "lineId1"
        every { edge.node } returns node
        every { cart.lines.edges } returns listOf(edge)

        coEvery { cartRepository.getCartProducts("cart123") } returns flowOf(
            ResponseState.Success<GetCartDetailsQuery.Cart>(cart)
        )
        coEvery { cartRepository.removeProductFromCart("cart123", "lineId1") } returns flowOf(
            ResponseState.Failure(Throwable("Remove failed"))
        )

        viewModel.clearCart()
        advanceUntilIdle()

        coVerify { cartRepository.getCartProductList("cart123") }
    }

//    // Test cases for loadDefaultAddress()
@Test
fun `loadDefaultAddress sets correct address`() = testScope.runTest {
    val address = Address(
        id = "gid://shopify/MailingAddress/123?random=param",
        firstName = "Test",
        lastName = "User",
        address1 = "123 Main St",
        city = "Testville",
        country = "Test Country",
        address2 = "",
        phone = "1234567890"
    )

    every { SharedPreferenceImpl.getFromSharedPreferenceInGeneral("${DEFAULT_ADDRESS_ID}_test_token") } returns "123"

    coEvery { addressRepository.getAddresses("test_token") } returns flowOf(
        ResponseState.Success<List<Address>>(listOf(address))
    )

    mockkStatic("com.example.buyva.utils.extensions.ExtensionsKt")
    every { address.id!!.stripTokenFromShopifyGid() } returns "123"

    viewModel.loadDefaultAddress()
    advanceUntilIdle()

    assertEquals(address, viewModel.defaultAddress.value)
}


    @Test
    fun `loadDefaultAddress does nothing when token is blank`() = testScope.runTest {
        // Override the token mock BEFORE creating the view model
        every { SharedPreferenceImpl.getFromSharedPreferenceInGeneral(USER_TOKEN) } returns null

        // Recreate the view model after the override
        viewModel = CartViewModel(application, cartRepository, addressRepository)

        viewModel.loadDefaultAddress()
        advanceUntilIdle()

        assertNull(viewModel.defaultAddress.value)
        coVerify(exactly = 0) { addressRepository.getAddresses(any()) }
    }


    @Test
    fun `loadDefaultAddress handles repository error`() = testScope.runTest {
        coEvery { addressRepository.getAddresses("test_token") } returns flowOf(
            ResponseState.Failure(Throwable("Network error"))
        )

        viewModel.loadDefaultAddress()
        advanceUntilIdle()

        assertNull(viewModel.defaultAddress.value)
    }

    // Test cases for updateCartLine()
    @Test
    fun `updateCartLine when successful refreshes cart`() = testScope.runTest {
        coEvery { cartRepository.updateCartLine("cart123", any(), any()) } returns flowOf(
            ResponseState.Success<Unit>(Unit)
        )
        coEvery { cartRepository.getCartProductList("cart123") } returns flowOf(
            ResponseState.Success<List<CartItem>>(emptyList())
        )

        viewModel.updateCartLine("line1", 2)
        advanceUntilIdle()

        assert(viewModel.cartProducts.value is ResponseState.Success<*>)
    }

    @Test
    fun `updateCartLine when failure does not crash`() = testScope.runTest {
        coEvery { cartRepository.updateCartLine("cart123", any(), any()) } returns flowOf(
            ResponseState.Failure(Throwable("error"))
        )

        // Should not crash
        viewModel.updateCartLine("line1", 2)
        advanceUntilIdle()
    }

    @Test
    fun `updateCartLine does nothing when cartId is null`() = testScope.runTest {
        every { SharedPreferenceImpl.getFromSharedPreferenceInGeneral(any()) } returns null

        viewModel.updateCartLine("line1", 2)
        advanceUntilIdle()

        coVerify(exactly = 0) { cartRepository.updateCartLine(any(), any(), any()) }
    }
}


