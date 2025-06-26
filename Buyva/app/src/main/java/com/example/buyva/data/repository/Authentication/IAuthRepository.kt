package com.example.buyva.data.repository.Authentication

import com.example.buyva.data.model.CustomerData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser

interface IAuthRepository {

    suspend fun isEmailVerified(): Boolean

    suspend fun reloadFirebaseUser(): FirebaseUser?

    suspend fun signUpWithEmail(
        email: String,
        password: String,
        fullName: String
    ): Result<FirebaseUser>

    suspend fun sendVerificationEmail(user: FirebaseUser): Result<Unit>

    suspend fun signInWithEmail(email: String, password: String): FirebaseUser

    suspend fun getShopifyAccessToken(email: String, password: String): Result<String>

    suspend fun signInWithGoogle(account: GoogleSignInAccount): FirebaseUser?

    suspend fun createShopifyCustomer(
        fullName: String,
        email: String,
        password: String
    ): Result<CustomerData>

    suspend fun createShopifyCustomerAfterGoogleSignIn(user: FirebaseUser): Result<CustomerData>

    fun logout()

    suspend fun deleteCurrentUser()
}
