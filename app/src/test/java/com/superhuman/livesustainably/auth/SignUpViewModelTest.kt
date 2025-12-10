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
class SignUpViewModelTest {

    private lateinit var viewModel: SignUpViewModel
    private lateinit var mockAuthRepository: FakeAuthRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockAuthRepository = FakeAuthRepository()
        viewModel = SignUpViewModel(mockAuthRepository)
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
    fun `onEmailChanged clears email error`() {
        viewModel.onSignUpClicked()
        assertNotNull(viewModel.state.value.emailError)
        
        viewModel.onEmailChanged("test@example.com")
        assertNull(viewModel.state.value.emailError)
    }

    @Test
    fun `onPasswordChanged clears password error`() {
        viewModel.onEmailChanged("test@example.com")
        viewModel.onSignUpClicked()
        assertNotNull(viewModel.state.value.passwordError)
        
        viewModel.onPasswordChanged("password123")
        assertNull(viewModel.state.value.passwordError)
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
    fun `onSignUpClicked with empty email sets error`() {
        viewModel.onEmailChanged("")
        viewModel.onPasswordChanged("password123")
        viewModel.onSignUpClicked()
        
        assertEquals("Email cannot be empty", viewModel.state.value.emailError)
    }

    @Test
    fun `onSignUpClicked with short password sets error`() {
        viewModel.onEmailChanged("test@example.com")
        viewModel.onPasswordChanged("12345")
        viewModel.onSignUpClicked()
        
        assertEquals("Password must be at least 6 characters", viewModel.state.value.passwordError)
    }

    @Test
    fun `onSignUpClicked with valid credentials starts loading`() = runTest {
        viewModel.onEmailChanged("test@example.com")
        viewModel.onPasswordChanged("password123")
        
        viewModel.onSignUpClicked()
        
        assertTrue(viewModel.state.value.isLoading)
    }

    @Test
    fun `onSignUpClicked with successful signup navigates back`() = runTest {
        mockAuthRepository.signupResult = AuthRepository.AuthResult.Success(FakeFirebaseUser())
        
        viewModel.onEmailChanged("test@example.com")
        viewModel.onPasswordChanged("password123")
        viewModel.onSignUpClicked()
        
        advanceUntilIdle()
        
        assertFalse(viewModel.state.value.isLoading)
        assertNotNull(viewModel.state.value.navigateBackToLogin)
    }

    @Test
    fun `onSignUpClicked with failed signup shows error`() = runTest {
        mockAuthRepository.signupResult = AuthRepository.AuthResult.Error("Email already exists")
        
        viewModel.onEmailChanged("test@example.com")
        viewModel.onPasswordChanged("password123")
        viewModel.onSignUpClicked()
        
        advanceUntilIdle()
        
        assertFalse(viewModel.state.value.isLoading)
        assertNotNull(viewModel.state.value.signUpError)
    }

    @Test
    fun `onGoogleIdTokenReceived with successful signup navigates back`() = runTest {
        mockAuthRepository.googleSignInResult = AuthRepository.AuthResult.Success(FakeFirebaseUser())
        
        viewModel.onGoogleIdTokenReceived("valid-token")
        
        advanceUntilIdle()
        
        assertFalse(viewModel.state.value.isLoading)
        assertNotNull(viewModel.state.value.navigateBackToLogin)
    }

    @Test
    fun `onGoogleIdTokenReceived with failed signup shows error`() = runTest {
        mockAuthRepository.googleSignInResult = AuthRepository.AuthResult.Error("Google sign-up failed")
        
        viewModel.onGoogleIdTokenReceived("invalid-token")
        
        advanceUntilIdle()
        
        assertFalse(viewModel.state.value.isLoading)
        assertNotNull(viewModel.state.value.signUpError)
    }

    @Test
    fun `onSignUpError sets error message`() {
        viewModel.onSignUpError("Custom error message")
        assertNotNull(viewModel.state.value.signUpError)
    }
}
