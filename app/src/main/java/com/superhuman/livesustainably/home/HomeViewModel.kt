package com.superhuman.livesustainably.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.superhuman.livesustainably.home.data.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val streakCount: Int = 0,
    val starCount: Int = 0,
    val roseCount: Int = 0,
    val currentDay: String = "Tue",
    val streakDays: List<StreakDay> = getDefaultStreakDays(),
    val activities: List<Activity> = getDefaultActivities(),
    val isLoading: Boolean = true,
    val isLogoutDialogVisible: Boolean = false,
    val error: String? = null
)

private fun getDefaultStreakDays(): List<StreakDay> {
    return listOf(
        StreakDay("Mon", false, false),
        StreakDay("Tue", false, true),
        StreakDay("Wed", false, false),
        StreakDay("Thu", false, false),
        StreakDay("Fri", false, false),
        StreakDay("Sat", false, false),
        StreakDay("Sun", false, false)
    )
}

private fun getDefaultActivities(): List<Activity> {
    return listOf(
        Activity(
            id = "stories",
            title = "Stories",
            iconRes = com.superhuman.livesustainably.R.drawable.ic_stories,
            imageRes = com.superhuman.livesustainably.R.drawable.img_earth_story,
            xp = 20,
            hasNotification = true,
            isCompleted = false
        ),
        Activity(
            id = "quiz",
            title = "Quiz",
            iconRes = com.superhuman.livesustainably.R.drawable.ic_quiz,
            imageRes = com.superhuman.livesustainably.R.drawable.img_quiz,
            xp = 10,
            hasNotification = true,
            isCompleted = false
        ),
        Activity(
            id = "mobility",
            title = "Mobility",
            iconRes = com.superhuman.livesustainably.R.drawable.ic_mobility,
            imageRes = com.superhuman.livesustainably.R.drawable.img_mobility,
            xp = 15,
            hasNotification = false,
            isCompleted = false
        ),
        Activity(
            id = "wellness",
            title = "Wellness",
            iconRes = com.superhuman.livesustainably.R.drawable.ic_wellness,
            imageRes = com.superhuman.livesustainably.R.drawable.img_wellness,
            xp = 15,
            hasNotification = false,
            isCompleted = false
        )
    )
}

data class StreakDay(
    val name: String,
    val isActive: Boolean,
    val isCurrent: Boolean
)

data class Activity(
    val id: String,
    val title: String,
    val iconRes: Int,
    val imageRes: Int,
    val xp: Int,
    val hasNotification: Boolean,
    val isCompleted: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _state: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }

                // Load user stats
                val stats = homeRepository.getUserStats().getOrNull()
                    ?: throw Exception("Failed to load user statistics")

                // Load streak data
                val streakDays = homeRepository.getWeeklyStreak().getOrNull()
                    ?: throw Exception("Failed to load streak data")

                // Load activities
                val activities = homeRepository.getTodaysActivities().getOrNull()
                    ?: throw Exception("Failed to load activities")

                _state.update {
                    it.copy(
                        streakCount = stats.streakCount,
                        starCount = stats.starCount,
                        roseCount = stats.roseCount,
                        currentDay = getCurrentDayName(),
                        streakDays = streakDays,
                        activities = activities,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load home data"
                        // Keep default data visible on error
                    )
                }
            }
        }
    }

    fun onActivityClick(activityId: String) {
        viewModelScope.launch {
            // Mark activity as completed
            homeRepository.markActivityCompleted(activityId)
            // Refresh data
            loadHomeData()
        }
    }

    fun onStartStreakClick() {
        viewModelScope.launch {
            val firstIncomplete = _state.value.activities.firstOrNull { !it.isCompleted }
            firstIncomplete?.let {
                onActivityClick(it.id)
            }
        }
    }

    fun refreshData() {
        loadHomeData()
    }

    fun onLogoutRequested() {
        _state.update { it.copy(isLogoutDialogVisible = true) }
    }

    fun dismissLogoutDialog() {
        _state.update { it.copy(isLogoutDialogVisible = false) }
    }

    fun onLogout() = viewModelScope.launch {
        _state.update { it.copy(isLogoutDialogVisible = false) }
        firebaseAuth.signOut()
    }

    private fun getCurrentDayName(): String {
        val calendar = java.util.Calendar.getInstance()
        return when (calendar.get(java.util.Calendar.DAY_OF_WEEK)) {
            java.util.Calendar.MONDAY -> "Mon"
            java.util.Calendar.TUESDAY -> "Tue"
            java.util.Calendar.WEDNESDAY -> "Wed"
            java.util.Calendar.THURSDAY -> "Thu"
            java.util.Calendar.FRIDAY -> "Fri"
            java.util.Calendar.SATURDAY -> "Sat"
            java.util.Calendar.SUNDAY -> "Sun"
            else -> "Mon"
        }
    }
}