package com.example.buyva.repository.Authentication

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.example.buyva.CreateCustomerAccessTokenMutation
import com.example.buyva.CreateCustomerMutation
import com.example.buyva.data.model.CustomerData
import com.example.buyva.data.repository.Authentication.AuthRepository
import com.example.buyva.utils.constants.USER_TOKEN
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.*

@ExperimentalCoroutinesApi
class AuthRepositoryTest {

    private lateinit var auth: FirebaseAuth
    private lateinit var apolloClient: ApolloClient
    private lateinit var authRepository: AuthRepository
    private lateinit var mockUser: FirebaseUser
    private lateinit var mockAuthResult: AuthResult

    @Before

    fun setUp() {
        // Mock dependencies
        auth = mockk(relaxed = true)
        apolloClient = mockk(relaxed = true)
        mockUser = mockk(relaxed = true)
        mockAuthResult = mockk(relaxed = true)
        authRepository = AuthRepository(auth, apolloClient)

        // Mock SharedPreferences
        mockkObject(SharedPreferenceImpl)
        every { SharedPreferenceImpl.saveToSharedPreferenceInGeneral(any(), any()) } just Runs
        every { SharedPreferenceImpl.clearUserData() } just Runs

        // Mock Log
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // Helper functions for Firebase Tasks
    private fun <T> mockSuccessfulTask(result: T? = null): Task<T> {
        val task: Task<T> = mockk(relaxed = true)
        every { task.isComplete } returns true
        every { task.isSuccessful } returns true
        every { task.exception } returns null
        every { task.result } returns result
        return task
    }

    private fun <T> mockFailedTask(exception: Exception): Task<T> {
        val task: Task<T> = mockk(relaxed = true)
        every { task.isComplete } returns true
        every { task.isSuccessful } returns false
        every { task.exception } returns exception
        return task
    }

    // Tests for all AuthRepository methods
    @Test
    fun `isEmailVerified returns true when email is verified`() = runTest {
        every { auth.currentUser } returns mockUser
        every { mockUser.isEmailVerified } returns true
        assertTrue(authRepository.isEmailVerified())
    }

    @Test
    fun `isEmailVerified returns false when user is null`() = runTest {
        every { auth.currentUser } returns null
        assertFalse(authRepository.isEmailVerified())
    }

    @Test
    fun `isEmailVerified returns false when email not verified`() = runTest {
        every { auth.currentUser } returns mockUser
        every { mockUser.isEmailVerified } returns false
        assertFalse(authRepository.isEmailVerified())
    }

    @Test
    fun `reloadFirebaseUser returns user after reload`() = runTest {
        every { auth.currentUser } returns mockUser
        every { mockUser.reload() } returns mockSuccessfulTask()
        assertEquals(mockUser, authRepository.reloadFirebaseUser())
    }

    @Test
    fun `reloadFirebaseUser returns null when user is null`() = runTest {
        every { auth.currentUser } returns null
        assertNull(authRepository.reloadFirebaseUser())
    }

    @Test
    fun `signUpWithEmail success when all operations succeed`() = runTest {
        // Firebase setup
        every { auth.createUserWithEmailAndPassword(any(), any()) } returns
                mockSuccessfulTask(mockAuthResult)
        every { mockAuthResult.user } returns mockUser
        every { mockUser.updateProfile(any()) } returns mockSuccessfulTask()

        // Shopify setup
        val mockCustomer = mockk<CreateCustomerMutation.Customer>(relaxed = true).apply {
            every { id } returns "shopify-id"
            every { email } returns "test@email.com"
            every { firstName } returns "Test"
            every { lastName } returns "User"
        }

        val shopifyResponse = mockk<ApolloResponse<CreateCustomerMutation.Data>>(relaxed = true).apply {
            every { data?.customerCreate?.customer } returns mockCustomer
            every { data?.customerCreate?.customerUserErrors } returns emptyList()
        }

        every { apolloClient.mutation(any<CreateCustomerMutation>()) } returns mockk {
            coEvery { execute() } returns shopifyResponse
        }

        val tokenResponse = mockk<ApolloResponse<CreateCustomerAccessTokenMutation.Data>>(relaxed = true).apply {
            every { data?.customerAccessTokenCreate?.customerAccessToken?.accessToken } returns "token"
            every { data?.customerAccessTokenCreate?.customerUserErrors } returns emptyList()
        }

        every { apolloClient.mutation(any<CreateCustomerAccessTokenMutation>()) } returns mockk {
            coEvery { execute() } returns tokenResponse
        }

        val result = authRepository.signUpWithEmail("test@email.com", "password", "Full Name")
        assertTrue(result.isSuccess)
        assertEquals(mockUser, result.getOrNull())
        verify { SharedPreferenceImpl.saveToSharedPreferenceInGeneral(USER_TOKEN, "token") }
    }

    @Test
    fun `signUpWithEmail fails when Firebase user creation fails`() = runTest {
        val exception = Exception("Firebase error")
        every { auth.createUserWithEmailAndPassword(any(), any()) } returns mockFailedTask(exception)

        val result = authRepository.signUpWithEmail("test@email.com", "password", "Full Name")
        assertTrue(result.isFailure)
        assertEquals("Firebase error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `signUpWithEmail fails when Shopify customer creation fails`() = runTest {
        // Firebase success
        every { auth.createUserWithEmailAndPassword(any(), any()) } returns
                mockSuccessfulTask(mockAuthResult)
        every { mockAuthResult.user } returns mockUser
        every { mockUser.updateProfile(any()) } returns mockSuccessfulTask()

        // Shopify failure
        val shopifyResponse = mockk<ApolloResponse<CreateCustomerMutation.Data>>(relaxed = true).apply {
            every { data?.customerCreate?.customer } returns null
            every { data?.customerCreate?.customerUserErrors } returns listOf(
                mockk { every { message } returns "Shopify error" }
            )
        }

        every { apolloClient.mutation(any<CreateCustomerMutation>()) } returns mockk {
            coEvery { execute() } returns shopifyResponse
        }

        val result = authRepository.signUpWithEmail("test@email.com", "password", "Full Name")
        assertTrue(result.isFailure)
        assertEquals("Shopify customer creation failed", result.exceptionOrNull()?.message)
    }

    @Test
    fun `sendVerificationEmail returns success`() = runTest {
        every { mockUser.sendEmailVerification(any()) } returns mockSuccessfulTask()
        val result = authRepository.sendVerificationEmail(mockUser)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `sendVerificationEmail returns failure on exception`() = runTest {
        val exception = Exception("Email error")
        every { mockUser.sendEmailVerification(any()) } returns mockFailedTask(exception)
        val result = authRepository.sendVerificationEmail(mockUser)
        assertTrue(result.isFailure)
        assertEquals("Email error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `signInWithEmail returns user and saves token`() = runTest {
        // Firebase success
        every { auth.signInWithEmailAndPassword(any(), any()) } returns
                mockSuccessfulTask(mockAuthResult)
        every { mockAuthResult.user } returns mockUser

        // Shopify token success
        val tokenResponse = mockk<ApolloResponse<CreateCustomerAccessTokenMutation.Data>>(relaxed = true).apply {
            every { data?.customerAccessTokenCreate?.customerAccessToken?.accessToken } returns "login-token"
            every { data?.customerAccessTokenCreate?.customerUserErrors } returns emptyList()
        }

        every { apolloClient.mutation(any<CreateCustomerAccessTokenMutation>()) } returns mockk {
            coEvery { execute() } returns tokenResponse
        }

        val user = authRepository.signInWithEmail("valid@email.com", "password")
        assertEquals(mockUser, user)
        verify { SharedPreferenceImpl.saveToSharedPreferenceInGeneral(USER_TOKEN, "login-token") }
    }

    @Test
    fun `signInWithEmail throws when Firebase returns null user`() = runTest {
        every { auth.signInWithEmailAndPassword(any(), any()) } returns
                mockSuccessfulTask(mockAuthResult)
        every { mockAuthResult.user } returns null

        val exception = assertFailsWith<Exception> {
            authRepository.signInWithEmail("email", "pass")
        }
        assertEquals("User is null", exception.message)
    }

    @Test
    fun `getShopifyAccessToken returns token on success`() = runTest {
        val tokenResponse = mockk<ApolloResponse<CreateCustomerAccessTokenMutation.Data>>(relaxed = true).apply {
            every { data?.customerAccessTokenCreate?.customerAccessToken?.accessToken } returns "valid-token"
            every { data?.customerAccessTokenCreate?.customerUserErrors } returns emptyList()
        }

        every { apolloClient.mutation(any<CreateCustomerAccessTokenMutation>()) } returns mockk {
            coEvery { execute() } returns tokenResponse
        }

        val result = authRepository.getShopifyAccessToken("email", "password")
        assertTrue(result.isSuccess)
        assertEquals("valid-token", result.getOrNull())
    }

    @Test
    fun `getShopifyAccessToken returns error on GraphQL error`() = runTest {
        val tokenResponse = mockk<ApolloResponse<CreateCustomerAccessTokenMutation.Data>>(relaxed = true).apply {
            every { data?.customerAccessTokenCreate?.customerAccessToken } returns null
            every { data?.customerAccessTokenCreate?.customerUserErrors } returns listOf(
                mockk { every { message } returns "Invalid credentials" }
            )
        }

        every { apolloClient.mutation(any<CreateCustomerAccessTokenMutation>()) } returns mockk {
            coEvery { execute() } returns tokenResponse
        }

        val result = authRepository.getShopifyAccessToken("bad@email.com", "wrong")
        assertTrue(result.isFailure)
        assertEquals("Invalid credentials", result.exceptionOrNull()?.message)
    }

    @Test
    fun `signInWithGoogle returns user on success`() = runTest {
        val account = mockk<GoogleSignInAccount>(relaxed = true)
        every { account.idToken } returns "google-token"
        every { auth.signInWithCredential(any()) } returns mockSuccessfulTask(mockAuthResult)
        every { mockAuthResult.user } returns mockUser

        val user = authRepository.signInWithGoogle(account)
        assertEquals(mockUser, user)
    }

    @Test
    fun `createShopifyCustomer returns success`() = runTest {
        val shopifyResponse = mockk<ApolloResponse<CreateCustomerMutation.Data>>(relaxed = true).apply {
            every { data?.customerCreate?.customer } returns mockk {
                every { id } returns "shopify-id"
                every { email } returns "test@email.com"
                every { firstName } returns "Test"
                every { lastName } returns "User"
            }
            every { data?.customerCreate?.customerUserErrors } returns emptyList()
        }

        every { apolloClient.mutation(any<CreateCustomerMutation>()) } returns mockk {
            coEvery { execute() } returns shopifyResponse
        }

        val result = authRepository.createShopifyCustomer("Full Name", "test@email.com", "password")
        assertTrue(result.isSuccess)
        assertEquals("shopify-id", result.getOrNull()?.id)
    }

    @Test
    fun `createShopifyCustomerAfterGoogleSignIn returns success`() = runTest {
        every { mockUser.email } returns "google@email.com"
        every { mockUser.displayName } returns "Google User"

        val shopifyResponse = mockk<ApolloResponse<CreateCustomerMutation.Data>>(relaxed = true).apply {
            every { data?.customerCreate?.customer } returns mockk {
                every { id } returns "google-shopify-id"
                every { email } returns "google@email.com"
                every { firstName } returns "Google"
                every { lastName } returns "User"
            }
            every { data?.customerCreate?.customerUserErrors } returns emptyList()
        }

        every { apolloClient.mutation(any<CreateCustomerMutation>()) } returns mockk {
            coEvery { execute() } returns shopifyResponse
        }

        val result = authRepository.createShopifyCustomerAfterGoogleSignIn(mockUser)
        assertTrue(result.isSuccess)
        assertEquals("google@email.com", result.getOrNull()?.email)
    }

    @Test
    fun `createShopifyCustomerAfterGoogleSignIn fails when email is blank`() = runTest {
        every { mockUser.email } returns null
        val result = authRepository.createShopifyCustomerAfterGoogleSignIn(mockUser)
        assertTrue(result.isFailure)
        assertEquals("Google account email is required", result.exceptionOrNull()?.message)
    }

    @Test
    fun `logout clears auth and preferences`() = runTest {
        every { auth.signOut() } just Runs
        authRepository.logout()
        verify { auth.signOut() }
        verify { SharedPreferenceImpl.clearUserData() }
    }

    @Test
    fun `deleteCurrentUser succeeds when user exists`() = runTest {
        every { auth.currentUser } returns mockUser
        every { mockUser.delete() } returns mockSuccessfulTask()
        authRepository.deleteCurrentUser()
    }

    @Test
    fun `signInWithGoogle returns null on failure`() = runTest {
        val account = mockk<GoogleSignInAccount>(relaxed = true)
        every { account.idToken } returns "google-token"
        val exception = Exception("Google auth failed")
        every { auth.signInWithCredential(any()) } returns mockFailedTask(exception)

        val user = authRepository.signInWithGoogle(account)

        assertNull(user)
        verify {
            Log.e(
                "1",
                "Google sign-in failed",
                match<Exception> { it.message == "Google auth failed" }
            )
        }
    }

    @Test
    fun `sendPasswordResetEmail returns success`() = runTest {
        every { auth.sendPasswordResetEmail(any()) } returns mockSuccessfulTask()
        val result = authRepository.sendPasswordResetEmail("reset@email.com")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `sendPasswordResetEmail returns failure on exception`() = runTest {
        val exception = Exception("Reset failed")
        every { auth.sendPasswordResetEmail(any()) } returns mockFailedTask(exception)
        val result = authRepository.sendPasswordResetEmail("invalid@email.com")
        assertTrue(result.isFailure)
        assertEquals("Reset failed", result.exceptionOrNull()?.message)
    }
    @Test
    fun `deleteCurrentUser logs error on failure`() = runTest {
        every { auth.currentUser } returns mockUser
        val exception = Exception("Delete failed")
        every { mockUser.delete() } returns mockFailedTask(exception)

        authRepository.deleteCurrentUser()
        verify {
            Log.e(
                "AuthRepository",
                "Failed to delete user",
                match<Exception> { it.message == "Delete failed" }
            )
        }
    }
}