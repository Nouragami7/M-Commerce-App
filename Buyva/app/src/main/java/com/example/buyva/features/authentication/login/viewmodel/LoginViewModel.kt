package com.example.buyva.features.authentication.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buyva.features.authentication.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<FirebaseUser?>(null)
    val loginState: StateFlow<FirebaseUser?> = _loginState

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            val user = authRepository.signInWithEmail(email, password)
            _loginState.value = user
        }
    }
}
