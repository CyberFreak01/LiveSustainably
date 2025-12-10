package com.superhuman.livesustainably.auth.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.superhuman.livesustainably.auth.data.AuthRepository.AuthResult
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun checkEmailExists(email: String): Boolean {
        return try {
            val result = firebaseAuth.fetchSignInMethodsForEmail(email).await()
            val methods = result?.signInMethods.orEmpty()
            methods.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun signup(email: String, password: String): AuthResult {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result?.user?.let { AuthResult.Success(it) }
                ?: AuthResult.Error("Signup succeeded but user is null")
        } catch (e: Exception) {
            if (e is FirebaseAuthUserCollisionException) {
                AuthResult.Error("Email already exists")
            } else {
                AuthResult.Error(e.message ?: "Signup failed")
            }
        }
    }

    override suspend fun login(email: String, password: String): AuthResult {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result?.user?.let { AuthResult.Success(it) }
                ?: AuthResult.Error("Login succeeded but user is null")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Login failed")
        }
    }

    override suspend fun signInWithGoogle(idToken: String): AuthResult {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            result?.user?.let { AuthResult.Success(it) }
                ?: AuthResult.Error("Google sign-in succeeded but user is null")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Google sign-in failed")
        }
    }
}