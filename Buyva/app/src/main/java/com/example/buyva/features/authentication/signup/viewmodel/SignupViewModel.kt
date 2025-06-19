package com.example.buyva.features.authentication.signup.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.buyva.data.model.CustomerData
import com.example.buyva.data.repository.AuthRepository
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SignupViewModel(private val repository: AuthRepository,private val applicationContext: Context
) : ViewModel() {

    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user: StateFlow<FirebaseUser?> = _user

    private val _customerData = MutableStateFlow<CustomerData?>(null)
    val customerData: StateFlow<CustomerData?> = _customerData

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
                val result = repository.signUpAndCreateShopifyCustomer(fullName, email, password)
                if (result.isSuccess) {
                    val (firebaseUser, customer) = result.getOrNull()!!
                    SharedPreferenceImpl.saveCustomer(
                        context = applicationContext,
                        id = customer.id,
                        email = customer.email,
                        firstName = customer.firstName,
                        lastName = customer.lastName
                    )
                    _customerData.value = customer

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
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Sign up failed"
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
                    // 1. Create Shopify customer
                    val result = repository.createShopifyCustomerAfterGoogleSignIn(firebaseUser)

                    if (result.isSuccess) {
                        val customer = result.getOrNull()!!

                        // 2. Save to SharedPreferences
                        SharedPreferenceImpl.saveCustomer(
                            context = applicationContext,
                            id = customer.id,
                            email = customer.email,
                            firstName = customer.firstName,
                            lastName = customer.lastName
                        )

                        _customerData.value = customer
                        _user.value = firebaseUser
                      //  onSuccess() // Navigate to home
                    } else {
                        _error.value = "Failed to create Shopify account"
                    }
                }
            } catch (e: Exception) {
                _error.value = "Google sign-in failed: ${e.message}"
            }
        }
    }


    companion object {
        fun isUserLoggedIn(context: Context): Boolean {
            return SharedPreferenceImpl.getCustomer(context) != null
        }

        fun logout(context: Context) {
            SharedPreferenceImpl.deleteCustomer(context)
            // Clear Firebase auth if needed
            // FirebaseAuth.getInstance().signOut()
        }
    }
}


class SignupViewModelFactory(
    private val repository: AuthRepository,
    private val applicationContext: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignupViewModel(repository, applicationContext) as T
    }
}