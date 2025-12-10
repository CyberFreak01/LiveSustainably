package com.superhuman.livesustainably.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.superhuman.livesustainably.auth.data.AuthRepository
import com.superhuman.livesustainably.utils.OneTimeEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val showPasswordField: Boolean = true,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val loginError: OneTimeEvent<String>? = null,
    val navigateToSignUp: OneTimeEvent<Unit>? = null,
    val navigateToHome: OneTimeEvent<Unit>? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun onEmailChanged(email: String) {
        _state.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChanged(password: String) {
        _state.update { it.copy(password = password, passwordError = null) }
    }

    fun onTogglePasswordVisibility() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onLoginError(message: String) {
        _state.update { it.copy(loginError = OneTimeEvent(message)) }
    }

    fun onEmailLoginClicked() {
        val current = _state.value
        val email = current.email
        val password = current.password

        // Validate email
        if (email.isBlank()) {
            _state.update { it.copy(emailError = "Email cannot be empty") }
            return
        }

        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _state.update { it.copy(emailError = "Please enter a valid email address") }
            return
        }

        // Validate password
        if (password.isBlank()) {
            _state.update { it.copy(passwordError = "Password cannot be empty") }
            return
        }

        if (password.length < 6) {
            _state.update { it.copy(passwordError = "Password must be at least 6 characters") }
            return
        }

        // Perform login
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, emailError = null, passwordError = null) }
            when (val result = authRepository.login(email, password)) {
                is AuthRepository.AuthResult.Error -> _state.update {
                    it.copy(
                        isLoading = false,
                        loginError = OneTimeEvent(result.message)
                    )
                }

                is AuthRepository.AuthResult.Success -> _state.update {
                    it.copy(
                        isLoading = false,
                        navigateToHome = OneTimeEvent(Unit)
                    )
                }
            }
        }
    }

    fun onGoogleIdTokenReceived(idToken: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = authRepository.signInWithGoogle(idToken)) {
                is AuthRepository.AuthResult.Error -> _state.update {
                    it.copy(
                        isLoading = false,
                        loginError = OneTimeEvent(result.message)
                    )
                }

                is AuthRepository.AuthResult.Success -> _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onNavigateToSignUpHandled() {
        _state.update { it.copy(navigateToSignUp = null) }
    }
}
