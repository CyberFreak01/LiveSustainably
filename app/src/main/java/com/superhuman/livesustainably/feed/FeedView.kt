package com.superhuman.livesustainably.feed

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.superhuman.livesustainably.R
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedView(
    viewModel: FeedViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
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
    
    val expandedHeight = 120.dp
    val collapsedHeight = 64.dp
    val collapseThreshold = (expandedHeight - collapsedHeight).value.coerceAtLeast(1f)
    
    val collapseProgress by animateFloatAsState(
        targetValue = (scrollOffset.value / collapseThreshold).coerceIn(0f, 1f),
        label = "collapseProgress"
    )
    
    val isCollapsed = collapseProgress > 0.7f
    val isCompact = screenWidth < 360.dp

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF9FAFB)),
                contentPadding = PaddingValues(
                    top = expandedHeight + 16.dp,
                    bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.feedItems) { feedItem ->
                    FeedCard(
                        feedItem = feedItem,
                        onLikeClick = { viewModel.toggleLike(feedItem.id) },
                        onCommentClick = { viewModel.toggleCommentSection(feedItem.id) },
                        onCommentSubmit = { comment ->
                            viewModel.addComment(feedItem.id, comment)
                        },
                        isCompact = isCompact
                    )
                }
            }
            
            StoriesAppBar(
                onNavigateBack = onNavigateBack,
                collapseProgress = collapseProgress,
                isCollapsed = isCollapsed,
                expandedHeight = expandedHeight,
                collapsedHeight = collapsedHeight,
                isCompact = isCompact
            )
        }
    }
}

@Composable
fun StoriesAppBar(
    onNavigateBack: () -> Unit,
    collapseProgress: Float,
    isCollapsed: Boolean,
    expandedHeight: Dp,
    collapsedHeight: Dp,
    isCompact: Boolean
) {
    val currentHeight by animateDpAsState(
        targetValue = if (isCollapsed) collapsedHeight else expandedHeight,
        label = "headerHeight"
    )
    
    val cornerRadius by animateDpAsState(
        targetValue = if (isCollapsed) 20.dp else 28.dp,
        label = "cornerRadius"
    )
    
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
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                
                Text(
                    text = "Stories",
                    fontSize = if (isCompact) 18.sp else 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .graphicsLayer {
                        alpha = 1f - collapseProgress
                    }
            ) {
                Spacer(modifier = Modifier.height(if (isCompact) 12.dp else 16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "Stories",
                        fontSize = if (isCompact) 24.sp else 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Read and share sustainability stories",
                    fontSize = if (isCompact) 14.sp else 16.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(start = 48.dp)
                )
            }
        }
    }
}

