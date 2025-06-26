package com.example.buyva.repository.home

import com.example.buyva.BrandsAndProductsQuery
import com.example.buyva.data.datasource.remote.RemoteDataSource
import com.example.buyva.data.repository.home.HomeRepositoryImpl
import com.example.buyva.data.repository.home.IHomeRepository
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class HomeRepositoryTest {


    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var homeRepository: IHomeRepository

    @Before
    fun setUp(){
        remoteDataSource = mockk()
        homeRepository = HomeRepositoryImpl(remoteDataSource)
    }


    @Test
    fun getBrandsAndProduct_callsRemoteDataSource_returnBrandsAndProductsData() = runTest{
      val expectedData = mockk<BrandsAndProductsQuery.Data>()

        every {
            remoteDataSource.getBrandsAndProduct()

        } returns flowOf(expectedData)


        val result = homeRepository.getBrandsAndProduct()

        result.collect{ data ->
            assertEquals(expectedData, data)
        }



    }



}