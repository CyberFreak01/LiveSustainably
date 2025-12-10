package com.superhuman.livesustainably.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.superhuman.livesustainably.navigation.NavBarDestination
import com.superhuman.livesustainably.navigation.UnifiedBottomNavigationBar
// NOTE: Google Maps SDK imports commented out - using simulated map instead
// import com.google.android.gms.maps.model.CameraPosition
// import com.google.android.gms.maps.model.LatLng
// import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapView(
    viewModel: MapViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToLeaderboard: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp
    // NOTE: Always use simulated map - Google Maps API key requirement removed
    // var useSimulatedMap by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Friends Nearby",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2563EB),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            if (!isLandscape) {
                UnifiedBottomNavigationBar(
                    currentRoute = "map",
                    onNavigateToHome = onNavigateToHome,
                    onNavigateToChat = onNavigateToChat,
                    onNavigateToLeaderboard = onNavigateToLeaderboard,
                    onNavigateToProfile = { }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF2563EB)
                )
            } else {
                // NOTE: Always using simulated map - Google Maps API key not required
                if (isLandscape) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        SimulatedMapContent(
                            state = state,
                            onFriendClick = { viewModel.selectFriend(it) },
                            modifier = Modifier.weight(0.6f)
                        )
                        FriendsListPanel(
                            friends = state.friends,
                            selectedFriend = state.selectedFriend,
                            onFriendClick = { viewModel.selectFriend(it) },
                            modifier = Modifier.weight(0.4f)
                        )
                    }
                } else {
                    SimulatedMapContent(
                        state = state,
                        onFriendClick = { viewModel.selectFriend(it) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            if (!isLandscape) {
                FriendsCountBadge(
                    count = state.friends.count { it.status == "active" },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                )

                state.selectedFriend?.let { friend ->
                    FriendDetailCard(
                        friend = friend,
                        onDismiss = { viewModel.dismissFriendCard() },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

// NOTE: GoogleMapContent function commented out - using simulated map instead
// Google Maps API key requirement removed
/*
@Composable
fun GoogleMapContent(
    state: MapState,
    onFriendClick: (Friend) -> Unit,
    onMapError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(state.userLocation.latitude, state.userLocation.longitude),
            14f
        )
    }

    var mapLoadError by remember { mutableStateOf(false) }

    LaunchedEffect(mapLoadError) {
        if (mapLoadError) {
            onMapError()
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = false),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            myLocationButtonEnabled = false
        ),
        onMapLoaded = { },
    ) {
        Marker(
            state = MarkerState(
                position = LatLng(
                    state.userLocation.latitude,
                    state.userLocation.longitude
                )
            ),
            title = "You",
            snippet = "Your current location"
        )

        state.friends.forEach { friend ->
            val markerColor = if (friend.status == "active") {
                com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN
            } else {
                com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_ORANGE
            }

            Marker(
                state = MarkerState(
                    position = LatLng(friend.latitude, friend.longitude)
                ),
                title = friend.name,
                snippet = friend.activity,
                icon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(markerColor),
                onClick = {
                    onFriendClick(friend)
                    true
                }
            )
        }
    }
}
*/

@Composable
fun SimulatedMapContent(
    state: MapState,
    onFriendClick: (Friend) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE8F4EA),
                        Color(0xFFD4E8D7),
                        Color(0xFFC5DCC8)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            drawLine(
                color = Color(0xFFB8D4BC),
                start = Offset(0f, height * 0.3f),
                end = Offset(width, height * 0.35f),
                strokeWidth = 3f
            )
            drawLine(
                color = Color(0xFFB8D4BC),
                start = Offset(0f, height * 0.6f),
                end = Offset(width, height * 0.55f),
                strokeWidth = 3f
            )
            drawLine(
                color = Color(0xFFB8D4BC),
                start = Offset(width * 0.3f, 0f),
                end = Offset(width * 0.35f, height),
                strokeWidth = 3f
            )
            drawLine(
                color = Color(0xFFB8D4BC),
                start = Offset(width * 0.7f, 0f),
                end = Offset(width * 0.65f, height),
                strokeWidth = 3f
            )

            val roadColor = Color(0xFFF5F5F5)
            val roadWidth = 20f

            drawLine(
                color = roadColor,
                start = Offset(0f, height * 0.4f),
                end = Offset(width, height * 0.4f),
                strokeWidth = roadWidth
            )
            drawLine(
                color = roadColor,
                start = Offset(0f, height * 0.7f),
                end = Offset(width, height * 0.7f),
                strokeWidth = roadWidth
            )
            drawLine(
                color = roadColor,
                start = Offset(width * 0.25f, 0f),
                end = Offset(width * 0.25f, height),
                strokeWidth = roadWidth
            )
            drawLine(
                color = roadColor,
                start = Offset(width * 0.6f, 0f),
                end = Offset(width * 0.6f, height),
                strokeWidth = roadWidth
            )

            val waterColor = Color(0xFF87CEEB).copy(alpha = 0.5f)
            val path = Path().apply {
                moveTo(width * 0.8f, 0f)
                cubicTo(
                    width * 0.85f, height * 0.3f,
                    width * 0.75f, height * 0.5f,
                    width * 0.9f, height
                )
                lineTo(width, height)
                lineTo(width, 0f)
                close()
            }
            drawPath(path, waterColor)

            drawCircle(
                color = Color(0xFF98D8AA),
                radius = 40f,
                center = Offset(width * 0.15f, height * 0.2f)
            )
            drawCircle(
                color = Color(0xFF98D8AA),
                radius = 30f,
                center = Offset(width * 0.45f, height * 0.15f)
            )
            drawCircle(
                color = Color(0xFF98D8AA),
                radius = 35f,
                center = Offset(width * 0.1f, height * 0.85f)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-20).dp)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2563EB))
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2563EB).copy(alpha = 0.2f))
                    .align(Alignment.Center)
            )
        }

        state.friends.forEachIndexed { index, friend ->
            val xOffset = when (index % 4) {
                0 -> 0.2f
                1 -> 0.4f
                2 -> 0.7f
                else -> 0.3f
            }
            val yOffset = when (index % 3) {
                0 -> 0.25f
                1 -> 0.55f
                else -> 0.8f
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                FriendMarker(
                    friend = friend,
                    onClick = { onFriendClick(friend) },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(
                            x = (xOffset * 300 + index * 30).dp,
                            y = (yOffset * 400 + index * 20).dp
                        )
                )
            }
        }

        Card(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Simulated Map (No API Key)",
                    fontSize = 14.sp,
                    color = Color(0xFF1F2937)
                )
            }
        }
    }
}

