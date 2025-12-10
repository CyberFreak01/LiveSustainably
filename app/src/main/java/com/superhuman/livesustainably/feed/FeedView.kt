package com.superhuman.livesustainably.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.superhuman.livesustainably.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedView(
    viewModel: FeedViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Stories",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1F2937),
                    navigationIconContentColor = Color(0xFF1F2937)
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF9FAFB)),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.feedItems) { feedItem ->
                FeedCard(
                    feedItem = feedItem,
                    onLikeClick = { viewModel.toggleLike(feedItem.id) },
                    onCommentClick = { viewModel.toggleCommentSection(feedItem.id) },
                    onCommentSubmit = { comment ->
                        viewModel.addComment(feedItem.id, comment)
                    }
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
    onCommentSubmit: (String) -> Unit
) {
    var commentText by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header: Author Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Author Avatar
                if (feedItem.authorAvatar.isNotEmpty()) {
                    AsyncImage(
                        model = feedItem.authorAvatar,
                        contentDescription = "Author Avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2563EB)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = feedItem.authorName.first().uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = feedItem.authorName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = feedItem.timestamp,
                        fontSize = 13.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }

                // Category Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = getCategoryColor(feedItem.category)
                ) {
                    Text(
                        text = feedItem.category,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            // Title
            Text(
                text = feedItem.title,
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = feedItem.description,
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 15.sp,
                color = Color(0xFF4B5563),
                lineHeight = 22.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Image
            if (feedItem.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = feedItem.imageUrl,
                    contentDescription = feedItem.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE5E7EB)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.image),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF9CA3AF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color(0xFFE5E7EB)
            )

            // Action Buttons: Like, Comment
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Like Button
                TextButton(
                    onClick = onLikeClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (feedItem.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (feedItem.isLiked) Color(0xFFEF4444) else Color(0xFF6B7280),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${feedItem.likeCount} Likes",
                        fontSize = 15.sp,
                        color = if (feedItem.isLiked) Color(0xFFEF4444) else Color(0xFF6B7280),
                        fontWeight = if (feedItem.isLiked) FontWeight.SemiBold else FontWeight.Normal
                    )
                }

                // Comment Button
                TextButton(
                    onClick = onCommentClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.chat_bubble),
                        contentDescription = "Comment",
                        tint = if (feedItem.showCommentSection) Color(0xFF2563EB) else Color(0xFF6B7280),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${feedItem.comments.size} Comments",
                        fontSize = 15.sp,
                        color = if (feedItem.showCommentSection) Color(0xFF2563EB) else Color(0xFF6B7280),
                        fontWeight = if (feedItem.showCommentSection) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }

            // Comments Section
            if (feedItem.showCommentSection) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFE5E7EB)
                )

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Display existing comments
                    feedItem.comments.forEach { comment ->
                        CommentItem(comment = comment)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Add Comment Input
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
fun CommentItem(comment: Comment) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Commenter Avatar
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFF10B981)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = comment.authorName.first().uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comment.authorName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = comment.timestamp,
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment.text,
                fontSize = 14.sp,
                color = Color(0xFF4B5563),
                lineHeight = 20.sp
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

// ============================================
// Data Classes
// ============================================

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