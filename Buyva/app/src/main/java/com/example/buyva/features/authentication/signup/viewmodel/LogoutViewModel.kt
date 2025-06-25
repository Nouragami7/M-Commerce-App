package com.example.buyva.features.authentication.signup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.buyva.data.repository.Authentication.AuthRepository
import com.example.buyva.data.repository.Authentication.IAuthRepository

class LogoutViewModel(
    private val authRepository: IAuthRepository
) : ViewModel() {

    fun logout() {
        authRepository.logout()
    }
}
class LogoutViewModelFactory(
    private val authRepository: IAuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LogoutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LogoutViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

