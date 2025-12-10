package com.superhuman.livesustainably.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserStats(
    val streakCount: Int,
    val starCount: Int,
    val roseCount: Int,
    val totalXP: Int,
    val activitiesCompleted: Int,
    val co2Saved: String
)

data class NotificationPreferences(
    val dailyReminders: Boolean,
    val achievementAlerts: Boolean,
    val friendActivity: Boolean,
    val weeklyDigest: Boolean,
    val pushNotifications: Boolean
)

data class PrivacyPreferences(
    val showOnMap: Boolean,
    val shareActivity: Boolean,
    val publicProfile: Boolean,
    val showOnLeaderboard: Boolean
)

data class DisplayPreferences(
    val darkMode: Boolean,
    val language: String,
    val units: String
)

data class SustainabilityPreferences(
    val primaryGoals: List<String>,
    val interests: List<String>,
    val dietaryPreference: String
)

data class Preferences(
    val notifications: NotificationPreferences,
    val privacy: PrivacyPreferences,
    val display: DisplayPreferences,
    val sustainability: SustainabilityPreferences
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val earnedDate: String
)

data class UserProfile(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val username: String,
    val avatarUrl: String,
    val bio: String,
    val location: String,
    val joinDate: String,
    val stats: UserStats
)

data class UserProfileData(
    val user: UserProfile,
    val preferences: Preferences,
    val achievements: List<Achievement>
)

data class ProfileState(
    val user: UserProfile? = null,
    val preferences: Preferences? = null,
    val achievements: List<Achievement> = emptyList(),
    val isLoading: Boolean = true,
    val isEditing: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val error: String? = null,
    val editFirstName: String = "",
    val editLastName: String = "",
    val editBio: String = "",
    val editLocation: String = ""
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }

                val jsonString = context.assets.open("user_profile.json")
                    .bufferedReader()
                    .use { it.readText() }

                val gson = Gson()
                val profileData = gson.fromJson(jsonString, UserProfileData::class.java)

                _state.update {
                    it.copy(
                        user = profileData.user,
                        preferences = profileData.preferences,
                        achievements = profileData.achievements,
                        isLoading = false,
                        editFirstName = profileData.user.firstName,
                        editLastName = profileData.user.lastName,
                        editBio = profileData.user.bio,
                        editLocation = profileData.user.location
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load profile data"
                    )
                }
            }
        }
    }

    fun startEditing() {
        _state.update { 
            it.copy(
                isEditing = true,
                editFirstName = it.user?.firstName ?: "",
                editLastName = it.user?.lastName ?: "",
                editBio = it.user?.bio ?: "",
                editLocation = it.user?.location ?: ""
            )
        }
    }

    fun cancelEditing() {
        _state.update { it.copy(isEditing = false) }
    }

    fun saveProfile() {
        viewModelScope.launch {
            val currentUser = _state.value.user ?: return@launch
            
            val updatedUser = currentUser.copy(
                firstName = _state.value.editFirstName,
                lastName = _state.value.editLastName,
                bio = _state.value.editBio,
                location = _state.value.editLocation
            )

            _state.update {
                it.copy(
                    user = updatedUser,
                    isEditing = false
                )
            }
        }
    }

    fun updateEditFirstName(value: String) {
        _state.update { it.copy(editFirstName = value) }
    }

    fun updateEditLastName(value: String) {
        _state.update { it.copy(editLastName = value) }
    }

    fun updateEditBio(value: String) {
        _state.update { it.copy(editBio = value) }
    }

    fun updateEditLocation(value: String) {
        _state.update { it.copy(editLocation = value) }
    }

    fun toggleNotificationPreference(preference: String) {
        val currentPrefs = _state.value.preferences ?: return
        val currentNotifications = currentPrefs.notifications

        val updatedNotifications = when (preference) {
            "dailyReminders" -> currentNotifications.copy(dailyReminders = !currentNotifications.dailyReminders)
            "achievementAlerts" -> currentNotifications.copy(achievementAlerts = !currentNotifications.achievementAlerts)
            "friendActivity" -> currentNotifications.copy(friendActivity = !currentNotifications.friendActivity)
            "weeklyDigest" -> currentNotifications.copy(weeklyDigest = !currentNotifications.weeklyDigest)
            "pushNotifications" -> currentNotifications.copy(pushNotifications = !currentNotifications.pushNotifications)
            else -> currentNotifications
        }

        _state.update {
            it.copy(preferences = currentPrefs.copy(notifications = updatedNotifications))
        }
    }

    fun togglePrivacyPreference(preference: String) {
        val currentPrefs = _state.value.preferences ?: return
        val currentPrivacy = currentPrefs.privacy

        val updatedPrivacy = when (preference) {
            "showOnMap" -> currentPrivacy.copy(showOnMap = !currentPrivacy.showOnMap)
            "shareActivity" -> currentPrivacy.copy(shareActivity = !currentPrivacy.shareActivity)
            "publicProfile" -> currentPrivacy.copy(publicProfile = !currentPrivacy.publicProfile)
            "showOnLeaderboard" -> currentPrivacy.copy(showOnLeaderboard = !currentPrivacy.showOnLeaderboard)
            else -> currentPrivacy
        }

        _state.update {
            it.copy(preferences = currentPrefs.copy(privacy = updatedPrivacy))
        }
    }

    fun toggleDarkMode() {
        val currentPrefs = _state.value.preferences ?: return
        val updatedDisplay = currentPrefs.display.copy(darkMode = !currentPrefs.display.darkMode)
        _state.update {
            it.copy(preferences = currentPrefs.copy(display = updatedDisplay))
        }
    }

    fun showLogoutDialog() {
        _state.update { it.copy(showLogoutDialog = true) }
    }

    fun dismissLogoutDialog() {
        _state.update { it.copy(showLogoutDialog = false) }
    }

    fun logout() {
        viewModelScope.launch {
            _state.update { it.copy(showLogoutDialog = false) }
            firebaseAuth.signOut()
        }
    }
}
