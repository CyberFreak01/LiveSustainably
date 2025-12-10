package com.superhuman.livesustainably.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Friend(
    val id: String,
    val name: String,
    val username: String,
    val avatarUrl: String,
    val latitude: Double,
    val longitude: Double,
    val lastActive: String,
    val status: String,
    val activity: String
)

data class UserLocation(
    val latitude: Double,
    val longitude: Double
)

data class FriendsData(
    val friends: List<Friend>,
    val userLocation: UserLocation
)

data class MapState(
    val friends: List<Friend> = emptyList(),
    val userLocation: UserLocation = UserLocation(51.5074, -0.1278),
    val selectedFriend: Friend? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class MapViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(MapState())
    val state: StateFlow<MapState> = _state.asStateFlow()

    init {
        loadFriendsData()
    }

    private fun loadFriendsData() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                
                val jsonString = context.assets.open("friends_locations.json")
                    .bufferedReader()
                    .use { it.readText() }
                
                val gson = Gson()
                val friendsData = gson.fromJson(jsonString, FriendsData::class.java)
                
                _state.update {
                    it.copy(
                        friends = friendsData.friends,
                        userLocation = friendsData.userLocation,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load friends data"
                    )
                }
            }
        }
    }

    fun selectFriend(friend: Friend?) {
        _state.update { it.copy(selectedFriend = friend) }
    }

    fun dismissFriendCard() {
        _state.update { it.copy(selectedFriend = null) }
    }
}
