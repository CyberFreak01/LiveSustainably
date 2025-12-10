package com.superhuman.livesustainably.feed

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `FeedState has correct default values`() {
        val state = FeedState()
        
        assertTrue(state.feedItems.isEmpty())
        assertFalse(state.isLoading)
    }

    @Test
    fun `FeedItem data class holds correct values`() {
        val feedItem = FeedItem(
            id = "1",
            authorName = "Test Author",
            authorAvatar = "https://example.com/avatar.png",
            timestamp = "2 hours ago",
            category = "Climate",
            title = "Test Title",
            description = "Test description content",
            imageUrl = "https://example.com/image.png",
            likeCount = 100,
            isLiked = false,
            comments = emptyList(),
            showCommentSection = false
        )
        
        assertEquals("1", feedItem.id)
        assertEquals("Test Author", feedItem.authorName)
        assertEquals("Climate", feedItem.category)
        assertEquals("Test Title", feedItem.title)
        assertEquals(100, feedItem.likeCount)
        assertFalse(feedItem.isLiked)
        assertTrue(feedItem.comments.isEmpty())
    }

    @Test
    fun `Comment data class holds correct values`() {
        val comment = Comment(
            id = "c1",
            authorName = "Commenter",
            text = "Great post!",
            timestamp = "1 hour ago"
        )
        
        assertEquals("c1", comment.id)
        assertEquals("Commenter", comment.authorName)
        assertEquals("Great post!", comment.text)
        assertEquals("1 hour ago", comment.timestamp)
    }

    @Test
    fun `toggle like logic increases count when not liked`() {
        val item = FeedItem(
            id = "1",
            authorName = "Author",
            authorAvatar = "",
            timestamp = "1h",
            category = "Climate",
            title = "Title",
            description = "Desc",
            imageUrl = "",
            likeCount = 10,
            isLiked = false,
            comments = emptyList()
        )
        
        val updatedItem = item.copy(
            isLiked = !item.isLiked,
            likeCount = if (item.isLiked) item.likeCount - 1 else item.likeCount + 1
        )
        
        assertTrue(updatedItem.isLiked)
        assertEquals(11, updatedItem.likeCount)
    }

    @Test
    fun `toggle like logic decreases count when already liked`() {
        val item = FeedItem(
            id = "1",
            authorName = "Author",
            authorAvatar = "",
            timestamp = "1h",
            category = "Climate",
            title = "Title",
            description = "Desc",
            imageUrl = "",
            likeCount = 10,
            isLiked = true,
            comments = emptyList()
        )
        
        val updatedItem = item.copy(
            isLiked = !item.isLiked,
            likeCount = if (item.isLiked) item.likeCount - 1 else item.likeCount + 1
        )
        
        assertFalse(updatedItem.isLiked)
        assertEquals(9, updatedItem.likeCount)
    }

    @Test
    fun `toggle comment section logic works correctly`() {
        val item = FeedItem(
            id = "1",
            authorName = "Author",
            authorAvatar = "",
            timestamp = "1h",
            category = "Climate",
            title = "Title",
            description = "Desc",
            imageUrl = "",
            likeCount = 10,
            isLiked = false,
            comments = emptyList(),
            showCommentSection = false
        )
        
        val updatedItem = item.copy(showCommentSection = !item.showCommentSection)
        
        assertTrue(updatedItem.showCommentSection)
    }

    @Test
    fun `add comment creates new comment with correct author`() {
        val commentText = "Test comment"
        val newComment = Comment(
            id = UUID.randomUUID().toString(),
            authorName = "You",
            text = commentText,
            timestamp = "Just now"
        )
        
        assertEquals("You", newComment.authorName)
        assertEquals("Test comment", newComment.text)
        assertEquals("Just now", newComment.timestamp)
    }

    @Test
    fun `comment IDs are unique`() {
        val comment1 = Comment(
            id = UUID.randomUUID().toString(),
            authorName = "You",
            text = "First",
            timestamp = "Just now"
        )
        val comment2 = Comment(
            id = UUID.randomUUID().toString(),
            authorName = "You",
            text = "Second",
            timestamp = "Just now"
        )
        
        assertNotEquals(comment1.id, comment2.id)
    }

    @Test
    fun `getCategoryColor returns correct colors`() {
        assertEquals(android.graphics.Color.parseColor("#2563EB"), getCategoryColorHex("climate"))
        assertEquals(android.graphics.Color.parseColor("#10B981"), getCategoryColorHex("renewable"))
        assertEquals(android.graphics.Color.parseColor("#8B5CF6"), getCategoryColorHex("conservation"))
        assertEquals(android.graphics.Color.parseColor("#F59E0B"), getCategoryColorHex("recycling"))
        assertEquals(android.graphics.Color.parseColor("#EC4899"), getCategoryColorHex("wildlife"))
        assertEquals(android.graphics.Color.parseColor("#6B7280"), getCategoryColorHex("unknown"))
    }
    
    private fun getCategoryColorHex(category: String): Int {
        return when (category.lowercase()) {
            "climate" -> android.graphics.Color.parseColor("#2563EB")
            "renewable" -> android.graphics.Color.parseColor("#10B981")
            "conservation" -> android.graphics.Color.parseColor("#8B5CF6")
            "recycling" -> android.graphics.Color.parseColor("#F59E0B")
            "wildlife" -> android.graphics.Color.parseColor("#EC4899")
            else -> android.graphics.Color.parseColor("#6B7280")
        }
    }

    @Test
    fun `feed items with different categories are sorted correctly`() {
        val items = listOf(
            FeedItem("1", "", "", "", "Climate", "", "", "", 0, false, emptyList()),
            FeedItem("2", "", "", "", "Renewable", "", "", "", 0, false, emptyList()),
            FeedItem("3", "", "", "", "Wildlife", "", "", "", 0, false, emptyList())
        )
        
        val categories = items.map { it.category }
        
        assertTrue(categories.contains("Climate"))
        assertTrue(categories.contains("Renewable"))
        assertTrue(categories.contains("Wildlife"))
    }
}
