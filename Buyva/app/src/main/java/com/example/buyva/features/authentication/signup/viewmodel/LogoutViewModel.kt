package com.example.buyva.features.authentication.signup.viewmodel

import androidx.lifecycle.ViewModel
import com.example.buyva.data.repository.Authentication.IAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(
    private val authRepository: IAuthRepository
) : ViewModel() {

    fun logout() {
        authRepository.logout()
    }
}

