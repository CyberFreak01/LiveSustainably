package com.superhuman.livesustainably.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(LeaderboardState())
    val state: StateFlow<LeaderboardState> = _state.asStateFlow()

    init {
        loadLeaderboardData()
    }

    private fun loadLeaderboardData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Load mock data from JSON
            val mockPlayers = getMockLeaderboardData()

            _state.update {
                it.copy(
                    players = mockPlayers,
                    isLoading = false
                )
            }
        }
    }

    fun toggleFollow(playerId: String) {
        _state.update { currentState ->
            currentState.copy(
                players = currentState.players.map { player ->
                    if (player.id == playerId) {
                        player.copy(isFollowing = !player.isFollowing)
                    } else {
                        player
                    }
                }
            )
        }
    }

    private fun getMockLeaderboardData(): List<LeaderboardPlayer> {
        // This would normally load from assets/leaderboard_mock.json
        return listOf(
            LeaderboardPlayer(
                id = "1",
                position = 1,
                username = "ksiyabi4",
                avatarUrl = "", // Will use placeholder
                countryFlag = "🇮🇳",
                roseCount = 20,
                xp = 0,
                isFollowing = false
            ),
            LeaderboardPlayer(
                id = "2",
                position = 1,
                username = "sgsbpijd-64225d",
                avatarUrl = "",
                countryFlag = "🇺🇸",
                roseCount = 20,
                xp = 0,
                isFollowing = false
            ),
            LeaderboardPlayer(
                id = "3",
                position = 1,
                username = "ihmgvgsg-663b57",
                avatarUrl = "",
                countryFlag = "🇬🇧",
                roseCount = 20,
                xp = 0,
                isFollowing = false
            ),
            LeaderboardPlayer(
                id = "4",
                position = 1,
                username = "cristina-1e51cb",
                avatarUrl = "",
                countryFlag = "🇧🇷",
                roseCount = 20,
                xp = 0,
                isFollowing = false
            ),
            LeaderboardPlayer(
                id = "5",
                position = 1,
                username = "zohaib-046018",
                avatarUrl = "",
                countryFlag = "🇵🇰",
                roseCount = 20,
                xp = 0,
                isFollowing = false
            )
        )
    }
}
