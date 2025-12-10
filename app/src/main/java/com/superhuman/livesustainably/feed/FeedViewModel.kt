package com.superhuman.livesustainably.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(FeedState())
    val state: StateFlow<FeedState> = _state.asStateFlow()

    init {
        loadFeedData()
    }

    private fun loadFeedData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val mockFeedItems = getMockFeedData()

            _state.update {
                it.copy(
                    feedItems = mockFeedItems,
                    isLoading = false
                )
            }
        }
    }

    fun toggleLike(feedItemId: String) {
        _state.update { currentState ->
            currentState.copy(
                feedItems = currentState.feedItems.map { item ->
                    if (item.id == feedItemId) {
                        item.copy(
                            isLiked = !item.isLiked,
                            likeCount = if (item.isLiked) item.likeCount - 1 else item.likeCount + 1
                        )
                    } else {
                        item
                    }
                }
            )
        }
    }

    fun toggleCommentSection(feedItemId: String) {
        _state.update { currentState ->
            currentState.copy(
                feedItems = currentState.feedItems.map { item ->
                    if (item.id == feedItemId) {
                        item.copy(showCommentSection = !item.showCommentSection)
                    } else {
                        item
                    }
                }
            )
        }
    }

    fun addComment(feedItemId: String, commentText: String) {
        _state.update { currentState ->
            currentState.copy(
                feedItems = currentState.feedItems.map { item ->
                    if (item.id == feedItemId) {
                        val newComment = Comment(
                            id = UUID.randomUUID().toString(),
                            authorName = "You",
                            text = commentText,
                            timestamp = "Just now"
                        )
                        item.copy(
                            comments = item.comments + newComment
                        )
                    } else {
                        item
                    }
                }
            )
        }
    }

    private fun getMockFeedData(): List<FeedItem> {
        return listOf(
            FeedItem(
                id = "1",
                authorName = "Dr. Jane Goodall",
                authorAvatar = "",
                timestamp = "2 hours ago",
                category = "Wildlife",
                title = "Protecting Our Forests: A Critical Mission",
                description = "Forests are the lungs of our planet. Every tree we save contributes to a healthier Earth. Join us in our mission to protect and restore forest ecosystems worldwide. Together, we can make a difference for future generations.",
                imageUrl = "https://media.istockphoto.com/id/1138249879/photo/a-man-hugging-a-tree.jpg?s=612x612&w=0&k=20&c=iNVH80yUxIhJwu0TP8bwqfTcLb7ZQwGhyOsZrmzOiq0=",
                likeCount = 234,
                isLiked = false,
                comments = listOf(
                    Comment(
                        id = "c1",
                        authorName = "Sarah Miller",
                        text = "This is so important! I've started volunteering with local reforestation projects.",
                        timestamp = "1 hour ago"
                    ),
                    Comment(
                        id = "c2",
                        authorName = "Mike Chen",
                        text = "Thank you for bringing awareness to this critical issue.",
                        timestamp = "30 min ago"
                    )
                )
            ),
            FeedItem(
                id = "2",
                authorName = "Earth Alliance",
                authorAvatar = "",
                timestamp = "5 hours ago",
                category = "Renewable",
                title = "Solar Energy Revolution: The Future is Bright",
                description = "Solar power is now cheaper than ever! More households are switching to renewable energy sources. Discover how you can make the transition to clean energy and reduce your carbon footprint significantly.",
                imageUrl = "https://i0.wp.com/solarquarter.com/wp-content/uploads/2019/10/electricity-1330214_1920.jpg?fit=1920",
                likeCount = 567,
                isLiked = false,
                comments = listOf()
            ),
            FeedItem(
                id = "3",
                authorName = "Ocean Warriors",
                authorAvatar = "",
                timestamp = "1 day ago",
                category = "Conservation",
                title = "Plastic-Free Oceans: A Dream Within Reach",
                description = "Our oceans are drowning in plastic. But there's hope! Communities worldwide are implementing plastic bans and organizing beach cleanups. Learn how you can contribute to cleaner oceans.",
                imageUrl = "https://imageio.forbes.com/specials-images/imageserve/971513936/0x0.jpg?format=jpg&height=900&width=1600&fit=bounds",
                likeCount = 892,
                isLiked = false,
                comments = listOf(
                    Comment(
                        id = "c3",
                        authorName = "Emma Watson",
                        text = "I've stopped using single-use plastics completely. Small changes make a big difference!",
                        timestamp = "12 hours ago"
                    )
                )
            ),
            FeedItem(
                id = "4",
                authorName = "Climate Action Now",
                authorAvatar = "",
                timestamp = "2 days ago",
                category = "Climate",
                title = "Electric Vehicles: Driving Towards a Greener Future",
                description = "The EV revolution is here! Electric vehicles are becoming more accessible and affordable. Join millions of drivers making the switch to emission-free transportation.",
                imageUrl = "https://franchiseindia.s3.ap-south-1.amazonaws.com/opp/article/english/images/2013658221.jpg",
                likeCount = 1234,
                isLiked = false,
                comments = listOf()
            ),
            FeedItem(
                id = "5",
                authorName = "Green Living Guide",
                authorAvatar = "",
                timestamp = "3 days ago",
                category = "Recycling",
                title = "Zero Waste Lifestyle: Tips for Beginners",
                description = "Going zero waste doesn't have to be overwhelming. Start small with these simple steps: bring reusable bags, choose package-free products, and compost your food scraps. Every effort counts!",
                imageUrl = "",
                likeCount = 445,
                isLiked = false,
                comments = listOf(
                    Comment(
                        id = "c4",
                        authorName = "Lisa Anderson",
                        text = "I've been zero waste for 6 months now. Best decision ever!",
                        timestamp = "2 days ago"
                    ),
                    Comment(
                        id = "c5",
                        authorName = "David Kim",
                        text = "Thanks for the practical tips. Starting my journey today!",
                        timestamp = "1 day ago"
                    )
                )
            ),
            FeedItem(
                id = "6",
                authorName = "Wildlife Conservation Society",
                authorAvatar = "",
                timestamp = "4 days ago",
                category = "Wildlife",
                title = "Saving Endangered Species: Success Stories",
                description = "Conservation efforts are working! Several species have been brought back from the brink of extinction. Learn about the incredible success stories and how you can support wildlife conservation.",
                imageUrl = "https://www.ibef.org/uploads/blog/1738142749_b234043aa16cfc9ae2d7.png",
                likeCount = 678,
                isLiked = false,
                comments = listOf()
            ),
            FeedItem(
                id = "7",
                authorName = "Sustainable Living",
                authorAvatar = "",
                timestamp = "5 days ago",
                category = "Climate",
                title = "Urban Gardening: Growing Food in Small Spaces",
                description = "You don't need a big backyard to grow your own food! Urban gardening is a great way to reduce your carbon footprint and enjoy fresh, organic produce. Here's how to get started.",
                imageUrl = "https://containergardening.wordpress.com/wp-content/uploads/2015/02/riser2-jojo-rom-285968_2051946656569_1181604134_31935796_8041270_o.jpg",
                likeCount = 321,
                isLiked = false,
                comments = listOf(
                    Comment(
                        id = "c6",
                        authorName = "Maria Garcia",
                        text = "I have a small balcony garden and it's thriving! Tomatoes and herbs mostly.",
                        timestamp = "4 days ago"
                    )
                )
            )
        )
    }
}
