package com.superhuman.livesustainably.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalConfiguration
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
fun AuthView(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToSignUp: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val credentialManager = remember { CredentialManager.create(context) }

    LaunchedEffect(state.value.navigateToSignUp) {
        state.value.navigateToSignUp?.consume { onNavigateToSignUp() }
    }

    LaunchedEffect(state.value.navigateToHome) {
        state.value.navigateToHome?.consume { onNavigateToHome() }
    }

    Auth(
        state = state.value,
        onEmailChanged = viewModel::onEmailChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
        onEmailLoginClicked = viewModel::onEmailLoginClicked,
        onGoogleLoginClicked = {
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
                    viewModel.onLoginError(e.message ?: "Google sign-in failed")
                }
            }
        },
        onNavigateToSignUp = onNavigateToSignUp,
        onNavigateToHome = onNavigateToHome,
    )
}

@Composable
private fun Auth(
    state: AuthState = AuthState(),
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onEmailLoginClicked: () -> Unit,
    onGoogleLoginClicked: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToHome: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val isCompactHeight = screenHeight < 700.dp
    val isCompactWidth = screenWidth < 360.dp
    val scrollState = rememberScrollState()
    
    val iconSize = when {
        isCompactHeight && isCompactWidth -> 70.dp
        isCompactHeight -> 80.dp
        else -> 110.dp
    }
    
    val verticalSpacing = if (isCompactHeight) 8.dp else 12.dp
    val fieldHeight = if (isCompactHeight) 52.dp else 58.dp
    val buttonHeight = if (isCompactHeight) 48.dp else 56.dp

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(color = androidx.compose.ui.graphics.Color(0xFFEFF5FF)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = if (isCompactWidth) 16.dp else 24.dp)
                    .padding(top = 2.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (isCompactHeight) 4.dp else 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Image(
                            modifier = Modifier.size(iconSize),
                            painter = painterResource(R.drawable.app_icon),
                            contentDescription = "App Icon",
                        )
                    }
                    Image(
                        modifier = Modifier.size(if (isCompactWidth) 26.dp else 32.dp),
                        painter = painterResource(R.drawable.ic_flag_uk),
                        contentDescription = "Language Selector",
                    )
                }

                SocialLoginButton(
                    text = "Log in with Google",
                    iconRes = R.drawable.ic_google,
                    onClick = onGoogleLoginClicked,
                    isCompact = isCompactHeight
                )

                Spacer(modifier = Modifier.height(verticalSpacing))

                SocialLoginButton(
                    text = "Log in with Facebook",
                    iconRes = R.drawable.ic_facebook,
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Coming soon")
                        }
                    },
                    isCompact = isCompactHeight
                )

                Spacer(modifier = Modifier.height(if (isCompactHeight) 12.dp else 20.dp))

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = if (isCompactHeight) 6.dp else 8.dp),
                    color = androidx.compose.ui.graphics.Color(0xFFE1E6F0),
                    thickness = 1.dp
                )

                Text(
                    text = "Sign in with Email",
                    style = MaterialTheme.typography.titleMedium,
                    color = androidx.compose.ui.graphics.Color(0xFF1F2937),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = if (isCompactHeight) 2.dp else 4.dp)
                        .padding(bottom = if (isCompactHeight) 10.dp else 16.dp),
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = state.email,
                    onValueChange = onEmailChanged,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(fieldHeight),
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

                Spacer(modifier = Modifier.height(if (isCompactHeight) 10.dp else 16.dp))

                AnimatedVisibility(visible = state.showPasswordField) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Enter your password",
                            style = MaterialTheme.typography.bodySmall,
                            color = androidx.compose.ui.graphics.Color(0xFF6B7280),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = if (isCompactHeight) 6.dp else 8.dp)
                        )
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
                                .height(fieldHeight),
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
                    }
                }

                Button(
                    onClick = onEmailLoginClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(buttonHeight),
                    enabled = !state.isLoading && state.email.isNotBlank() && state.password.isNotBlank(),
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
                        Text(
                            text = "Sign in with Email",
                            color = androidx.compose.ui.graphics.Color.White
                        )
                    }
                }

                state.loginError?.consume { error ->
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

                Spacer(modifier = Modifier.height(if (isCompactHeight) 16.dp else 24.dp))

                Text(
                    text = "Do you work for an organization that uses SSO?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.ui.graphics.Color(0xFF6B7280),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (isCompactHeight) 8.dp else 12.dp),
                )

                Button(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Coming soon")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(buttonHeight),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFFF3F4F6),
                    ),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lock),
                        contentDescription = "Lock Icon",
                        modifier = Modifier.size(20.dp),
                        tint = androidx.compose.ui.graphics.Color(0xFF1F2937),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log in with SSO", color = androidx.compose.ui.graphics.Color(0xFF1F2937))
                }

                Spacer(modifier = Modifier.height(if (isCompactHeight) 16.dp else 24.dp))

                TextButton(
                    modifier = Modifier.padding(bottom = 12.dp),
                    onClick = { onNavigateToSignUp() }
                ) {
                    Text(
                        text = "Go to Registration",
                        color = androidx.compose.ui.graphics.Color(0xFF2563EB),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "→",
                        color = androidx.compose.ui.graphics.Color(0xFF2563EB),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun SocialLoginButton(
    text: String,
    iconRes: Int,
    onClick: () -> Unit,
    isCompact: Boolean = false
) {
    val buttonHeight = if (isCompact) 48.dp else 58.dp
    val verticalPadding = if (isCompact) 4.dp else 6.dp
    val iconSize = if (isCompact) 24.dp else 28.dp
    val fontSize = if (isCompact) 13.sp else 14.sp
    
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(buttonHeight)
            .padding(vertical = verticalPadding),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = androidx.compose.ui.graphics.Color.White,
        ),
        contentPadding = PaddingValues(
            horizontal = 18.dp,
            vertical = if (isCompact) 8.dp else 10.dp
        ),
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = text,
            modifier = Modifier.size(iconSize),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = androidx.compose.ui.graphics.Color(0xFF1F2937),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = fontSize,
                lineHeight = 18.sp
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthPreview() {
    LiveSustainablyTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Auth(
                state = AuthState(),
                onEmailChanged = { },
                onPasswordChanged = { },
                onTogglePasswordVisibility = {},
                onEmailLoginClicked = {},
                onGoogleLoginClicked = {},
                onNavigateToSignUp = {},
                onNavigateToHome = {},
            )
        }
    }
}
