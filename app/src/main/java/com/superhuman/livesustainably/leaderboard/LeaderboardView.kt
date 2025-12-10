package com.superhuman.livesustainably.leaderboard

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.superhuman.livesustainably.R
import com.superhuman.livesustainably.navigation.NavBarDestination
import com.superhuman.livesustainably.navigation.UnifiedBottomNavigationBar

@Composable
fun LeaderboardView(
    viewModel: LeaderboardViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            UnifiedBottomNavigationBar(
                currentRoute = NavBarDestination.Leaderboard.route,
                onNavigateToHome = onNavigateToHome,
                onNavigateToChat = onNavigateToChat,
                onNavigateToLeaderboard = { },
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                LeaderboardHeaderSection(
                    userPosition = state.userPosition,
                    leagueName = state.leagueName,
                    leagueSubtitle = state.leagueSubtitle,
                    timeRemaining = state.timeRemaining,
                    streakCount = state.streakCount,
                    starCount = state.starCount,
                    roseCount = state.roseCount,
                    lockedLeagues = state.lockedLeagues
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Position",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF9CA3AF)
                    )
                    Text(
                        text = "Points",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }

            item {
                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF2563EB))
                    }
                }
            }

            itemsIndexed(
                items = state.players,
                key = { _, player -> player.id }
            ) { index, player ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(
                            top = if (index == 0) 0.dp else 0.dp,
                            bottom = if (index == state.players.size - 1) 24.dp else 0.dp
                        )
                        .animateContentSize(),
                    shape = when {
                        state.players.size == 1 -> RoundedCornerShape(20.dp)
                        index == 0 -> RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                        index == state.players.size - 1 -> RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                        else -> RoundedCornerShape(0.dp)
                    },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (index == 0 || index == state.players.size - 1) 2.dp else 0.dp
                    )
                ) {
                    LeaderboardPlayerItem(
                        player = player,
                        isTopThree = player.position <= 3,
                        onFollowClick = { viewModel.toggleFollow(player.id) }
                    )
                    if (index < state.players.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = Color(0xFFE5E7EB)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardHeaderSection(
    userPosition: Int,
    leagueName: String,
    leagueSubtitle: String,
    timeRemaining: String,
    streakCount: Int,
    starCount: Int,
    roseCount: Int,
    lockedLeagues: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(
                RoundedCornerShape(
                    bottomStart = 32.dp,
                    bottomEnd = 32.dp
                )
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2563EB),
                        Color(0xFF1D4ED8)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatBadge(icon = "🔥", count = streakCount)
                Spacer(modifier = Modifier.width(12.dp))
                StatBadge(icon = "⭐", count = starCount)
                Spacer(modifier = Modifier.width(12.dp))
                StatBadge(icon = "🌹", count = roseCount)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFE4B5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.seed_icon),
                        contentDescription = "Seed",
                        modifier = Modifier.size(50.dp),
                        tint = Color.Unspecified
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                repeat(lockedLeagues) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    if (it < lockedLeagues - 1) {
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = leagueName,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = leagueSubtitle,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = { },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "How does it work?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Timer",
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = timeRemaining,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                }
            }
        }
    }
}

@Composable
fun StatBadge(icon: String, count: Int) {
    Surface(
        modifier = Modifier
            .height(40.dp)
            .widthIn(min = 80.dp),
        shape = RoundedCornerShape(28.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = count.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
        }
    }
}

@Composable
fun LeaderboardPlayerItem(
    player: LeaderboardPlayer,
    isTopThree: Boolean = false,
    onFollowClick: () -> Unit
) {
    val positionColor = when (player.position) {
        1 -> Color(0xFFFFD700)
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> Color(0xFF1F2937)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.width(30.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isTopThree) {
                Text(
                    text = when (player.position) {
                        1 -> "🥇"
                        2 -> "🥈"
                        3 -> "🥉"
                        else -> player.position.toString()
                    },
                    fontSize = 20.sp
                )
            } else {
                Text(
                    text = player.position.toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = positionColor
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Box(modifier = Modifier.size(48.dp)) {
            if (player.avatarUrl.isNotEmpty()) {
                AsyncImage(
                    model = player.avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(
                            when (player.position) {
                                1 -> Color(0xFFFFF3CD)
                                2 -> Color(0xFFE8E8E8)
                                3 -> Color(0xFFFFE5D0)
                                else -> Color(0xFFE5E7EB)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = positionColor.copy(alpha = 0.7f)
                    )
                }
            }

            if (player.countryFlag.isNotEmpty()) {
                Text(
                    text = player.countryFlag,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.BottomStart)
                        .offset(x = (-4).dp, y = 4.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = player.username,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F2937)
            )
            if (player.roseCount > 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🌹", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+${player.roseCount}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }
        }

        TextButton(
            onClick = onFollowClick,
            colors = ButtonDefaults.textButtonColors(
                contentColor = if (player.isFollowing) Color(0xFF10B981) else Color(0xFF2563EB)
            )
        ) {
            Text(
                text = if (player.isFollowing) "Following" else "Follow",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "${player.xp} XP",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
        )
    }
}

data class LeaderboardPlayer(
    val id: String,
    val position: Int,
    val username: String,
    val avatarUrl: String,
    val countryFlag: String,
    val roseCount: Int,
    val xp: Int,
    val isFollowing: Boolean
)

data class LeaderboardState(
    val userPosition: Int = 1,
    val leagueName: String = "Determined Seed",
    val leagueSubtitle: String = "The top 7 advance to the next League",
    val timeRemaining: String = "05D 15h",
    val streakCount: Int = 15,
    val starCount: Int = 0,
    val roseCount: Int = 3,
    val lockedLeagues: Int = 4,
    val players: List<LeaderboardPlayer> = emptyList(),
    val isLoading: Boolean = false
)
