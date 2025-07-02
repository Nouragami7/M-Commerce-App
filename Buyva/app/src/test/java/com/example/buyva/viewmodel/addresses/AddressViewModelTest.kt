package com.example.buyva.viewmodel.addresses

import android.app.Application
import com.example.buyva.data.model.Address
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.adresses.IAddressRepo
import com.example.buyva.features.profile.addressdetails.viewmodel.AddressViewModel
import com.example.buyva.utils.constants.DEFAULT_ADDRESS_ID
import com.example.buyva.utils.constants.USER_TOKEN
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl

import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class AddressViewModelTest {

    private lateinit var viewModel: AddressViewModel
    private lateinit var repo: IAddressRepo
    private lateinit var app: Application
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repo = mockk(relaxed = true)
        app = mockk(relaxed = true)

        mockkObject(SharedPreferenceImpl)
        every { SharedPreferenceImpl.getFromSharedPreferenceInGeneral(USER_TOKEN) } returns "test_token"

        viewModel = AddressViewModel(app, repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `loadAddresses sets Failure when token is null`() = runTest {
        unmockkAll()
        mockkObject(SharedPreferenceImpl)
        every { SharedPreferenceImpl.getFromSharedPreferenceInGeneral(USER_TOKEN) } returns null

        val localViewModel = AddressViewModel(app, repo)

        localViewModel.loadAddresses()
        advanceUntilIdle()

        val state = localViewModel.addresses.value
        assertTrue(state is ResponseState.Failure)
        assertEquals("User token is missing", (state as ResponseState.Failure).message.message)
    }

    @Test
    fun `loadAddresses sets Success when addresses fetched`() = runTest {
        val address = Address(
            id = "gid://shopify/MailingAddress/123",
            firstName = "John",
            lastName = "Doe",
            address1 = "123 Main St",
            address2 = "Apt 4B",
            city = "Cityville",
            country = "Countryland",
            phone = "1234567890"
        )

        coEvery {
            repo.getAddresses("test_token")
        } returns flowOf(ResponseState.Success(listOf(address)))

        every {
            SharedPreferenceImpl.getFromSharedPreferenceInGeneral("${DEFAULT_ADDRESS_ID}_test_token")
        } returns null

        every {
            SharedPreferenceImpl.saveToSharedPreferenceInGeneral(any(), any())
        } just Runs

        viewModel.loadAddresses()
        advanceUntilIdle()

        assertTrue(viewModel.addresses.value is ResponseState.Success<*>)
    }

    @Test
    fun `saveAddress calls createAddress for new address`() = runTest {
        val address = Address(
            id = null,
            firstName = "John",
            lastName = "Doe",
            address1 = "123 Main St",
            address2 = "Apt 4B",
            city = "Cityville",
            country = "Countryland",
            phone = "1234567890"
        )

        coEvery {
            repo.createAddress(address, "test_token")
        } returns flowOf(ResponseState.Success<Unit>(Unit))

        coEvery {
            repo.getAddresses("test_token")
        } returns flowOf(ResponseState.Success<List<Address>>(emptyList()))

        viewModel.saveAddress(address)
        advanceUntilIdle()

        assertTrue(viewModel.saveAddressState.value is ResponseState.Success<*>)
    }

    @Test
    fun `saveAddress calls updateAddress for existing address`() = runTest {
        val address = Address(
            id = "gid://shopify/MailingAddress/123",
            firstName = "John",
            lastName = "Doe",
            address1 = "123 Main St",
            address2 = "Apt 4B",
            city = "Cityville",
            country = "Countryland",
            phone = "1234567890"
        )

        coEvery {
            repo.updateAddress(address, "test_token")
        } returns flowOf(ResponseState.Success<Unit>(Unit))

        coEvery {
            repo.getAddresses("test_token")
        } returns flowOf(ResponseState.Success<List<Address>>(emptyList()))

        viewModel.saveAddress(address)
        advanceUntilIdle()

        assertTrue(viewModel.saveAddressState.value is ResponseState.Success<*>)
    }

    @Test
    fun `resetSaveAddressState resets to Loading`() {
        viewModel.resetSaveAddressState()
        assertTrue(viewModel.saveAddressState.value is ResponseState.Loading)
    }

    @Test
    fun `deleteAddress triggers loadAddresses on success`() = runTest {
        coEvery {
            repo.deleteCustomerAddress("address123", "test_token")
        } returns flowOf(ResponseState.Success<Unit>(Unit))

        coEvery {
            repo.getAddresses("test_token")
        } returns flowOf(ResponseState.Success<List<Address>>(emptyList()))

        viewModel.deleteAddress("address123")
        advanceUntilIdle()

        assertTrue(viewModel.addresses.value is ResponseState.Success<*>)
    }
}

