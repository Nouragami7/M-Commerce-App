package com.example.buyva.data.repository.adresses

import com.example.buyva.data.model.Address
import com.example.buyva.data.model.uistate.ResponseState
import kotlinx.coroutines.flow.Flow

interface IAddressRepo {
    suspend fun createAddress(
        address :Address,
        token: String
    ): Flow<ResponseState>
    suspend fun getAddresses(
        token: String
    ): Flow<ResponseState>
     suspend fun deleteCustomerAddress(addressId: String, token: String): Flow<ResponseState>
     suspend fun updateAddress(address: Address, token: String): Flow<ResponseState>
}
