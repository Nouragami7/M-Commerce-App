package com.example.buyva.viewmodel.authentication

import com.example.buyva.data.repository.Authentication.IAuthRepository
import com.example.buyva.features.authentication.login.viewmodel.LoginViewModel
import com.example.buyva.features.authentication.login.viewmodel.UserSessionManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
@ExperimentalCoroutinesApi
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var authRepository: IAuthRepository
    private lateinit var viewModel: LoginViewModel
    private lateinit var mockUser: FirebaseUser
    private lateinit var mockGoogleAccount: GoogleSignInAccount

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk(relaxed = true)
        mockUser = mockk {
            every { isEmailVerified } returns true
        }
        mockkObject(UserSessionManager)
        every { UserSessionManager.setGuestMode(any()) } just Runs

        mockGoogleAccount = mockk()
        viewModel = LoginViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `signIn with valid credentials and verified email updates loginState`() = runTest {
            val fakeUser = mockk<FirebaseUser>(relaxed = true) {
                every { isEmailVerified } returns true
            }

            coEvery { authRepository.signInWithEmail("test@example.com", "123456") } returns fakeUser

            viewModel.signIn("test@example.com", "123456")

            advanceUntilIdle()

            assertEquals(fakeUser, viewModel.loginState.value)
            assertEquals(null, viewModel.errorMessage.value)
        }


    @Test
    fun `signIn with unverified email shows error and logs out`() = runTest {
        // Arrange
        val unverifiedUser = mockk<FirebaseUser> {
            every { isEmailVerified } returns false
        }
        coEvery { authRepository.signInWithEmail("test@example.com", "password") } returns unverifiedUser
        coEvery { authRepository.logout() } returns Unit

        // Act
        viewModel.signIn("test@example.com", "password")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(
            "Please verify your email first. Check your inbox or spam folder.",
            viewModel.errorMessage.value
        )
        assertNull(viewModel.loginState.value)
        verify { authRepository.logout() }
    }

    @Test
    fun `signIn with Google success sets login state`() = runTest {
        // Arrange
        mockkObject(UserSessionManager)
        every { UserSessionManager.setGuestMode(false) } just Runs

        val mockUser = mockk<FirebaseUser>(relaxed = true)
        val mockGoogleAccount = mockk<GoogleSignInAccount>()

        coEvery { authRepository.signInWithGoogle(mockGoogleAccount) } returns mockUser

        val viewModel = LoginViewModel(authRepository)

        // Act
        viewModel.signInWithGoogle(mockGoogleAccount)
        advanceUntilIdle()

        // Assert
        assertEquals(mockUser, viewModel.loginState.value)
        assertNull(viewModel.errorMessage.value)
    }

    @Test
    fun `forgotPassword with valid email shows success message`() = runTest {
        // Arrange
        coEvery { authRepository.sendPasswordResetEmail("test@example.com") } returns Result.success(Unit)

        // Act
        viewModel.forgotPassword("test@example.com")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals("Password reset email sent. Please check your inbox.", viewModel.errorMessage.value)
    }
}