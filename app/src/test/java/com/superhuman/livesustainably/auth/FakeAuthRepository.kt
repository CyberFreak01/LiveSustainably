package com.superhuman.livesustainably.auth

import com.google.firebase.auth.FirebaseUser
import com.superhuman.livesustainably.auth.data.AuthRepository

class FakeAuthRepository : AuthRepository {
    var loginResult: AuthRepository.AuthResult = AuthRepository.AuthResult.Success(FakeFirebaseUser())
    var signupResult: AuthRepository.AuthResult = AuthRepository.AuthResult.Success(FakeFirebaseUser())
    var googleSignInResult: AuthRepository.AuthResult = AuthRepository.AuthResult.Success(FakeFirebaseUser())
    var emailExists: Boolean = false

    override suspend fun checkEmailExists(email: String): Boolean = emailExists

    override suspend fun signup(email: String, password: String): AuthRepository.AuthResult = signupResult

    override suspend fun login(email: String, password: String): AuthRepository.AuthResult = loginResult

    override suspend fun signInWithGoogle(idToken: String): AuthRepository.AuthResult = googleSignInResult
}

abstract class FakeFirebaseUser : FirebaseUser() {
    override fun getUid(): String = "fake-uid"
    override fun getProviderId(): String = "firebase"
    override fun isAnonymous(): Boolean = false
    override fun isEmailVerified(): Boolean = true
    override fun getEmail(): String? = "test@example.com"
    override fun getDisplayName(): String? = "Test User"
    override fun getPhotoUrl(): android.net.Uri? = null
    override fun getPhoneNumber(): String? = null
    override fun getTenantId(): String? = null
}
