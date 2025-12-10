package com.superhuman.livesustainably.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.superhuman.livesustainably.R
import com.superhuman.livesustainably.ui.theme.LiveSustainablyTheme
import com.superhuman.livesustainably.utils.consume
import kotlinx.coroutines.launch

@Composable
fun SignUpView(
    viewModel: SignUpViewModel = hiltViewModel(),
    onBackToLogin: () -> Unit = {},
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val credentialManager = remember { CredentialManager.create(context) }

    LaunchedEffect(state.value.navigateBackToLogin) {
        state.value.navigateBackToLogin?.consume { onBackToLogin() }
    }

    SignUpContent(
        state = state.value,
        onEmailChanged = viewModel::onEmailChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
        onSignUpClicked = viewModel::onSignUpClicked,
        onGoogleSignUp = {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            kotlinx.coroutines.GlobalScope.launch {
                try {
                    val result = credentialManager.getCredential(context, request)
                    val credential = result.credential
                    if (credential is GoogleIdTokenCredential) {
                        viewModel.onGoogleIdTokenReceived(credential.idToken)
                    }
                } catch (e: GetCredentialException) {
                    viewModel.onSignUpError(e.message ?: "Google sign-up failed")
                }
            }
        },
        onBackToLogin = onBackToLogin,
    )
}

@Composable
private fun SignUpContent(
    state: SignUpState = SignUpState(),
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onSignUpClicked: () -> Unit,
    onGoogleSignUp: () -> Unit,
    onBackToLogin: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = androidx.compose.ui.graphics.Color(0xFFEFF5FF)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 2.dp, bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Image(
                        modifier = Modifier.size(110.dp),
                        painter = painterResource(R.drawable.app_icon),
                        contentDescription = "App Icon",
                    )
                    Text(
                        text = "Sign Up",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = androidx.compose.ui.graphics.Color(0xFF1F2937),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Google sign up
            SocialSignUpButton(
                text = "Sign up with Google",
                iconRes = R.drawable.ic_google,
                onClick = onGoogleSignUp,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Or sign up with Email",
                style = MaterialTheme.typography.titleMedium,
                color = androidx.compose.ui.graphics.Color(0xFF1F2937),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )

            // Email Field
            OutlinedTextField(
                value = state.email,
                onValueChange = onEmailChanged,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                singleLine = true,
                isError = state.emailError != null,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                placeholder = {
                    Text(
                        text = "Enter your email address",
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp)
                    )
                },
            )
            AnimatedVisibility(visible = state.emailError != null) {
                Text(
                    text = state.emailError.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            OutlinedTextField(
                value = state.password,
                onValueChange = onPasswordChanged,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = onTogglePasswordVisibility) {
                        Icon(
                            painter = painterResource(
                                if (state.isPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed
                            ),
                            contentDescription = "Toggle password visibility"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                singleLine = true,
                isError = state.passwordError != null,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                placeholder = {
                    Text(
                        text = "Enter your password",
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp)
                    )
                },
            )
            AnimatedVisibility(visible = state.passwordError != null) {
                Text(
                    text = state.passwordError.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign up button
            Button(
                onClick = onSignUpClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !state.isLoading,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFF1F2937),
                ),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = androidx.compose.ui.graphics.Color.White,
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_email),
                        contentDescription = "Email Icon",
                        modifier = Modifier.size(20.dp),
                        tint = androidx.compose.ui.graphics.Color.White,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign up with Email", color = androidx.compose.ui.graphics.Color.White)
                }
            }

            state.signUpError?.consume { error ->
                AnimatedVisibility(visible = true) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.weight(1f))

            TextButton(onClick = onBackToLogin) {
                Text(
                    text = "Back to Login",
                    color = androidx.compose.ui.graphics.Color(0xFF2563EB),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun SocialSignUpButton(
    text: String,
    iconRes: Int,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .padding(vertical = 6.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = androidx.compose.ui.graphics.Color.White,
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 18.dp,
            vertical = 10.dp
        ),
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = text,
            modifier = Modifier.size(28.dp),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = androidx.compose.ui.graphics.Color(0xFF1F2937),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                lineHeight = 18.sp
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SignUpPreview() {
    LiveSustainablyTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SignUpContent(
                state = SignUpState(),
                onEmailChanged = {},
                onPasswordChanged = {},
                onTogglePasswordVisibility = {},
                onSignUpClicked = {},
                onGoogleSignUp = {},
                onBackToLogin = {},
            )
        }
    }
}
