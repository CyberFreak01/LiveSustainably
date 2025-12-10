package com.superhuman.livesustainably.auth

import com.superhuman.livesustainably.auth.data.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private lateinit var mockAuthRepository: FakeAuthRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockAuthRepository = FakeAuthRepository()
        viewModel = AuthViewModel(mockAuthRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has empty fields`() {
        val state = viewModel.state.value
        assertEquals("", state.email)
        assertEquals("", state.password)
        assertFalse(state.isLoading)
        assertNull(state.emailError)
        assertNull(state.passwordError)
    }

    @Test
    fun `onEmailChanged updates email in state`() {
        viewModel.onEmailChanged("test@example.com")
        assertEquals("test@example.com", viewModel.state.value.email)
    }

    @Test
    fun `onPasswordChanged updates password in state`() {
        viewModel.onPasswordChanged("password123")
        assertEquals("password123", viewModel.state.value.password)
    }

    @Test
    fun `onTogglePasswordVisibility toggles visibility`() {
        assertFalse(viewModel.state.value.isPasswordVisible)
        viewModel.onTogglePasswordVisibility()
        assertTrue(viewModel.state.value.isPasswordVisible)
        viewModel.onTogglePasswordVisibility()
        assertFalse(viewModel.state.value.isPasswordVisible)
    }

    @Test
    fun `onEmailLoginClicked with empty email sets error`() {
        viewModel.onEmailChanged("")
        viewModel.onPasswordChanged("password123")
        viewModel.onEmailLoginClicked()
        
        assertEquals("Email cannot be empty", viewModel.state.value.emailError)
    }

    @Test
    fun `onEmailLoginClicked with empty password sets error`() {
        viewModel.onEmailChanged("test@example.com")
        viewModel.onPasswordChanged("")
        viewModel.onEmailLoginClicked()
        
        assertEquals("Password cannot be empty", viewModel.state.value.passwordError)
    }

    @Test
    fun `onEmailLoginClicked with short password sets error`() {
        viewModel.onEmailChanged("test@example.com")
        viewModel.onPasswordChanged("12345")
        viewModel.onEmailLoginClicked()
        
        assertEquals("Password must be at least 6 characters", viewModel.state.value.passwordError)
    }

    @Test
    fun `onEmailLoginClicked with valid credentials starts loading`() = runTest {
        viewModel.onEmailChanged("test@example.com")
        viewModel.onPasswordChanged("password123")
        
        viewModel.onEmailLoginClicked()
        
        assertTrue(viewModel.state.value.isLoading)
    }

    @Test
    fun `onEmailLoginClicked with successful login navigates to home`() = runTest {
        mockAuthRepository.loginResult = AuthRepository.AuthResult.Success(FakeFirebaseUser())
        
        viewModel.onEmailChanged("test@example.com")
        viewModel.onPasswordChanged("password123")
        viewModel.onEmailLoginClicked()
        
        advanceUntilIdle()
        
        assertFalse(viewModel.state.value.isLoading)
        assertNotNull(viewModel.state.value.navigateToHome)
    }

    @Test
    fun `onEmailLoginClicked with failed login shows error`() = runTest {
        mockAuthRepository.loginResult = AuthRepository.AuthResult.Error("Invalid credentials")
        
        viewModel.onEmailChanged("test@example.com")
        viewModel.onPasswordChanged("password123")
        viewModel.onEmailLoginClicked()
        
        advanceUntilIdle()
        
        assertFalse(viewModel.state.value.isLoading)
        assertNotNull(viewModel.state.value.loginError)
    }

    @Test
    fun `onGoogleIdTokenReceived with successful login stops loading`() = runTest {
        mockAuthRepository.googleSignInResult = AuthRepository.AuthResult.Success(FakeFirebaseUser())
        
        viewModel.onGoogleIdTokenReceived("valid-token")
        
        advanceUntilIdle()
        
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `onGoogleIdTokenReceived with failed login shows error`() = runTest {
        mockAuthRepository.googleSignInResult = AuthRepository.AuthResult.Error("Google sign-in failed")
        
        viewModel.onGoogleIdTokenReceived("invalid-token")
        
        advanceUntilIdle()
        
        assertFalse(viewModel.state.value.isLoading)
        assertNotNull(viewModel.state.value.loginError)
    }

    @Test
    fun `onLoginError sets error message`() {
        viewModel.onLoginError("Custom error message")
        assertNotNull(viewModel.state.value.loginError)
    }
}
