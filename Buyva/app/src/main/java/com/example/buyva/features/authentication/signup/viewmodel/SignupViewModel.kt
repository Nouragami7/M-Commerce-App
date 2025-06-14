package com.example.buyva.features.authentication.signup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.buyva.features.authentication.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignupViewModel(
    private val repository: AuthRepository
) : ViewModel() {

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

        if (!password.any { it.isUpperCase() }) {
            _error.value = "Password must contain at least one uppercase letter"
            return
        }

        if (!password.any { it.isDigit() }) {
            _error.value = "Password must contain at least one number"
            return
        }

        if (password != confirmPassword) {
            _error.value = "Passwords do not match"
            return
        }

        viewModelScope.launch {
            try {
                val result = repository.signUpWithEmail(email, password)

                result?.sendEmailVerification()?.addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        _error.value = "Failed to send verification email"
                    }
                }

                _user.value = result
                _error.value = null
            } catch (e: Exception) {
                _error.value = when (e) {
                    is FirebaseAuthUserCollisionException -> "Email already in use"
                    is FirebaseAuthWeakPasswordException -> "Weak password: ${e.reason}"
                    is FirebaseAuthException -> "Signup failed: ${e.message}"
                    else -> e.message ?: "Signup failed"
                }
            }
        }
    }

    fun signUpWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            try {
                val result = repository.signInWithGoogle(account)
                _user.value = result
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Google Sign-up failed"
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
