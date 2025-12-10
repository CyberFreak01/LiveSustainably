package com.superhuman.livesustainably

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.superhuman.livesustainably.auth.AuthView
import com.superhuman.livesustainably.auth.SignUpView
import com.superhuman.livesustainably.chatbot.ChatbotView
import com.superhuman.livesustainably.feed.FeedView
import com.superhuman.livesustainably.home.HomeView
import com.superhuman.livesustainably.leaderboard.LeaderboardView
import com.superhuman.livesustainably.map.MapView
import com.superhuman.livesustainably.profile.ProfileView

sealed class AppAuthState {
    object Loading : AppAuthState()
    object Authenticated : AppAuthState()
    object Unauthenticated : AppAuthState()
}

sealed class NavDestinations(val route: String) {
    object Login : NavDestinations("login")
    object SignUp : NavDestinations("signup")
    object Home : NavDestinations("home")
    object Leaderboard : NavDestinations("leaderboard")
    object Feed : NavDestinations("feed")
    object Map : NavDestinations("map")
    object Chatbot : NavDestinations("chatbot")
    object Profile : NavDestinations("profile")
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
            HomeView(
                onNavigateToLeaderboard = {
                    navController.navigate(NavDestinations.Leaderboard.route)
                },
                onNavigateToFeed = {
                    navController.navigate(NavDestinations.Feed.route)
                },
                onNavigateToChat = {
                    navController.navigate(NavDestinations.Chatbot.route)
                },
                onNavigateToProfile = {
                    navController.navigate(NavDestinations.Profile.route)
                },
                onNavigateToMap = {
                    navController.navigate(NavDestinations.Map.route)
                }
            )
        }

        composable(NavDestinations.Leaderboard.route) {
            LeaderboardView(
                onNavigateToHome = {
                    navController.navigate(NavDestinations.Home.route) {
                        popUpTo(NavDestinations.Home.route) { inclusive = true }
                    }
                },
                onNavigateToChat = { navController.navigate(NavDestinations.Chatbot.route) },
                onNavigateToProfile = { navController.navigate(NavDestinations.Profile.route) }
            )
        }

        composable(NavDestinations.Feed.route) {
            FeedView(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavDestinations.Map.route) {
            MapView(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(NavDestinations.Home.route) {
                        popUpTo(NavDestinations.Home.route) { inclusive = true }
                    }
                },
                onNavigateToProfile = { navController.navigate(NavDestinations.Profile.route) },
                onNavigateToChat = { navController.navigate(NavDestinations.Chatbot.route) }
            )
        }

        composable(NavDestinations.Chatbot.route) {
            ChatbotView(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(NavDestinations.Home.route) {
                        popUpTo(NavDestinations.Home.route) { inclusive = true }
                    }
                },
                onNavigateToLeaderboard = { navController.navigate(NavDestinations.Leaderboard.route) },
                onNavigateToProfile = { navController.navigate(NavDestinations.Profile.route) }
            )
        }

        composable(NavDestinations.Profile.route) {
            ProfileView(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(NavDestinations.Home.route) {
                        popUpTo(NavDestinations.Home.route) { inclusive = true }
                    }
                },
                onNavigateToChat = { navController.navigate(NavDestinations.Chatbot.route) },
                onNavigateToLeaderboard = { navController.navigate(NavDestinations.Leaderboard.route) },
                onLogout = {
                    navController.navigate(NavDestinations.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
