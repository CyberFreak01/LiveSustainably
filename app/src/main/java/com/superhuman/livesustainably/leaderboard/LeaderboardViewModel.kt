package com.superhuman.livesustainably.leaderboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LeaderboardJsonData(
    val league: LeagueData,
    val players: List<LeaderboardPlayerJson>
)

data class LeagueData(
    val name: String,
    val subtitle: String,
    val timeRemaining: String,
    val lockedLeagues: Int
)

data class LeaderboardPlayerJson(
    val id: String,
    val position: Int,
    val username: String,
    val avatarUrl: String,
    val countryFlag: String,
    val roseCount: Int,
    val xp: Int,
    val isFollowing: Boolean
)

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(LeaderboardState())
    val state: StateFlow<LeaderboardState> = _state.asStateFlow()

    init {
        loadLeaderboardData()
    }

    private fun loadLeaderboardData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val jsonString = context.assets.open("leaderboard_mock.json")
                    .bufferedReader()
                    .use { it.readText() }

                val gson = Gson()
                val data = gson.fromJson(jsonString, LeaderboardJsonData::class.java)

                val players = data.players.map { player ->
                    LeaderboardPlayer(
                        id = player.id,
                        position = player.position,
                        username = player.username,
                        avatarUrl = player.avatarUrl,
                        countryFlag = player.countryFlag,
                        roseCount = player.roseCount,
                        xp = player.xp,
                        isFollowing = player.isFollowing
                    )
                }

                _state.update {
                    it.copy(
                        leagueName = data.league.name,
                        leagueSubtitle = data.league.subtitle,
                        timeRemaining = data.league.timeRemaining,
                        lockedLeagues = data.league.lockedLeagues,
                        players = players,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        players = emptyList(),
                        isLoading = false
                    )
                }
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
}
