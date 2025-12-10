package com.superhuman.livesustainably.auth.data

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun checkEmailExists(email: String): Boolean
    suspend fun signup(email: String, password: String): AuthResult
    suspend fun login(email: String, password: String): AuthResult
    suspend fun signInWithGoogle(idToken: String): AuthResult

    sealed class AuthResult {
        data class Success(val user: FirebaseUser) : AuthResult()
        data class Error(val message: String) : AuthResult()
    }
}