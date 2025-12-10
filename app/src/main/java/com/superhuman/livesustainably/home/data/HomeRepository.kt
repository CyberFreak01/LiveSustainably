package com.superhuman.livesustainably.home.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.superhuman.livesustainably.R
import com.superhuman.livesustainably.home.Activity
import com.superhuman.livesustainably.home.StreakDay
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

data class UserStats(
    val streakCount: Int = 0,
    val starCount: Int = 0,
    val roseCount: Int = 0,
    val totalXP: Int = 0
)

data class StreakData(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActiveDate: Long? = null,
    val activeDays: List<String> = emptyList() // ["Mon", "Tue", etc.]
)

@Singleton
class HomeRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val userId: String?
        get() = auth.currentUser?.uid

    suspend fun getUserStats(): Result<UserStats> {
        return try {
            val userId = userId ?: return Result.failure(Exception("User not authenticated"))

            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            if (!document.exists()) {
                return Result.failure(Exception("User document not found"))
            }

            val stats = UserStats(
                streakCount = document.getLong("streakCount")?.toInt() ?: 0,
                starCount = document.getLong("starCount")?.toInt() ?: 0,
                roseCount = document.getLong("roseCount")?.toInt() ?: 0,
                totalXP = document.getLong("totalXP")?.toInt() ?: 0
            )

            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWeeklyStreak(): Result<List<StreakDay>> {
        return try {
            val userId = userId ?: return Result.failure(Exception("User not authenticated"))

            val document = firestore.collection("users")
                .document(userId)
                .collection("streak")
                .document("current")
                .get()
                .await()

            val activeDays = if (document.exists()) {
                document.get("activeDays") as? List<String> ?: emptyList()
            } else {
                emptyList()
            }
            
            val currentDay = getCurrentDayName()

            val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            val streakDays = daysOfWeek.map { day ->
                StreakDay(
                    name = day,
                    isActive = activeDays.contains(day),
                    isCurrent = day == currentDay
                )
            }

            Result.success(streakDays)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTodaysActivities(): Result<List<Activity>> {
        return try {
            val userId = userId ?: return Result.failure(Exception("User not authenticated"))

            // Get user's completed activities for today
            val completedActivities = try {
                firestore.collection("users")
                    .document(userId)
                    .collection("completedActivities")
                    .whereEqualTo("date", getTodayDate())
                    .get()
                    .await()
                    .documents
                    .mapNotNull { it.getString("activityId") }
            } catch (e: Exception) {
                emptyList()
            }

            // Get all available activities
            val activitiesSnapshot = firestore.collection("activities")
                .whereEqualTo("isActive", true)
                .get()
                .await()

            val activities = activitiesSnapshot.documents.mapNotNull { doc ->
                try {
                    val title = doc.getString("title")
                    if (title.isNullOrBlank()) return@mapNotNull null

                    Activity(
                        id = doc.id,
                        title = title,
                        iconRes = getIconResource(doc.getString("iconName") ?: ""),
                        imageRes = getImageResource(doc.getString("imageName") ?: ""),
                        xp = doc.getLong("xp")?.toInt() ?: 0,
                        hasNotification = doc.getBoolean("hasNotification") ?: false,
                        isCompleted = completedActivities.contains(doc.id)
                    )
                } catch (e: Exception) {
                    null
                }
            }

            Result.success(activities)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStreak(): Result<Boolean> {
        return try {
            val userId = userId ?: return Result.failure(Exception("User not authenticated"))
            val currentDay = getCurrentDayName()

            val streakRef = firestore.collection("users")
                .document(userId)
                .collection("streak")
                .document("current")

            firestore.runTransaction { transaction ->
                val snapshot = transaction[streakRef]
                val activeDays = (snapshot.get("activeDays") as? List<String>)?.toMutableList() ?: mutableListOf()

                if (!activeDays.contains(currentDay)) {
                    activeDays.add(currentDay)
                    transaction.update(streakRef, "activeDays", activeDays)
                    transaction.update(streakRef, "currentStreak", activeDays.size)
                }
            }.await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markActivityCompleted(activityId: String): Result<Boolean> {
        return try {
            val userId = userId ?: return Result.failure(Exception("User not authenticated"))
            
            if (activityId.isBlank()) {
                return Result.failure(Exception("Activity ID cannot be empty"))
            }

            // Add completed activity
            firestore.collection("users")
                .document(userId)
                .collection("completedActivities")
                .add(
                    mapOf(
                        "activityId" to activityId,
                        "date" to getTodayDate(),
                        "timestamp" to System.currentTimeMillis()
                    )
                )
                .await()

            // Update streak
            val streakResult = updateStreak()
            if (streakResult.isFailure) {
                // Log but don't fail - activity was still marked complete
                streakResult.exceptionOrNull()?.printStackTrace()
            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Helper functions
    private fun getCurrentDayName(): String {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Mon"
            Calendar.TUESDAY -> "Tue"
            Calendar.WEDNESDAY -> "Wed"
            Calendar.THURSDAY -> "Thu"
            Calendar.FRIDAY -> "Fri"
            Calendar.SATURDAY -> "Sat"
            Calendar.SUNDAY -> "Sun"
            else -> "Mon"
        }
    }

    private fun getTodayDate(): String {
        val calendar = Calendar.getInstance()
        return "${calendar.get(Calendar.YEAR)}-" +
                "${calendar.get(Calendar.MONTH) + 1}-" +
                "${calendar.get(Calendar.DAY_OF_MONTH)}"
    }

    private fun getIconResource(iconName: String): Int {
        // Map icon names from Firestore to drawable resources
        return when (iconName) {
            "stories" -> R.drawable.ic_stories
            "quiz" -> R.drawable.ic_quiz
            "mobility" -> R.drawable.ic_mobility
            "wellness" -> R.drawable.ic_wellness
            else -> R.drawable.ic_eye_closed
        }
    }

    private fun getImageResource(imageName: String): Int {
        // Map image names from Firestore to drawable resources
        return when (imageName) {
            "earth_story" -> R.drawable.img_earth_story
            "quiz" -> R.drawable.img_quiz
            "mobility" -> R.drawable.img_mobility
            "wellness" -> R.drawable.img_wellness
            else -> R.drawable.app_icon
        }
    }
}
