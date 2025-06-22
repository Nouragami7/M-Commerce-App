package com.example.buyva.data.repository
//////
import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.example.buyva.CreateCustomerAccessTokenMutation
import com.example.buyva.CreateCustomerMutation
import com.example.buyva.data.model.CustomerData
import com.example.buyva.type.CustomerCreateInput
import com.example.buyva.utils.constants.USER_TOKEN
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth,
    private val apolloClient: ApolloClient
) {

    suspend fun isEmailVerified(): Boolean {
        return auth.currentUser?.isEmailVerified ?: false
    }

    suspend fun reloadFirebaseUser(): FirebaseUser? {
        auth.currentUser?.reload()?.await()
        return auth.currentUser
    }

    suspend fun signUpWithEmail(
        email: String,
        password: String,
        fullName: String
    ): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(fullName)
                    .build()
                user.updateProfile(profileUpdates).await()
            }
            val user = result.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Firebase user is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendVerificationEmail(user: FirebaseUser): Result<Unit> {
        return try {
            val actionCodeSettings = ActionCodeSettings.newBuilder()
                .setUrl("https://yourapp.page.link/verify-email")
                .setHandleCodeInApp(true)
                .setAndroidPackageName("com.example.buyva", true, "23")
                .build()

            user.sendEmailVerification(actionCodeSettings).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithEmail(email: String, password: String): FirebaseUser {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user ?: throw Exception("User is null")
    }

    suspend fun getShopifyAccessToken(email: String, password: String): Result<String> {
        return try {
            val response = apolloClient
                .mutation(CreateCustomerAccessTokenMutation(email = email, password = password))
                .execute()

            val token = response.data?.customerAccessTokenCreate?.customerAccessToken?.accessToken
            val error = response.data?.customerAccessTokenCreate?.customerUserErrors?.firstOrNull()?.message
            Log.i("AuthRepository", "Shopify access token: $token")

            when {
                token != null -> {
                    SharedPreferenceImpl.saveToSharedPreferenceInGeneral(USER_TOKEN, token)
                    Result.success(token)
                }
                error != null -> Result.failure(Exception(error))
                else -> Result.failure(Exception("Unknown error during token creation"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(account: GoogleSignInAccount): FirebaseUser? {
        return try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val result = auth.signInWithCredential(credential).await()
            result.user
        } catch (e: Exception) {
            Log.e("AuthRepository", "Google sign-in failed", e)
            null
        }
    }

    suspend fun createShopifyCustomer(fullName: String, email: String, password: String): Result<CustomerData> {
        val names = fullName.trim().split(" ")
        val firstName = names.firstOrNull() ?: ""
        val lastName = names.drop(1).joinToString(" ")

        return try {
            val input = CustomerCreateInput(
                firstName = Optional.Present(firstName),
                lastName = Optional.Present(lastName),
                email = email,
                password = password
            )

            val mutation = CreateCustomerMutation(input)
            val response = apolloClient.mutation(mutation).execute()

            handleShopifyCustomerResponse(response.data?.customerCreate)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createShopifyCustomerAfterGoogleSignIn(
        user: FirebaseUser
    ): Result<CustomerData> {
        return try {
            if (user.email.isNullOrBlank()) {
                return Result.failure(Exception("Google account email is required"))
            }

            val input = CustomerCreateInput(
                firstName = Optional.Present(user.displayName?.split(" ")?.firstOrNull() ?: ""),
                lastName = Optional.Present(
                    user.displayName?.split(" ")?.drop(1)?.joinToString(" ") ?: ""
                ),
                email = user.email ?: "",
                password = ""
            )

            val response = apolloClient.mutation(CreateCustomerMutation(input)).execute()
            handleShopifyCustomerResponse(response.data?.customerCreate)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun handleShopifyCustomerResponse(
        customerCreate: CreateCustomerMutation.CustomerCreate?
    ): Result<CustomerData> {
        val customer = customerCreate?.customer
        val error = customerCreate?.customerUserErrors?.firstOrNull()?.message

        return when {
            customer != null -> {
                Result.success(
                    CustomerData(
                        id = customer.id,
                        email = customer.email ?: "",
                        firstName = customer.firstName ?: "",
                        lastName = customer.lastName ?: ""
                    )
                )
            }
            error != null -> Result.failure(Exception(error))
            else -> Result.failure(Exception("Unknown Shopify error"))
        }
    }

    fun logout() {
        auth.signOut()
        SharedPreferenceImpl.clearUserData()
    }

    suspend fun deleteCurrentUser() {
        try {
            auth.currentUser?.delete()?.await()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to delete user", e)
        }
    }
}