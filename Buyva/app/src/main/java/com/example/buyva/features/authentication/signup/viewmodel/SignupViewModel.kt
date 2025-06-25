package com.example.buyva.features.authentication.signup.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buyva.data.model.CustomerData
import com.example.buyva.data.repository.Authentication.IAuthRepository
import com.example.buyva.features.authentication.login.viewmodel.UserSessionManager
import com.example.buyva.utils.constants.USER_TOKEN
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val repository: IAuthRepository,
    private val applicationContext: Context
) : ViewModel() {

    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user: StateFlow<FirebaseUser?> = _user

    private val _customerData = MutableStateFlow<CustomerData?>(null)
    val customerData: StateFlow<CustomerData?> = _customerData

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isEmailVerified = MutableStateFlow(false)
    val isEmailVerified: StateFlow<Boolean> = _isEmailVerified

    private val _verificationSent = MutableStateFlow(false)
    val verificationSent: StateFlow<Boolean> = _verificationSent

    private var verificationStartTime = 0L
    private var verificationCheckJob: Job? = null
    private var currentPassword: String = ""

    private suspend fun FirebaseUser.reloadSuspend() = withContext(Dispatchers.IO) {
        this@reloadSuspend.reload().await()
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
        _verificationSent.value = false
        _isEmailVerified.value = false
        currentPassword = password

        viewModelScope.launch {
            try {
                val firebaseUserResult = repository.signUpWithEmail(email, password, fullName)

                if (firebaseUserResult.isSuccess) {
                    val firebaseUser = firebaseUserResult.getOrThrow()
                    sendVerificationEmail(firebaseUser)
                } else {
                    handleSignUpError(firebaseUserResult, email, password, fullName)
                }
            } catch (e: Exception) {
                _error.value = "Sign up failed: ${e.message}"
            }
        }
    }

    private suspend fun sendVerificationEmail(user: FirebaseUser) {
        try {
            user.sendEmailVerification().await()
            _verificationSent.value = true
            _error.value = "Verification email sent. Please check your inbox."
            verificationStartTime = System.currentTimeMillis()
            startVerificationCheck(user)
        } catch (e: Exception) {
            _error.value = "Failed to send verification email: ${e.message}"
        }
    }

    private suspend fun handleSignUpError(
        result: Result<FirebaseUser>,
        email: String,
        password: String,
        fullName: String
    ) {
        val exception = result.exceptionOrNull()
        when (exception) {
            is FirebaseAuthUserCollisionException -> {
                try {
                    val existingUser = repository.signInWithEmail(email, password)
                    if (!existingUser.isEmailVerified) {
                        sendVerificationEmail(existingUser)
                    } else {
                        _error.value = "This email is already registered and verified. Please sign in."
                    }
                } catch (e: FirebaseAuthException) {
                    when (e) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            _error.value = "The email is already registered, but the password is incorrect."
                        }
                        else -> {
                            _error.value = "Account conflict: ${e.message}"
                        }
                    }
                }
            }
            else -> {
                _error.value = exception?.message ?: "Firebase sign up failed"
            }
        }
    }

    private fun startVerificationCheck(user: FirebaseUser) {
        verificationCheckJob?.cancel()
        verificationCheckJob = viewModelScope.launch {
            Log.d("DebugCheck", "startVerificationCheck started")

            while (true) {
                delay(TimeUnit.SECONDS.toMillis(30))

                if (System.currentTimeMillis() - verificationStartTime > TimeUnit.HOURS.toMillis(24)) {
                    _error.value = "Verification link expired. Please sign up again."
                    try {
                        repository.deleteCurrentUser()
                    } catch (e: Exception) {
                        Log.e("DebugCheck", "Failed to delete user", e)
                    }
                    stopVerificationCheck()
                    return@launch
                }

                try {
                    user.reloadSuspend()
                    Log.d("DebugCheck", " Reloaded user, isEmailVerified = ${user.isEmailVerified}")

                    if (user.isEmailVerified) {
                        _isEmailVerified.value = true
                        createShopifyAccount(user)
                        stopVerificationCheck()
                        return@launch
                    }
                } catch (e: Exception) {
                    _error.value = "Failed to check verification: ${e.message}"
                }
            }
        }
    }

    private fun stopVerificationCheck() {
        verificationCheckJob?.cancel()
        verificationCheckJob = null
    }

    private suspend fun createShopifyAccount(user: FirebaseUser) {
        val fullName = user.displayName ?: ""
        val email = user.email ?: ""

        Log.d("DebugCheck", " createShopifyAccount started for $email")
        Log.d("DebugCheck", " createShopifyCustomer called for $email")

        try {
            val shopifyResult = repository.createShopifyCustomer(fullName, email, currentPassword)

            if (shopifyResult.isSuccess) {
                val customer = shopifyResult.getOrThrow()
                Log.d("DebugCheck", " Shopify Customer created: ${customer.id}")

                SharedPreferenceImpl.saveCustomer(
                    context = applicationContext,
                    id = customer.id,
                    email = customer.email,
                    firstName = customer.firstName,
                    lastName = customer.lastName
                )
                UserSessionManager.setGuestMode(false)

                _customerData.value = customer

                Log.d("DebugCheck", " Trying to get Shopify access token...")
                val tokenResult = repository.getShopifyAccessToken(email, currentPassword)

                if (tokenResult.isSuccess) {
                    val token = tokenResult.getOrThrow()
                    Log.d("DebugCheck", "Access Token received: $token")
                    SharedPreferenceImpl.saveToSharedPreferenceInGeneral(USER_TOKEN, token)
                } else {
                    val tokenError = tokenResult.exceptionOrNull()?.message ?: "Unknown token error"
                    Log.e("DebugCheck", " Failed to get access token: $tokenError")
                }

                _user.value = user
                _error.value = null
            } else {
                val error = shopifyResult.exceptionOrNull()?.message ?: "Unknown Shopify error"
                _error.value = error
            }
        } catch (e: Exception) {
            _error.value = "Account setup failed: ${e.message}"
        } finally {
            currentPassword = ""
        }
    }

    fun resendVerificationEmail() {
        viewModelScope.launch {
            try {
                val user = repository.reloadFirebaseUser()
                user?.let {
                    it.sendEmailVerification().await()
                    verificationStartTime = System.currentTimeMillis()
                    _verificationSent.value = true
                    _error.value = "Verification email resent. Please check your inbox."
                } ?: run {
                    _error.value = "No user found. Please sign up again."
                }
            } catch (e: Exception) {
                _error.value = "Failed to resend email: ${e.message}"
            }
        }
    }

    fun signUpWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            try {
                val firebaseUser = repository.signInWithGoogle(account)
                if (firebaseUser != null) {
                    _isEmailVerified.value = true

                    val result = repository.createShopifyCustomerAfterGoogleSignIn(firebaseUser)
                    if (result.isSuccess) {
                        val customer = result.getOrNull()!!

                        SharedPreferenceImpl.saveCustomer(
                            context = applicationContext,
                            id = customer.id,
                            email = customer.email,
                            firstName = customer.firstName,
                            lastName = customer.lastName
                        )
                        UserSessionManager.setGuestMode(false)

                        _customerData.value = customer
                        _user.value = firebaseUser
                    } else {
                        _error.value = "Failed to create Shopify account"
                    }
                } else {
                    _error.value = "Google sign-in failed: No user returned"
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
        }
    }
}
