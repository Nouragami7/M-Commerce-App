package com.example.buyva.viewmodel.Authentication

import com.example.buyva.data.repository.Authentication.IAuthRepository
import com.example.buyva.features.authentication.signup.viewmodel.LogoutViewModel
import io.mockk.mockk
import io.mockk.verify
import io.mockk.just
import io.mockk.runs
import org.junit.Before
import org.junit.Test

class LogoutViewModelTest {

    private lateinit var authRepository: IAuthRepository
    private lateinit var viewModel: LogoutViewModel

    @Before
    fun setUp() {
        authRepository = mockk()
        viewModel = LogoutViewModel(authRepository)
    }

    @Test
    fun `logout calls authRepository logout`() {

        io.mockk.every { authRepository.logout() } just runs

        viewModel.logout()

        verify(exactly = 1) { authRepository.logout() }
    }
}
