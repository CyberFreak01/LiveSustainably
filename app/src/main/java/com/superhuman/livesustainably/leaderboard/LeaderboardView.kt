package com.superhuman.livesustainably.leaderboard

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.superhuman.livesustainably.R
import com.superhuman.livesustainably.navigation.NavBarDestination
import com.superhuman.livesustainably.navigation.UnifiedBottomNavigationBar
import kotlin.math.min

@Composable
fun LeaderboardView(
    viewModel: LeaderboardViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    
    val scrollOffset = remember {
        derivedStateOf {
            if (listState.firstVisibleItemIndex == 0) {
                listState.firstVisibleItemScrollOffset.toFloat()
            } else {
                1000f
            }
        }
    }
    
    val headerHeight = getResponsiveLeaderboardHeaderHeight(screenHeight)
    val collapsedHeight = 72.dp
    val collapseThreshold = (headerHeight - collapsedHeight).value.coerceAtLeast(1f)
    
    val collapseProgress by animateFloatAsState(
        targetValue = (scrollOffset.value / collapseThreshold).coerceIn(0f, 1f),
        label = "collapseProgress"
    )
    
    val isCollapsed = collapseProgress > 0.7f
    val isCompact = screenWidth < 360.dp

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Spacer(modifier = Modifier.height(headerHeight))
                }
                
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = if (isCompact) 12.dp else 20.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Position",
                            fontSize = if (isCompact) 14.sp else 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF9CA3AF)
                        )
                        Text(
                            text = "Points",
                            fontSize = if (isCompact) 14.sp else 16.sp,
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
                            .padding(horizontal = if (isCompact) 12.dp else 20.dp)
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
                            onFollowClick = { viewModel.toggleFollow(player.id) },
                            isCompact = isCompact
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
            
            CollapsibleLeaderboardHeader(
                leagueName = state.leagueName,
                leagueSubtitle = state.leagueSubtitle,
                timeRemaining = state.timeRemaining,
                streakCount = state.streakCount,
                starCount = state.starCount,
                roseCount = state.roseCount,
                lockedLeagues = state.lockedLeagues,
                collapseProgress = collapseProgress,
                isCollapsed = isCollapsed,
                expandedHeight = headerHeight,
                collapsedHeight = collapsedHeight,
                screenWidth = screenWidth
            )
        }
    }
}

@Composable
private fun getResponsiveLeaderboardHeaderHeight(screenHeight: Dp): Dp {
    return when {
        screenHeight < 600.dp -> 250.dp
        screenHeight < 800.dp -> 280.dp
        else -> 320.dp
    }
}

