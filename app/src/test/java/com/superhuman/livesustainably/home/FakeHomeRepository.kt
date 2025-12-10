package com.superhuman.livesustainably.home

import com.superhuman.livesustainably.home.data.UserStats

interface FakeHomeRepositoryInterface {
    suspend fun getUserStats(): Result<UserStats>
    suspend fun getWeeklyStreak(): Result<List<StreakDay>>
    suspend fun getTodaysActivities(): Result<List<Activity>>
    suspend fun markActivityCompleted(activityId: String): Result<Boolean>
    suspend fun updateStreak(): Result<Boolean>
}

class FakeHomeRepository : FakeHomeRepositoryInterface {
    var userStatsResult: Result<UserStats> = Result.success(UserStats())
    var streakDaysResult: Result<List<StreakDay>> = Result.success(
        listOf(
            StreakDay("Mon", false, false),
            StreakDay("Tue", false, true),
            StreakDay("Wed", false, false),
            StreakDay("Thu", false, false),
            StreakDay("Fri", false, false),
            StreakDay("Sat", false, false),
            StreakDay("Sun", false, false)
        )
    )
    var activitiesResult: Result<List<Activity>> = Result.success(emptyList())
    var delayMs: Long = 0

    override suspend fun getUserStats(): Result<UserStats> {
        if (delayMs > 0) {
            kotlinx.coroutines.delay(delayMs)
        }
        return userStatsResult
    }

    override suspend fun getWeeklyStreak(): Result<List<StreakDay>> {
        return streakDaysResult
    }

    override suspend fun getTodaysActivities(): Result<List<Activity>> {
        return activitiesResult
    }

    override suspend fun markActivityCompleted(activityId: String): Result<Boolean> {
        return Result.success(true)
    }

    override suspend fun updateStreak(): Result<Boolean> {
        return Result.success(true)
    }
}
