package com.example.buyva.data.repository

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.example.buyva.CreateCustomerMutation
import com.example.buyva.type.CustomerCreateInput
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import com.apollographql.apollo3.api.Optional

class AuthRepository(
    private val auth: FirebaseAuth,
    private val apolloClient: ApolloClient
) {

    suspend fun signUpWithEmail(email: String, password: String): FirebaseUser? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            null
        }
    }

    suspend fun signInWithEmail(email: String, password: String): FirebaseUser {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user ?: throw Exception("User is null")
    }

    suspend fun signInWithGoogle(account: GoogleSignInAccount): FirebaseUser? {
        return try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val result = auth.signInWithCredential(credential).await()
            result.user
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createShopifyCustomer(fullName: String, email: String, password: String): Result<String> {
        val names = fullName.trim().split(" ")
        val firstName = names.firstOrNull() ?: ""
        val lastName = names.drop(1).joinToString(" ")

        return try {
            val input = CustomerCreateInput(
                firstName = Optional.Present(firstName),
                lastName = Optional.Present(lastName),
                email = email,       // Required String, not Optional
                password = password  // Required String, not Optional
            )

            val mutation = CreateCustomerMutation(input)
            val response = apolloClient.mutation(mutation).execute()

            Log.d("Shopify", "Customer: ${response.data?.customerCreate?.customer}")
            Log.d("Shopify", "Error: ${response.data?.customerCreate?.customerUserErrors?.firstOrNull()?.message}")
            val customer = response.data?.customerCreate?.customer
            val error = response.data?.customerCreate?.customerUserErrors?.firstOrNull()?.message

            when {
                customer != null -> Result.success(customer.id)
                error != null -> Result.failure(Exception(error))
                else -> Result.failure(Exception("Unknown Shopify error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUpAndCreateShopifyCustomer(
        fullName: String,
        email: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            val firebaseUser = signUpWithEmail(email, password)
                ?: return Result.failure(Exception("Firebase sign up failed"))

            val shopifyResult = createShopifyCustomer(fullName, email, password)
            if (shopifyResult.isFailure) {
                return Result.failure(shopifyResult.exceptionOrNull()!!)
            }

            Result.success(firebaseUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
