package com.example.buyva.features.authentication.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.buyva.features.authentication.repository.AuthRepository
import com.google.firebase.auth.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<FirebaseUser?>(null)
    val loginState: StateFlow<FirebaseUser?> = _loginState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun signIn(email: String, password: String) {
        if (email.isBlank()) {
            _errorMessage.value = "Email is required"
            return
        }

        if (password.isBlank()) {
            _errorMessage.value = "Password is required"
            return
        }

        viewModelScope.launch {
            try {
                val user = authRepository.signInWithEmail(email, password)
                _loginState.value = user
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        "Incorrect password or email"
                    }
                    is FirebaseAuthInvalidUserException -> {
                        "User not found"
                    }
                    is FirebaseAuthEmailException -> {
                        "Invalid email format"
                    }
                    is FirebaseAuthException -> {
                        // General Firebase Auth error
                        "Authentication failed: ${e.message}"
                    }
                    else -> {
                        e.message ?: "Login failed"
                    }
                }
            }
        }
    }
}

class LoginViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
