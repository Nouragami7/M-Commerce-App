package com.example.buyva.features.authentication.signup.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.buyva.features.authentication.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SignupViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user: StateFlow<FirebaseUser?> = _user

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private suspend fun FirebaseUser.reloadSuspend() = suspendCoroutine<Unit> { cont ->
        this.reload().addOnCompleteListener { task ->
            if (task.isSuccessful) cont.resume(Unit)
            else cont.resumeWithException(task.exception ?: Exception("Reload failed"))
        }
    }

    fun signUp(fullName: String, email: String, password: String, confirmPassword: String) {
        if (fullName.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _error.value = "Please fill all fields"
            return
        }
        if (password != confirmPassword) {
            _error.value = "Passwords do not match"
            return
        }

        _error.value = null

        viewModelScope.launch {
            try {
                val firebaseUser = repository.signUpWithEmail(email, password)
                if (firebaseUser == null) {
                    _error.value = "Sign up failed"
                    return@launch
                }
                firebaseUser.sendEmailVerification().addOnCompleteListener { verifyTask ->
                    if (verifyTask.isSuccessful) {
                        viewModelScope.launch {
                            try {
                                firebaseUser.reloadSuspend()
                                if (firebaseUser.isEmailVerified) {
                                    _user.value = firebaseUser
                                    _error.value = null
                                } else {
                                    _error.value = "Please verify your email. A verification link was sent."
                                    _user.value = null
                                }
                            } catch (e: Exception) {
                                _error.value = "Failed to reload user data."
                            }
                        }
                    } else {
                        _error.value = "Failed to send verification email."
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Sign up failed"
            }
        }
    }

    fun signUpWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            try {
                val firebaseUser = repository.signInWithGoogle(account)
                if (firebaseUser != null) {
                    _user.value = firebaseUser
                    _error.value = null
                } else {
                    _error.value = "Google sign-in failed"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Google sign-in failed"
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
