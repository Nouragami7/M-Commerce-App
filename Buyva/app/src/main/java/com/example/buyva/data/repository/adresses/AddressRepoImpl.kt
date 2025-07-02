package com.example.buyva.data.repository.adresses

import com.example.buyva.data.datasource.remote.RemoteDataSource
import com.example.buyva.data.model.Address
import com.example.buyva.data.model.uistate.ResponseState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddressRepoImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : IAddressRepo {
    override suspend fun createAddress(address: Address, token: String): Flow<ResponseState> {
        return remoteDataSource.createCustomerAddress(token, address)
    }

    override suspend fun getAddresses(token: String): Flow<ResponseState> {
        return remoteDataSource.getCustomerAddresses(token)
    }

    override suspend fun deleteCustomerAddress(addressId: String, token: String) = remoteDataSource.deleteCustomerAddress(addressId, token)
    override suspend fun updateAddress(address: Address, token: String): Flow<ResponseState>{
        return remoteDataSource.updateAddress(address, token)
    }
}