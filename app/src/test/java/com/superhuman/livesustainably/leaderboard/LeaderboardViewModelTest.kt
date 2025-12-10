package com.superhuman.livesustainably.leaderboard

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LeaderboardViewModelTest {

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
    fun `LeaderboardState has correct default values`() {
        val state = LeaderboardState()
        
        assertEquals("", state.leagueName)
        assertEquals("", state.leagueSubtitle)
        assertEquals("", state.timeRemaining)
        assertEquals(0, state.lockedLeagues)
        assertTrue(state.players.isEmpty())
        assertFalse(state.isLoading)
    }

    @Test
    fun `Player data class holds correct values`() {
        val player = Player(
            id = "1",
            position = 1,
            username = "TestPlayer",
            avatarUrl = "https://example.com/avatar.png",
            countryFlag = "🇺🇸",
            roseCount = 5,
            xp = 1000,
            isFollowing = true
        )
        
        assertEquals("1", player.id)
        assertEquals(1, player.position)
        assertEquals("TestPlayer", player.username)
        assertEquals("https://example.com/avatar.png", player.avatarUrl)
        assertEquals("🇺🇸", player.countryFlag)
        assertEquals(5, player.roseCount)
        assertEquals(1000, player.xp)
        assertTrue(player.isFollowing)
    }

    @Test
    fun `Player copy changes only specified values`() {
        val player = Player(
            id = "1",
            position = 1,
            username = "TestPlayer",
            avatarUrl = "",
            countryFlag = "🇬🇧",
            roseCount = 3,
            xp = 500,
            isFollowing = false
        )
        
        val updatedPlayer = player.copy(isFollowing = true)
        
        assertEquals(player.id, updatedPlayer.id)
        assertEquals(player.position, updatedPlayer.position)
        assertEquals(player.username, updatedPlayer.username)
        assertEquals(player.xp, updatedPlayer.xp)
        assertTrue(updatedPlayer.isFollowing)
        assertFalse(player.isFollowing)
    }

    @Test
    fun `LeaderboardState with players updates correctly`() {
        val players = listOf(
            Player("1", 1, "Player1", "", "🇺🇸", 5, 1000, false),
            Player("2", 2, "Player2", "", "🇬🇧", 3, 800, true)
        )
        
        val state = LeaderboardState(
            leagueName = "Gold League",
            leagueSubtitle = "Top performers",
            timeRemaining = "2D 5h",
            lockedLeagues = 2,
            players = players,
            isLoading = false
        )
        
        assertEquals("Gold League", state.leagueName)
        assertEquals(2, state.players.size)
        assertEquals("Player1", state.players[0].username)
        assertEquals("Player2", state.players[1].username)
    }

    @Test
    fun `toggle follow logic works correctly`() {
        val players = listOf(
            Player("1", 1, "Player1", "", "🇺🇸", 5, 1000, false),
            Player("2", 2, "Player2", "", "🇬🇧", 3, 800, true)
        )
        
        val targetId = "1"
        val updatedPlayers = players.map { player ->
            if (player.id == targetId) {
                player.copy(isFollowing = !player.isFollowing)
            } else {
                player
            }
        }
        
        assertTrue(updatedPlayers[0].isFollowing)
        assertTrue(updatedPlayers[1].isFollowing)
    }

    @Test
    fun `position ordering is correct`() {
        val players = listOf(
            Player("1", 3, "Player1", "", "🇺🇸", 5, 500, false),
            Player("2", 1, "Player2", "", "🇬🇧", 3, 1000, true),
            Player("3", 2, "Player3", "", "🇫🇷", 4, 800, false)
        )
        
        val sortedPlayers = players.sortedBy { it.position }
        
        assertEquals(1, sortedPlayers[0].position)
        assertEquals(2, sortedPlayers[1].position)
        assertEquals(3, sortedPlayers[2].position)
    }
}
