package com.superhuman.livesustainably

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.superhuman.livesustainably.auth.AuthView
import com.superhuman.livesustainably.auth.SignUpView
import com.superhuman.livesustainably.home.HomeView

sealed class AppAuthState {
    object Loading : AppAuthState()
    object Authenticated : AppAuthState()
    object Unauthenticated : AppAuthState()
}

sealed class NavDestinations(val route: String) {
    object Login : NavDestinations("login")
    object SignUp : NavDestinations("signup")
    object Home : NavDestinations("home")
}

@Composable
fun AppNavigator(navController: NavHostController, authState: AppAuthState) {

    val startDestination = when (authState) {
        AppAuthState.Authenticated -> NavDestinations.Home.route
        else -> NavDestinations.Login.route
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(NavDestinations.Login.route) {
            AuthView(
                onNavigateToSignUp = {
                    navController.navigate(NavDestinations.SignUp.route)
                }
            )
        }

        composable(NavDestinations.SignUp.route) {
            SignUpView(
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable(NavDestinations.Home.route) {
            HomeView()
        }
    }
}