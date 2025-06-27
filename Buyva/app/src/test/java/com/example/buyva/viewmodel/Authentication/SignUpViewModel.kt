package com.example.buyva.viewmodel.Authentication

import android.content.Context
import com.example.buyva.data.model.CustomerData
import com.example.buyva.data.repository.Authentication.IAuthRepository
import com.example.buyva.features.authentication.login.viewmodel.UserSessionManager
import com.example.buyva.features.authentication.signup.viewmodel.SignupViewModel
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import io.mockk.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SignupViewModelTest {
    private val mockContext = mockk<Context>(relaxed = true)
    private lateinit var testDispatcher: TestDispatcher

    private lateinit var authRepository: IAuthRepository
    private lateinit var context: Context
    private lateinit var viewModel: SignupViewModel

    @Before
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher) // <-- عشان نتحكم في viewModelScope

        authRepository = mockk()
        context = mockk(relaxed = true)

        mockkObject(UserSessionManager)
        mockkObject(SharedPreferenceImpl)

        viewModel = SignupViewModel(authRepository, context)
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `signUp with empty fields sets error`() = runTest {
        viewModel.signUp("", "", "", "")
        assertEquals("Please fill all fields", viewModel.error.value)
    }

    @Test
    fun `signUp with non-matching passwords sets error`() = runTest {
        viewModel.signUp("Youssef", "test@example.com", "123456", "654321")
        assertEquals("Passwords do not match", viewModel.error.value)
    }


    @Test
    fun `resendVerificationEmail with valid user sends email and updates state`() = runTest {
        // Arrange
        val mockUser = mockk<FirebaseUser>(relaxed = true)
        every { mockUser.sendEmailVerification() } returns Tasks.forResult(null)
        coEvery { authRepository.reloadFirebaseUser() } returns mockUser

        // Act
        viewModel.resendVerificationEmail()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(true, viewModel.verificationSent.value)
        assertEquals("Verification email resent. Please check your inbox.", viewModel.error.value)
    }

    @Test
    fun `signUpWithGoogle success sets user and customerData`() = runTest {
        // Arrange
        val mockAccount = mockk<GoogleSignInAccount>()
        val mockUser = mockk<FirebaseUser>(relaxed = true)
        val mockCustomer = CustomerData("123", "test@example.com", "Youssef", "Allam")

        coEvery { authRepository.signInWithGoogle(mockAccount) } returns mockUser
        coEvery { authRepository.createShopifyCustomerAfterGoogleSignIn(mockUser) } returns Result.success(mockCustomer)

        // Mock static methods
        mockkStatic(UserSessionManager::class)
        mockkStatic(SharedPreferenceImpl::class)

        every { UserSessionManager.setGuestMode(false) } just Runs
        every { SharedPreferenceImpl.saveCustomer(any(), any(), any(), any(), any()) } just Runs
        every { SharedPreferenceImpl.saveToSharedPreferenceInGeneral(any(), any()) } just Runs

        // Act
        viewModel.signUpWithGoogle(mockAccount)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(mockUser, viewModel.user.value)
        assertEquals(mockCustomer, viewModel.customerData.value)
    }
}


