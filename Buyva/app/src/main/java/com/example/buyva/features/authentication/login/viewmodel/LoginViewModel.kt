package com.example.buyva.features.authentication.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buyva.data.repository.Authentication.IAuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: IAuthRepository
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

                // Check if email is verified
                if (!user.isEmailVerified) {
                    _errorMessage.value =
                        "Please verify your email first. Check your inbox or spam folder."
                    authRepository.logout()
                    return@launch
                }
                UserSessionManager.setGuestMode(false)

                _loginState.value = user
                _errorMessage.value = null
            } catch (e: Exception) {
                handleLoginError(e)
            }
        }
    }

    private fun handleLoginError(e: Exception) {
        _errorMessage.value = when (e) {
            is FirebaseAuthInvalidCredentialsException -> "Incorrect password or email"
            is FirebaseAuthInvalidUserException -> "User not found. Please sign up"
            is FirebaseAuthEmailException -> "Invalid email format"
            is FirebaseAuthException -> "Authentication failed: ${e.message}"
            else -> e.message ?: "Login failed"
        }
    }

    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            try {
                val user = authRepository.signInWithGoogle(account)
                if (user != null) {
                    UserSessionManager.setGuestMode(false)

                    _loginState.value = user
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Google Sign-In failed: No user returned"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Google Sign-In failed: ${e.message}"
            }
        }
    }
    fun forgotPassword(email: String) {
        if (email.isBlank()) {
            _errorMessage.value = "Please enter your email"
            return
        }

        viewModelScope.launch {
            val result = authRepository.sendPasswordResetEmail(email)
            result.onSuccess {
                _errorMessage.value = "Password reset email sent. Please check your inbox."
            }.onFailure {
                _errorMessage.value = "Failed to send reset email: ${it.message}"
            }
        }
    }

}