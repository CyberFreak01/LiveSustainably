package com.superhuman.livesustainably

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.superhuman.livesustainably.ui.theme.LiveSustainablyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Keep the splash screen visible until the auth state is determined.
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener {
            mainViewModel.authState.value != AppAuthState.Loading
        }

        setContent {
            LiveSustainablyTheme {
                val navController = rememberNavController()
                val authState = mainViewModel.authState.collectAsStateWithLifecycle().value

                // Show the navigator once the auth state is not loading
                if (authState != AppAuthState.Loading) {
                    AppNavigator(navController, authState)
                }
            }
        }
    }
}