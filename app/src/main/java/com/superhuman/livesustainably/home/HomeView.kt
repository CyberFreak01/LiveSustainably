package com.superhuman.livesustainably.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.superhuman.livesustainably.R
import com.superhuman.livesustainably.navigation.NavBarDestination
import com.superhuman.livesustainably.navigation.UnifiedBottomNavigationBar

@Composable
fun HomeView(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToActivity: (String) -> Unit = {},
    onNavigateToLeaderboard: () -> Unit = {},
    onNavigateToFeed: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            UnifiedBottomNavigationBar(
                currentRoute = NavBarDestination.Home.route,
                onNavigateToHome = { },
                onNavigateToChat = onNavigateToChat,
                onNavigateToLeaderboard = onNavigateToLeaderboard,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section with Blue Background
            HeaderSection(
                streakCount = state.streakCount,
                starCount = state.starCount,
                roseCount = state.roseCount,
                onStartClick = {
                    val firstActivity = state.activities.firstOrNull { !it.isCompleted }
                    firstActivity?.let {
                        when (it.id) {
                            "stories" -> onNavigateToFeed()
                            "mobility" -> onNavigateToMap()
                            else -> onNavigateToActivity(it.id)
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Active Streak Card
            ActiveStreakCard(
                currentDay = state.currentDay,
                streakDays = state.streakDays
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Today's Activities Section
            TodaysActivitiesSection(
                activities = state.activities,
                onActivityClick = onNavigateToActivity,
                onNavigateToFeed = onNavigateToFeed,
                onNavigateToMap = onNavigateToMap
            )
        }
    }
}

@Composable
fun HeaderSection(
    streakCount: Int,
    starCount: Int,
    roseCount: Int,
    onStartClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
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
    )
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Absolute.Right,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatBadge(icon = "🔥", count = streakCount)
                Spacer(modifier = Modifier.width(12.dp))
                StatBadge(icon = "⭐", count = starCount)
                Spacer(modifier = Modifier.width(12.dp))
                StatBadge(icon = "🌹", count = roseCount)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 🔥 Combined Plant Image + Text in Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.Left
            ) {
                Image(
                    painter = painterResource(id = R.drawable.plant_cartoon),
                    contentDescription = "Plant Character",
                    modifier = Modifier.size(70.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Complete an activity and\nstart with the right Streak!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Left,
                    lineHeight = 28.sp
//                    modifier = Modifier.widthIn(max = 220.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Start Button BELOW the Row - navigates to first incomplete activity
            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .height(40.dp)
                    .widthIn(min = 120.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF4444)
                )
            ) {
                Text(
                    text = "START",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
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
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                fontSize = 20.sp
            )
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
fun ActiveStreakCard(
    currentDay: String,
    streakDays: List<StreakDay>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tree icon background
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDCFCE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🌳",
                        fontSize = 28.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Active your Streak!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = "Come back every day to increase your Streak days!",
                        fontSize = 14.sp,
                        color = Color(0xFF9CA3AF),
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Week Days Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                streakDays.forEach { day ->
                    StreakDayItem(
                        dayName = day.name,
                        isActive = day.isActive,
                        isCurrent = day.isCurrent
                    )
                }
            }
        }
    }
}

@Composable
fun StreakDayItem(
    dayName: String,
    isActive: Boolean,
    isCurrent: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = dayName,
            fontSize = 14.sp,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
            color = if (isCurrent) Color(0xFF2563EB) else Color(0xFF6B7280)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCurrent -> Color(0xFFDCFCE7)
                        isActive -> Color(0xFF10B981)
                        else -> Color(0xFFE5E7EB)
                    }
                )
                .then(
                    if (isCurrent) {
                        Modifier.padding(2.dp)
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCurrent) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.Transparent)
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFF2563EB))
                    )
                }
            }
        }
    }
}

@Composable
fun TodaysActivitiesSection(
    activities: List<Activity>,
    onActivityClick: (String) -> Unit,
    onNavigateToFeed: () -> Unit = {},
    onNavigateToMap: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Today's activities",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Grid of Activity Cards
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            activities.chunked(2).forEach { rowActivities ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    rowActivities.forEach { activity ->
                        ActivityCard(
                            activity = activity,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                when (activity.id) {
                                    "stories" -> onNavigateToFeed()
                                    "mobility" -> onNavigateToMap()
                                    else -> onActivityClick(activity.id)
                                }
                            }
                        )
                    }
                    // Fill empty space if odd number
                    if (rowActivities.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityCard(
    activity: Activity,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .height(180.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .clip(RoundedCornerShape(24.dp))
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {

                // ---------- HEADER ROW ----------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Left circular icon
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF6ED)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = activity.iconRes),
                            contentDescription = activity.title,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Title
                    Text(
                        text = activity.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937),
                        modifier = Modifier.weight(1f)
                    )

                    // Notification Dot
                    if (activity.hasNotification) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // ---------- BOTTOM-LEFT IMAGE (2/3 visible) ----------
            Image(
                painter = painterResource(id = activity.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-20).dp, y = 20.dp)  // pushes image partially outside (like screenshot)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            // ---------- XP BADGE OVERLAY ----------
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 60.dp, y = (-10).dp), // overlaps image bottom-right
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF0EA5E9),
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "+${activity.xp} XP",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

    }
}
// Note: Data classes are defined in HomeViewModel.kt to avoid duplication
