package com.superhuman.livesustainably.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.superhuman.livesustainably.auth.data.AuthRepository
import com.superhuman.livesustainably.utils.OneTimeEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SignUpState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val signUpError: OneTimeEvent<String>? = null,
    val navigateBackToLogin: OneTimeEvent<Unit>? = null,
)

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state: StateFlow<SignUpState> = _state.asStateFlow()

    fun onEmailChanged(email: String) {
        _state.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChanged(password: String) {
        _state.update { it.copy(password = password, passwordError = null) }
    }

    fun onTogglePasswordVisibility() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onSignUpClicked() {
        val current = _state.value

        if (current.email.isBlank()) {
            _state.update { it.copy(emailError = "Email cannot be empty") }
            return
        }

        if (current.password.length < 6) {
            _state.update { it.copy(passwordError = "Password must be at least 6 characters") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = authRepository.signup(current.email, current.password)) {
                is AuthRepository.AuthResult.Error -> _state.update {
                    it.copy(
                        isLoading = false,
                        signUpError = OneTimeEvent(result.message)
                    )
                }

                is AuthRepository.AuthResult.Success -> _state.update {
                    it.copy(
                        isLoading = false,
                        navigateBackToLogin = OneTimeEvent(Unit)
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
                        signUpError = OneTimeEvent(result.message)
                    )
                }

                is AuthRepository.AuthResult.Success -> _state.update {
                    it.copy(
                        isLoading = false,
                        navigateBackToLogin = OneTimeEvent(Unit)
                    )
                }
            }
        }
    }

    fun onSignUpError(message: String) {
        _state.update { it.copy(signUpError = OneTimeEvent(message)) }
    }

    fun onNavigateBackHandled() {
        _state.update { it.copy(navigateBackToLogin = null) }
    }
}