@Composable
fun CollapsibleLeaderboardHeader(
    leagueName: String,
    leagueSubtitle: String,
    timeRemaining: String,
    streakCount: Int,
    starCount: Int,
    roseCount: Int,
    lockedLeagues: Int,
    collapseProgress: Float,
    isCollapsed: Boolean,
    expandedHeight: Dp,
    collapsedHeight: Dp,
    screenWidth: Dp
) {
    val currentHeight by animateDpAsState(
        targetValue = if (isCollapsed) collapsedHeight else expandedHeight,
        label = "headerHeight"
    )
    
    val cornerRadius by animateDpAsState(
        targetValue = if (isCollapsed) 24.dp else 32.dp,
        label = "cornerRadius"
    )
    
    val isCompact = screenWidth < 360.dp
    val badgeSize = if (isCompact) 32.dp else 40.dp
    val badgeFontSize = if (isCompact) 16.sp else 20.sp
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(currentHeight)
            .clip(
                RoundedCornerShape(
                    bottomStart = cornerRadius,
                    bottomEnd = cornerRadius
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
        if (isCollapsed) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(if (isCompact) 36.dp else 44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFE4B5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.seed_icon),
                            contentDescription = "Seed",
                            modifier = Modifier.size(if (isCompact) 22.dp else 28.dp),
                            tint = Color.Unspecified
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = leagueName,
                        fontSize = if (isCompact) 16.sp else 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(if (isCompact) 6.dp else 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompactStatBadge(icon = "🔥", count = streakCount, size = badgeSize, fontSize = badgeFontSize)
                    CompactStatBadge(icon = "⭐", count = starCount, size = badgeSize, fontSize = badgeFontSize)
                    CompactStatBadge(icon = "🌹", count = roseCount, size = badgeSize, fontSize = badgeFontSize)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = if (isCompact) 12.dp else 20.dp)
                    .graphicsLayer {
                        alpha = 1f - collapseProgress
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(if (isCompact) 12.dp else 16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatBadge(icon = "🔥", count = streakCount, isCompact = isCompact)
                    Spacer(modifier = Modifier.width(if (isCompact) 6.dp else 12.dp))
                    StatBadge(icon = "⭐", count = starCount, isCompact = isCompact)
                    Spacer(modifier = Modifier.width(if (isCompact) 6.dp else 12.dp))
                    StatBadge(icon = "🌹", count = roseCount, isCompact = isCompact)
                }
                
                Spacer(modifier = Modifier.height(if (isCompact) 14.dp else 20.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (isCompact) 60.dp else 80.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFE4B5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.seed_icon),
                            contentDescription = "Seed",
                            modifier = Modifier.size(if (isCompact) 36.dp else 50.dp),
                            tint = Color.Unspecified
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(if (isCompact) 8.dp else 12.dp))
                    
                    repeat(minOf(lockedLeagues, if (isCompact) 3 else 4)) {
                        Box(
                            modifier = Modifier
                                .size(if (isCompact) 44.dp else 60.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = Color.White,
                                modifier = Modifier.size(if (isCompact) 22.dp else 30.dp)
                            )
                        }
                        if (it < minOf(lockedLeagues, if (isCompact) 3 else 4) - 1) {
                            Spacer(modifier = Modifier.width(if (isCompact) 6.dp else 12.dp))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(if (isCompact) 12.dp else 16.dp))
                
                Text(
                    text = leagueName,
                    fontSize = if (isCompact) 22.sp else 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = leagueSubtitle,
                    fontSize = if (isCompact) 13.sp else 16.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(if (isCompact) 8.dp else 12.dp))
                
                TextButton(
                    onClick = { },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                ) {
                    Text(
                        text = "How does it work?",
                        fontSize = if (isCompact) 14.sp else 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        modifier = Modifier.size(if (isCompact) 16.dp else 20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = if (isCompact) 12.dp else 16.dp,
                            vertical = if (isCompact) 6.dp else 8.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Timer",
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(if (isCompact) 16.dp else 20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = timeRemaining,
                            fontSize = if (isCompact) 14.sp else 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CompactStatBadge(icon: String, count: Int, size: Dp, fontSize: androidx.compose.ui.unit.TextUnit) {
    Surface(
        modifier = Modifier.height(size),
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = fontSize)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = count.toString(),
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
        }
    }
}

@Composable
fun StatBadge(icon: String, count: Int, isCompact: Boolean = false) {
    val height = if (isCompact) 32.dp else 40.dp
    val minWidth = if (isCompact) 60.dp else 80.dp
    val fontSize = if (isCompact) 16.sp else 20.sp
    val paddingH = if (isCompact) 10.dp else 16.dp
    val paddingV = if (isCompact) 6.dp else 8.dp
    
    Surface(
        modifier = Modifier
            .height(height)
            .widthIn(min = minWidth),
        shape = RoundedCornerShape(28.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = paddingH, vertical = paddingV),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = fontSize)
            Spacer(modifier = Modifier.width(if (isCompact) 4.dp else 8.dp))
            Text(
                text = count.toString(),
                fontSize = fontSize,
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
    onFollowClick: () -> Unit,
    isCompact: Boolean = false
) {
    val positionColor = when (player.position) {
        1 -> Color(0xFFFFD700)
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> Color(0xFF1F2937)
    }
    
    val avatarSize = if (isCompact) 40.dp else 48.dp
    val positionWidth = if (isCompact) 24.dp else 30.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = if (isCompact) 12.dp else 16.dp,
                vertical = if (isCompact) 10.dp else 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.width(positionWidth),
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
                    fontSize = if (isCompact) 16.sp else 20.sp
                )
            } else {
                Text(
                    text = player.position.toString(),
                    fontSize = if (isCompact) 15.sp else 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = positionColor
                )
            }
        }

        Spacer(modifier = Modifier.width(if (isCompact) 10.dp else 16.dp))

        Box(modifier = Modifier.size(avatarSize)) {
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
                        tint = positionColor.copy(alpha = 0.7f),
                        modifier = Modifier.size(if (isCompact) 20.dp else 24.dp)
                    )
                }
            }

            if (player.countryFlag.isNotEmpty()) {
                Text(
                    text = player.countryFlag,
                    fontSize = if (isCompact) 12.sp else 16.sp,
                    modifier = Modifier
                        .size(if (isCompact) 16.dp else 20.dp)
                        .align(Alignment.BottomStart)
                        .offset(x = (-4).dp, y = 4.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(if (isCompact) 8.dp else 12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = player.username,
                fontSize = if (isCompact) 14.sp else 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F2937)
            )
            if (player.roseCount > 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🌹", fontSize = if (isCompact) 12.sp else 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+${player.roseCount}",
                        fontSize = if (isCompact) 12.sp else 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }
        }

        if (!isCompact) {
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
        }

        Text(
            text = "${player.xp} XP",
            fontSize = if (isCompact) 14.sp else 16.sp,
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
