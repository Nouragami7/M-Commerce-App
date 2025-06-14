package com.example.buyva.features.authentication.signup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.example.buyva.features.authentication.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignupViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user: StateFlow<FirebaseUser?> = _user

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun signUp(fullName: String, email: String, password: String, confirmPassword: String) {
        if (fullName.isBlank()) {
            _error.value = "Full name is required"
            return
        }

        if (email.isBlank()) {
            _error.value = "Email is required"
            return
        }

        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
        if (!emailRegex.matches(email)) {
            _error.value = "Invalid email format"
            return
        }

        if (password.length < 6) {
            _error.value = "Password must be at least 6 characters"
            return
        }

        val hasUpperCase = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }

        if (!hasUpperCase) {
            _error.value = "Password must contain at least one uppercase letter"
            return
        }

        if (!hasDigit) {
            _error.value = "Password must contain at least one number"
            return
        }

        if (password != confirmPassword) {
            _error.value = "Passwords do not match"
            return
        }

        viewModelScope.launch {
            val result = repository.signUpWithEmail(email, password)
            if (result != null) {
                _user.value = result
                _error.value = null
            } else {
                _error.value = "Sign up failed"
            }
        }
    }
}

class SignupViewModelFactory(
    private val repository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignupViewModel(repository) as T
    }
}
