package com.superhuman.livesustainably.auth

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

sealed class GoogleSignInResult {
    data class Success(val idToken: String) : GoogleSignInResult()
    data class Error(val message: String) : GoogleSignInResult()
    object Cancelled : GoogleSignInResult()
}

class GoogleSignInHelper(
    private val activity: ComponentActivity,
    private val serverClientId: String
) {
    private val credentialManager: CredentialManager = CredentialManager.create(activity)

    suspend fun signIn(): GoogleSignInResult {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId)
                .setAutoSelectEnabled(true)
                .setNonce(null)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = activity
            )

            handleSignInResponse(result)
        } catch (e: NoCredentialException) {
            GoogleSignInResult.Error(getAndroid15FriendlyErrorMessage(e))
        } catch (e: GetCredentialCancellationException) {
            GoogleSignInResult.Cancelled
        } catch (e: GetCredentialException) {
            handleCredentialException(e)
        } catch (e: Exception) {
            GoogleSignInResult.Error("Sign-in failed: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    private fun handleSignInResponse(result: GetCredentialResponse): GoogleSignInResult {
        return when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        GoogleSignInResult.Success(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        GoogleSignInResult.Error("Failed to parse Google ID token")
                    }
                } else {
                    GoogleSignInResult.Error("Unexpected credential type received")
                }
            }
            is GoogleIdTokenCredential -> {
                GoogleSignInResult.Success(credential.idToken)
            }
            else -> {
                GoogleSignInResult.Error("Unexpected credential type received")
            }
        }
    }

    private fun handleCredentialException(e: GetCredentialException): GoogleSignInResult {
        return when {
            e.message?.contains("16") == true && e.message?.contains("Cannot find a matching credential") == true -> {
                if (Build.VERSION.SDK_INT >= 35) {
                    GoogleSignInResult.Error(
                        "No Google account found. Please add a Google account to your device in Settings > Accounts."
                    )
                } else {
                    GoogleSignInResult.Error("No matching Google account found. Please try again.")
                }
            }
            e.message?.contains("Account reauth failed") == true -> {
                GoogleSignInResult.Error(
                    "Please re-authenticate your Google account in device Settings."
                )
            }
            else -> {
                GoogleSignInResult.Error(getAndroid15FriendlyErrorMessage(e))
            }
        }
    }

    private fun getAndroid15FriendlyErrorMessage(e: Exception): String {
        return if (Build.VERSION.SDK_INT >= 35) {
            when {
                e.message?.contains("No credentials available") == true ->
                    "No Google account found. Please add a Google account in Settings > Accounts."
                e.message?.contains("interrupted") == true ->
                    "Sign-in was interrupted. Please try again."
                else ->
                    "Google Sign-In failed. Please ensure you have a Google account set up on this device."
            }
        } else {
            e.localizedMessage ?: "Google Sign-In failed. Please try again."
        }
    }

    suspend fun clearCredentialState() {
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Exception) {
            // Ignore errors when clearing state
        }
    }

    companion object {
        fun create(activity: ComponentActivity, serverClientId: String): GoogleSignInHelper {
            return GoogleSignInHelper(activity, serverClientId)
        }
    }
}
