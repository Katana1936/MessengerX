package com.example.messengerx.view.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messengerx.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val _chatList = MutableStateFlow<List<ChatItem>>(emptyList())
    val chatList: StateFlow<List<ChatItem>> = _chatList

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadChats()
    }

    fun loadChats() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getData("chats")
                val chatItems = response.mapNotNull { (id, data) ->
                    val participants = data["participants"] as? List<String> ?: return@mapNotNull null
                    val lastMessage = data["lastMessage"] as? String ?: return@mapNotNull null
                    val timestamp = (data["timestamp"] as? Double)?.toLong() ?: return@mapNotNull null

                    val chatResponse = ChatResponse(
                        id = id,
                        participants = participants,
                        lastMessage = lastMessage,
                        timestamp = timestamp
                    )
                    convertToChatItem(chatResponse)
                }
                _chatList.value = chatItems
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Не удалось загрузить чаты"
            }
        }
    }

    private fun convertToChatItem(response: ChatResponse): ChatItem {
        return ChatItem(
            id = response.id,
            name = response.participants.joinToString(", "),
            isOnline = response.isOnline,
            lastSeen = response.lastSeen,
            lastMessage = response.lastMessage
        )
    }
}

data class ChatRequest(
    val participants: List<String>,
    val lastMessage: String,
    val timestamp: Long
)

data class ChatResponse(
    val id: String,
    val participants: List<String>,
    val lastMessage: String,
    val timestamp: Long,
    val isOnline: Boolean = false,
    val lastSeen: String = "Unknown"
)



