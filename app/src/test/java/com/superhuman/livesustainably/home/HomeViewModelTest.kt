package com.superhuman.livesustainably.home

import com.superhuman.livesustainably.home.data.UserStats
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var fakeHomeRepository: FakeHomeRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeHomeRepository = FakeHomeRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fake repository returns correct default streak days`() = runTest {
        val result = fakeHomeRepository.getWeeklyStreak()
        
        assertTrue(result.isSuccess)
        assertEquals(7, result.getOrNull()?.size)
    }

    @Test
    fun `fake repository returns correct user stats`() = runTest {
        fakeHomeRepository.userStatsResult = Result.success(
            UserStats(streakCount = 5, starCount = 10, roseCount = 3, totalXP = 100)
        )
        
        val result = fakeHomeRepository.getUserStats()
        
        assertTrue(result.isSuccess)
        assertEquals(5, result.getOrNull()?.streakCount)
        assertEquals(10, result.getOrNull()?.starCount)
        assertEquals(3, result.getOrNull()?.roseCount)
    }

    @Test
    fun `fake repository returns activities`() = runTest {
        fakeHomeRepository.activitiesResult = Result.success(
            listOf(
                Activity("1", "Stories", 0, 0, 50, false, false),
                Activity("2", "Quiz", 0, 0, 30, false, true)
            )
        )
        
        val result = fakeHomeRepository.getTodaysActivities()
        
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
    }

    @Test
    fun `fake repository handles error case`() = runTest {
        fakeHomeRepository.userStatsResult = Result.failure(Exception("Network error"))
        
        val result = fakeHomeRepository.getUserStats()
        
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun `fake repository mark activity completed returns success`() = runTest {
        val result = fakeHomeRepository.markActivityCompleted("test-activity")
        
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull() == true)
    }

    @Test
    fun `fake repository update streak returns success`() = runTest {
        val result = fakeHomeRepository.updateStreak()
        
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull() == true)
    }

    @Test
    fun `streak day data class holds correct values`() {
        val streakDay = StreakDay("Mon", true, false)
        
        assertEquals("Mon", streakDay.name)
        assertTrue(streakDay.isActive)
        assertFalse(streakDay.isCurrent)
    }

    @Test
    fun `activity data class holds correct values`() {
        val activity = Activity(
            id = "test",
            title = "Test Activity",
            iconRes = 0,
            imageRes = 0,
            xp = 50,
            hasNotification = true,
            isCompleted = false
        )
        
        assertEquals("test", activity.id)
        assertEquals("Test Activity", activity.title)
        assertEquals(50, activity.xp)
        assertTrue(activity.hasNotification)
        assertFalse(activity.isCompleted)
    }
}