@Composable
fun FriendMarker(
    friend: Friend,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val markerColor = if (friend.status == "active") Color(0xFF10B981) else Color(0xFFF59E0B)

    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(markerColor)
                .padding(3.dp),
            contentAlignment = Alignment.Center
        ) {
            if (friend.avatarUrl.isNotEmpty()) {
                AsyncImage(
                    model = friend.avatarUrl,
                    contentDescription = friend.name,
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
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = markerColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        Canvas(modifier = Modifier.size(10.dp, 8.dp)) {
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width / 2, size.height)
                close()
            }
            drawPath(path, markerColor)
        }
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Text(
                text = friend.name.split(" ").first(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1F2937),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
fun FriendsListPanel(
    friends: List<Friend>,
    selectedFriend: Friend?,
    onFriendClick: (Friend) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxHeight(),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Active Friends",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
            Spacer(modifier = Modifier.height(16.dp))

            friends.forEach { friend ->
                FriendListItem(
                    friend = friend,
                    isSelected = selectedFriend?.id == friend.id,
                    onClick = { onFriendClick(friend) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendListItem(
    friend: Friend,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF9FAFB)
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF2563EB))
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(40.dp)) {
                AsyncImage(
                    model = friend.avatarUrl,
                    contentDescription = friend.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(
                            if (friend.status == "active") Color(0xFF10B981)
                            else Color(0xFFF59E0B)
                        )
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = friend.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                )
                Text(
                    text = friend.activity,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}

@Composable
fun FriendsCountBadge(
    count: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF10B981),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$count Active",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun FriendDetailCard(
    friend: Friend,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(56.dp)) {
                        AsyncImage(
                            model = friend.avatarUrl,
                            contentDescription = friend.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(
                                    if (friend.status == "active") Color(0xFF10B981)
                                    else Color(0xFFF59E0B)
                                )
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = friend.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                        Text(
                            text = friend.username,
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF9CA3AF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoChip(icon = Icons.Default.LocationOn, text = friend.activity)
                InfoChip(icon = Icons.Default.DateRange, text = friend.lastActive)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Message")
                }
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Profile")
                }
            }
        }
    }
}

@Composable
fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF3F4F6)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF6B7280),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                color = Color(0xFF6B7280)
            )
        }
    }
}

// NOTE: MapBottomNavigationBar replaced with UnifiedBottomNavigationBar
// See MapView composable for the unified navigation bar implementation
