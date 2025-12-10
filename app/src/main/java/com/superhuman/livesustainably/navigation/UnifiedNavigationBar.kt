package com.superhuman.livesustainably.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superhuman.livesustainably.R

sealed class NavBarDestination(
    val route: String,
    val label: String
) {
    object Home : NavBarDestination("home", "HOME")
    object Chat : NavBarDestination("chat", "CHAT")
    object Leaderboard : NavBarDestination("leaderboard", "LEADERBOARD")
    object Profile : NavBarDestination("profile", "PROFILE")
}

@Composable
fun UnifiedBottomNavigationBar(
    currentRoute: String,
    onNavigateToHome: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = modifier
    ) {
        // Home
        NavigationBarItem(
            selected = currentRoute == NavBarDestination.Home.route,
            onClick = onNavigateToHome,
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home"
                )
            },
            label = {
                Text(
                    text = "HOME",
                    fontSize = 11.sp,
                    fontWeight = if (currentRoute == NavBarDestination.Home.route) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2563EB),
                selectedTextColor = Color(0xFF2563EB),
                unselectedIconColor = Color(0xFF9CA3AF),
                unselectedTextColor = Color(0xFF9CA3AF),
                indicatorColor = Color.Transparent
            )
        )

        // Chat
        NavigationBarItem(
            selected = currentRoute == NavBarDestination.Chat.route,
            onClick = onNavigateToChat,
            icon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Chat"
                )
            },
            label = {
                Text(
                    text = "CHAT",
                    fontSize = 11.sp,
                    fontWeight = if (currentRoute == NavBarDestination.Chat.route) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2563EB),
                selectedTextColor = Color(0xFF2563EB),
                unselectedIconColor = Color(0xFF9CA3AF),
                unselectedTextColor = Color(0xFF9CA3AF),
                indicatorColor = Color.Transparent
            )
        )

        // Leaderboard
        NavigationBarItem(
            selected = currentRoute == NavBarDestination.Leaderboard.route,
            onClick = onNavigateToLeaderboard,
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.finishflag),
                    contentDescription = "Leaderboard",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = "LEADERBOARD",
                    fontSize = 11.sp,
                    fontWeight = if (currentRoute == NavBarDestination.Leaderboard.route) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2563EB),
                selectedTextColor = Color(0xFF2563EB),
                unselectedIconColor = Color(0xFF9CA3AF),
                unselectedTextColor = Color(0xFF9CA3AF),
                indicatorColor = Color.Transparent
            )
        )

        // Profile
        NavigationBarItem(
            selected = currentRoute == NavBarDestination.Profile.route,
            onClick = onNavigateToProfile,
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile"
                )
            },
            label = {
                Text(
                    text = "PROFILE",
                    fontSize = 11.sp,
                    fontWeight = if (currentRoute == NavBarDestination.Profile.route) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2563EB),
                selectedTextColor = Color(0xFF2563EB),
                unselectedIconColor = Color(0xFF9CA3AF),
                unselectedTextColor = Color(0xFF9CA3AF),
                indicatorColor = Color.Transparent
            )
        )
    }
}
