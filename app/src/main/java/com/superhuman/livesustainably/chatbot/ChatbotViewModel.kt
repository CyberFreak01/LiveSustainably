package com.superhuman.livesustainably.chatbot

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatMessage(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class CategoryResponses(
    val keywords: List<String>,
    val responses: List<String>
)

data class ChatbotData(
    val greetings: List<String>,
    val categories: Map<String, CategoryResponses>,
    val fallback: List<String>,
    val encouragement: List<String>
)

data class ChatbotState(
    val messages: List<ChatMessage> = emptyList(),
    val isTyping: Boolean = false,
    val currentInput: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ChatbotViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(ChatbotState())
    val state: StateFlow<ChatbotState> = _state.asStateFlow()

    private var chatbotData: ChatbotData? = null

    init {
        loadChatbotData()
    }

    private fun loadChatbotData() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }

                val jsonString = context.assets.open("chatbot_responses.json")
                    .bufferedReader()
                    .use { it.readText() }

                val gson = Gson()
                chatbotData = gson.fromJson(jsonString, ChatbotData::class.java)

                val greeting = chatbotData?.greetings?.random() 
                    ?: "Hello! How can I help you today?"

                val initialMessage = ChatMessage(
                    id = "greeting_${System.currentTimeMillis()}",
                    content = greeting,
                    isFromUser = false
                )

                _state.update {
                    it.copy(
                        messages = listOf(initialMessage),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load chatbot data"
                    )
                }
            }
        }
    }

    fun updateInput(input: String) {
        _state.update { it.copy(currentInput = input) }
    }

    fun sendMessage() {
        val input = _state.value.currentInput.trim()
        if (input.isEmpty()) return

        viewModelScope.launch {
            val userMessage = ChatMessage(
                id = "user_${System.currentTimeMillis()}",
                content = input,
                isFromUser = true
            )

            _state.update {
                it.copy(
                    messages = it.messages + userMessage,
                    currentInput = "",
                    isTyping = true
                )
            }

            delay(800 + (Math.random() * 700).toLong())

            val response = generateResponse(input.lowercase())

            val botMessage = ChatMessage(
                id = "bot_${System.currentTimeMillis()}",
                content = response,
                isFromUser = false
            )

            _state.update {
                it.copy(
                    messages = it.messages + botMessage,
                    isTyping = false
                )
            }
        }
    }

    private fun generateResponse(input: String): String {
        val data = chatbotData ?: return "I'm having trouble thinking right now. Please try again!"

        for ((_, category) in data.categories) {
            for (keyword in category.keywords) {
                if (input.contains(keyword)) {
                    return category.responses.random()
                }
            }
        }

        if (Math.random() > 0.7) {
            return data.encouragement.random()
        }

        return data.fallback.random()
    }

    fun clearChat() {
        viewModelScope.launch {
            val greeting = chatbotData?.greetings?.random()
                ?: "Hello! How can I help you today?"

            val initialMessage = ChatMessage(
                id = "greeting_${System.currentTimeMillis()}",
                content = greeting,
                isFromUser = false
            )

            _state.update {
                it.copy(messages = listOf(initialMessage))
            }
        }
    }
}