@Composable
fun FeedCard(
    feedItem: FeedItem,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onCommentSubmit: (String) -> Unit,
    isCompact: Boolean = false
) {
    var commentText by remember { mutableStateOf("") }
    
    val horizontalPadding = if (isCompact) 12.dp else 16.dp
    val contentPadding = if (isCompact) 12.dp else 16.dp
    val avatarSize = if (isCompact) 36.dp else 40.dp
    val titleSize = if (isCompact) 16.sp else 18.sp
    val imageHeight = if (isCompact) 180.dp else 220.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (feedItem.authorAvatar.isNotEmpty()) {
                    AsyncImage(
                        model = feedItem.authorAvatar,
                        contentDescription = "Author Avatar",
                        modifier = Modifier
                            .size(avatarSize)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(avatarSize)
                            .clip(CircleShape)
                            .background(Color(0xFF2563EB)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = feedItem.authorName.first().uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isCompact) 16.sp else 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(if (isCompact) 10.dp else 12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = feedItem.authorName,
                        fontSize = if (isCompact) 14.sp else 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = feedItem.timestamp,
                        fontSize = if (isCompact) 12.sp else 13.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = getCategoryColor(feedItem.category)
                ) {
                    Text(
                        text = feedItem.category,
                        modifier = Modifier.padding(
                            horizontal = if (isCompact) 10.dp else 12.dp,
                            vertical = if (isCompact) 4.dp else 6.dp
                        ),
                        fontSize = if (isCompact) 11.sp else 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            Text(
                text = feedItem.title,
                modifier = Modifier.padding(horizontal = contentPadding),
                fontSize = titleSize,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = feedItem.description,
                modifier = Modifier.padding(horizontal = contentPadding),
                fontSize = if (isCompact) 14.sp else 15.sp,
                color = Color(0xFF4B5563),
                lineHeight = if (isCompact) 20.sp else 22.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (feedItem.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = feedItem.imageUrl,
                    contentDescription = feedItem.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight)
                        .padding(horizontal = contentPadding)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight)
                        .padding(horizontal = contentPadding)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE5E7EB)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.image),
                        contentDescription = null,
                        modifier = Modifier.size(if (isCompact) 40.dp else 48.dp),
                        tint = Color(0xFF9CA3AF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = contentPadding),
                color = Color(0xFFE5E7EB)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(
                    onClick = onLikeClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (feedItem.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (feedItem.isLiked) Color(0xFFEF4444) else Color(0xFF6B7280),
                        modifier = Modifier.size(if (isCompact) 18.dp else 20.dp)
                    )
                    Spacer(modifier = Modifier.width(if (isCompact) 4.dp else 8.dp))
                    Text(
                        text = "${feedItem.likeCount} Likes",
                        fontSize = if (isCompact) 13.sp else 15.sp,
                        color = if (feedItem.isLiked) Color(0xFFEF4444) else Color(0xFF6B7280),
                        fontWeight = if (feedItem.isLiked) FontWeight.SemiBold else FontWeight.Normal
                    )
                }

                TextButton(
                    onClick = onCommentClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.chat_bubble),
                        contentDescription = "Comment",
                        tint = if (feedItem.showCommentSection) Color(0xFF2563EB) else Color(0xFF6B7280),
                        modifier = Modifier.size(if (isCompact) 18.dp else 20.dp)
                    )
                    Spacer(modifier = Modifier.width(if (isCompact) 4.dp else 8.dp))
                    Text(
                        text = "${feedItem.comments.size} Comments",
                        fontSize = if (isCompact) 13.sp else 15.sp,
                        color = if (feedItem.showCommentSection) Color(0xFF2563EB) else Color(0xFF6B7280),
                        fontWeight = if (feedItem.showCommentSection) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }

            if (feedItem.showCommentSection) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = contentPadding),
                    color = Color(0xFFE5E7EB)
                )

                Column(modifier = Modifier.padding(contentPadding)) {
                    feedItem.comments.forEach { comment ->
                        CommentItem(comment = comment, isCompact = isCompact)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Add a comment...") },
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2563EB),
                                unfocusedBorderColor = Color(0xFFE5E7EB)
                            ),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (commentText.isNotBlank()) {
                                    onCommentSubmit(commentText)
                                    commentText = ""
                                }
                            },
                            enabled = commentText.isNotBlank()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = if (commentText.isNotBlank()) Color(0xFF2563EB) else Color(0xFF9CA3AF)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment, isCompact: Boolean = false) {
    val avatarSize = if (isCompact) 28.dp else 32.dp
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(avatarSize)
                .clip(CircleShape)
                .background(Color(0xFF10B981)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = comment.authorName.first().uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = if (isCompact) 12.sp else 14.sp
            )
        }

        Spacer(modifier = Modifier.width(if (isCompact) 10.dp else 12.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.authorName,
                    fontSize = if (isCompact) 13.sp else 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = comment.timestamp,
                    fontSize = if (isCompact) 11.sp else 12.sp,
                    color = Color(0xFF9CA3AF)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment.text,
                fontSize = if (isCompact) 13.sp else 14.sp,
                color = Color(0xFF4B5563),
                lineHeight = if (isCompact) 18.sp else 20.sp
            )
        }
    }
}

fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "climate" -> Color(0xFF2563EB)
        "renewable" -> Color(0xFF10B981)
        "conservation" -> Color(0xFF8B5CF6)
        "recycling" -> Color(0xFFF59E0B)
        "wildlife" -> Color(0xFFEC4899)
        else -> Color(0xFF6B7280)
    }
}

data class Comment(
    val id: String,
    val authorName: String,
    val text: String,
    val timestamp: String
)

data class FeedItem(
    val id: String,
    val authorName: String,
    val authorAvatar: String,
    val timestamp: String,
    val category: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val likeCount: Int,
    val isLiked: Boolean,
    val comments: List<Comment>,
    val showCommentSection: Boolean = false
)

data class FeedState(
    val feedItems: List<FeedItem> = emptyList(),
    val isLoading: Boolean = false
)
