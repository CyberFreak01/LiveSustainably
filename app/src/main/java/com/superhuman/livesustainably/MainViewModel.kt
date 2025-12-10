package com.superhuman.livesustainably

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _authState = MutableStateFlow<AppAuthState>(AppAuthState.Loading)
    val authState: StateFlow<AppAuthState> = _authState

    init {
        checkUserSession()
    }

    private fun checkUserSession() {
        firebaseAuth.addAuthStateListener { auth ->
            if (auth.currentUser == null) {
                _authState.value = AppAuthState.Unauthenticated
            } else {
                _authState.value = AppAuthState.Authenticated
            }
        }
    }
}