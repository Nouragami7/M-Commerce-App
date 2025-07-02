package com.example.buyva.viewmodel.map

import com.example.buyva.features.profile.map.viewmodel.MapViewModel
import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

import io.mockk.mockk
import io.mockk.every

import android.location.Address
import org.junit.Assert.assertEquals

class MapViewModelTest {

    private lateinit var viewModel: MapViewModel

    @Before
    fun setup() {
        viewModel = MapViewModel()
    }

    @Test
    fun `setSearchResults should update the internal list`() {
        val address1 = mockk<Address>()
        every { address1.latitude } returns 30.0
        every { address1.longitude } returns 31.0

        val address2 = mockk<Address>()
        every { address2.latitude } returns 29.9
        every { address2.longitude } returns 32.1

        val addresses = listOf(address1, address2)

        viewModel.setSearchResults(addresses)

        assertEquals(2, viewModel.searchResults.size)
        assertEquals(30.0, viewModel.searchResults[0].latitude, 0.01)
        assertEquals(31.0, viewModel.searchResults[0].longitude, 0.01)
    }

    @Test
    fun `selectLocation returns correct LatLng from Address`() {
        val address = mockk<Address>()
        every { address.latitude } returns 30.0
        every { address.longitude } returns 31.0

        val result = viewModel.selectLocation(address)

        assertEquals(30.0, result.latitude, 0.001)
        assertEquals(31.0, result.longitude, 0.001)
    }

}
