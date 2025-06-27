package com.example.buyva.repository.addresses

import com.example.buyva.data.datasource.remote.RemoteDataSource
import com.example.buyva.data.model.Address
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.adresses.AddressRepoImpl
import com.example.buyva.data.repository.adresses.IAddressRepo
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddressRepoImplTest {

    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var addressRepo: AddressRepoImpl

    @Before
    fun setUp() {
        remoteDataSource = mockk()
        addressRepo = AddressRepoImpl(remoteDataSource)
    }

    @Test
    fun createAddress_returnsSuccess() = runTest {
        val address = Address(
            firstName = "alia",
            lastName = "hussiem",
            address1 = "123 Main St",
            city = "City",
            country = "Country",
            address2 = "addres2",
            phone = "1234567890",

            )
        val token = "token123"
        val expected = ResponseState.Success("Created")

        coEvery { remoteDataSource.createCustomerAddress(token, address) } returns flowOf(expected)

        val result = addressRepo.createAddress(address, token).toList()

        assertEquals(listOf(expected), result)
    }

    @Test
    fun createAddress_returnsFailure() = runTest {
        val address = Address(
            firstName = "alia",
            lastName = "hussiem",
            address1 = "123 Main St",
            city = "City",
            country = "Country",
            address2 = "addres2",
            phone = "1234567890",

            )
        val token = "token123"
        val expected = ResponseState.Failure(Throwable("Creation failed"))

        coEvery { remoteDataSource.createCustomerAddress(token, address) } returns flowOf(expected)

        val result = addressRepo.createAddress(address, token).toList()

        assertEquals(listOf(expected), result)
    }

    @Test
    fun getAddresses_returnsSuccess() = runTest {
        val token = "token123"
        val expected = ResponseState.Success("Address List")

        coEvery { remoteDataSource.getCustomerAddresses(token) } returns flowOf(expected)

        val result = addressRepo.getAddresses(token).toList()

        assertEquals(listOf(expected), result)
    }

    @Test
    fun getAddresses_returnsFailure() = runTest {
        val token = "token123"
        val expected = ResponseState.Failure(Throwable("Fetch error"))

        coEvery { remoteDataSource.getCustomerAddresses(token) } returns flowOf(expected)

        val result = addressRepo.getAddresses(token).toList()

        assertEquals(listOf(expected), result)
    }

    @Test
    fun delete_customer_address_calls_remote_successfully() = runTest {
        val token = "token123"
        val addressId = "address123"

        coEvery { remoteDataSource.deleteCustomerAddress(addressId, token) } returns flowOf(ResponseState.Success(Unit))

        addressRepo.deleteCustomerAddress(addressId, token)

        coVerify { remoteDataSource.deleteCustomerAddress(addressId, token) }
    }


    @Test
    fun updateAddress_returnsSuccess() = runTest {
        val token = "token123"
        val address = Address(
            firstName = "alia",
            lastName = "hussiem",
            address1 = "123 Main St",
            city = "City",
            country = "Country",
            address2 = "addres2",
            phone = "1234567890",

            )
        val expected = ResponseState.Success("Updated")

        coEvery { remoteDataSource.updateAddress(address, token) } returns flowOf(expected)

        val result = addressRepo.updateAddress(address, token).toList()

        assertEquals(listOf(expected), result)
    }

    @Test
    fun updateAddress_returnsFailure() = runTest {
        val token = "token123"
        val address = Address(
            firstName = "alia",
            lastName = "hussiem",
            address1 = "123 Main St",
            city = "City",
            country = "Country",
            address2 = "addres2",
            phone = "1234567890",

        )
        val expected = ResponseState.Failure(Throwable("Update failed"))

        coEvery { remoteDataSource.updateAddress(address, token) } returns flowOf(expected)

        val result = addressRepo.updateAddress(address, token).toList()

        assertEquals(listOf(expected), result)
    }
}
