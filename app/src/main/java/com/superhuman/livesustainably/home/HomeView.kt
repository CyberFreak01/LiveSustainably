package com.superhuman.livesustainably.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.superhuman.livesustainably.R
import com.superhuman.livesustainably.navigation.NavBarDestination
import com.superhuman.livesustainably.navigation.UnifiedBottomNavigationBar
import kotlin.math.min

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
    
    val headerHeight = getResponsiveHeaderHeight(screenHeight)
    val collapsedHeight = 72.dp
    val collapseThreshold = (headerHeight - collapsedHeight).value.coerceAtLeast(1f)
    
    val collapseProgress by animateFloatAsState(
        targetValue = (scrollOffset.value / collapseThreshold).coerceIn(0f, 1f),
        label = "collapseProgress"
    )
    
    val isCollapsed = collapseProgress > 0.7f

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
                    Spacer(modifier = Modifier.height(24.dp))
                    ActiveStreakCard(
                        currentDay = state.currentDay,
                        streakDays = state.streakDays,
                        screenWidth = screenWidth
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    TodaysActivitiesSection(
                        activities = state.activities,
                        onActivityClick = onNavigateToActivity,
                        onNavigateToFeed = onNavigateToFeed,
                        onNavigateToMap = onNavigateToMap,
                        screenWidth = screenWidth
                    )
                }
            }
            
            CollapsibleHeaderSection(
                streakCount = state.streakCount,
                starCount = state.starCount,
                roseCount = state.roseCount,
                onStartClick = {
                    if (state.activities.isNotEmpty()) {
                        val firstActivity = state.activities.firstOrNull { !it.isCompleted }
                        firstActivity?.let {
                            when (it.id) {
                                "stories" -> onNavigateToFeed()
                                "mobility" -> onNavigateToMap()
                                else -> onNavigateToActivity(it.id)
                            }
                        }
                    }
                },
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
private fun getResponsiveHeaderHeight(screenHeight: Dp): Dp {
    return when {
        screenHeight < 600.dp -> 200.dp
        screenHeight < 800.dp -> 230.dp
        else -> 260.dp
    }
}

@Composable
fun CollapsibleHeaderSection(
    streakCount: Int,
    starCount: Int,
    roseCount: Int,
    onStartClick: () -> Unit = {},
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
                Image(
                    painter = painterResource(id = R.drawable.plant_cartoon),
                    contentDescription = "Plant Character",
                    modifier = Modifier.size(if (isCompact) 40.dp else 50.dp)
                )
                
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
                
                Spacer(modifier = Modifier.height(if (isCompact) 16.dp else 24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.plant_cartoon),
                        contentDescription = "Plant Character",
                        modifier = Modifier.size(if (isCompact) 50.dp else 70.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(if (isCompact) 10.dp else 16.dp))
                    
                    Text(
                        text = "Complete an activity and\nstart with the right Streak!",
                        fontSize = if (isCompact) 16.sp else 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Left,
                        lineHeight = if (isCompact) 22.sp else 28.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(if (isCompact) 16.dp else 24.dp))
                
                Button(
                    onClick = onStartClick,
                    modifier = Modifier
                        .height(if (isCompact) 36.dp else 40.dp)
                        .widthIn(min = if (isCompact) 100.dp else 120.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444)
                    )
                ) {
                    Text(
                        text = "START",
                        fontSize = if (isCompact) 14.sp else 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
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
fun ActiveStreakCard(
    currentDay: String,
    streakDays: List<StreakDay>,
    screenWidth: Dp = 400.dp
) {
    val isCompact = screenWidth < 360.dp
    val padding = if (isCompact) 16.dp else 24.dp
    val iconSize = if (isCompact) 40.dp else 56.dp
    val titleSize = if (isCompact) 18.sp else 22.sp
    val dayCircleSize = if (isCompact) 32.dp else 40.dp
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = if (isCompact) 12.dp else 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .clip(CircleShape)
                        .background(Color(0xFFDCFCE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🌳", fontSize = if (isCompact) 22.sp else 28.sp)
                }
                
                Spacer(modifier = Modifier.width(if (isCompact) 12.dp else 16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Active your Streak!",
                        fontSize = titleSize,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = "Come back every day to increase your Streak days!",
                        fontSize = if (isCompact) 12.sp else 14.sp,
                        color = Color(0xFF9CA3AF),
                        lineHeight = if (isCompact) 16.sp else 20.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(if (isCompact) 16.dp else 24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                streakDays.forEach { day ->
                    StreakDayItem(
                        dayName = day.name,
                        isActive = day.isActive,
                        isCurrent = day.isCurrent,
                        circleSize = dayCircleSize,
                        isCompact = isCompact
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
    isCurrent: Boolean,
    circleSize: Dp = 40.dp,
    isCompact: Boolean = false
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = dayName,
            fontSize = if (isCompact) 12.sp else 14.sp,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
            color = if (isCurrent) Color(0xFF2563EB) else Color(0xFF6B7280)
        )
        Spacer(modifier = Modifier.height(if (isCompact) 6.dp else 8.dp))
        Box(
            modifier = Modifier
                .size(circleSize)
                .clip(CircleShape)
                .background(
                    when {
                        isCurrent -> Color(0xFFDCFCE7)
                        isActive -> Color(0xFF10B981)
                        else -> Color(0xFFE5E7EB)
                    }
                )
                .then(
                    if (isCurrent) Modifier.padding(2.dp) else Modifier
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
    onNavigateToMap: () -> Unit = {},
    screenWidth: Dp = 400.dp
) {
    val isCompact = screenWidth < 360.dp
    val horizontalPadding = if (isCompact) 12.dp else 20.dp
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
    ) {
        Text(
            text = "Today's activities",
            fontSize = if (isCompact) 22.sp else 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
        )
        
        Spacer(modifier = Modifier.height(if (isCompact) 16.dp else 20.dp))
        
        Column(verticalArrangement = Arrangement.spacedBy(if (isCompact) 12.dp else 16.dp)) {
            activities.chunked(2).forEach { rowActivities ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(if (isCompact) 10.dp else 16.dp)
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
                            },
                            isCompact = isCompact
                        )
                    }
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
    onClick: () -> Unit,
    isCompact: Boolean = false
) {
    val cardHeight = if (isCompact) 150.dp else 180.dp
    val iconBoxSize = if (isCompact) 32.dp else 38.dp
    val iconSize = if (isCompact) 18.dp else 22.dp
    val titleSize = if (isCompact) 15.sp else 18.sp
    val imageSize = if (isCompact) 100.dp else 120.dp
    
    Card(
        onClick = onClick,
        modifier = modifier.height(cardHeight),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    .padding(horizontal = if (isCompact) 12.dp else 16.dp, vertical = if (isCompact) 10.dp else 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(iconBoxSize)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF6ED)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = activity.iconRes),
                            contentDescription = activity.title,
                            modifier = Modifier.size(iconSize)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(if (isCompact) 8.dp else 10.dp))
                    
                    Text(
                        text = activity.title,
                        fontSize = titleSize,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937),
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (activity.hasNotification) {
                        Box(
                            modifier = Modifier
                                .size(if (isCompact) 10.dp else 14.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(if (isCompact) 8.dp else 10.dp))
            }
            
            Image(
                painter = painterResource(id = activity.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(imageSize)
                    .align(Alignment.BottomStart)
                    .offset(x = (-20).dp, y = 20.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = if (isCompact) 50.dp else 60.dp, y = (-10).dp),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF0EA5E9),
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = if (isCompact) 10.dp else 14.dp,
                        vertical = if (isCompact) 6.dp else 8.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "+${activity.xp} XP",
                        fontSize = if (isCompact) 12.sp else 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(if (isCompact) 4.dp else 6.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(if (isCompact) 12.dp else 16.dp)
                    )
                }
            }
        }
    }
}
