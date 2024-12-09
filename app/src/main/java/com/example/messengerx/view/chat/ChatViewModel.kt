package com.example.messengerx.view.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messengerx.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val apiService: ApiService) : ViewModel() {
    private val _chatList = MutableStateFlow<List<ChatItem>>(emptyList())
    val chatList: StateFlow<List<ChatItem>> = _chatList

    private val _messages = MutableStateFlow<List<MessageResponse>>(emptyList())
    val messages: StateFlow<List<MessageResponse>> = _messages

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadChats() {
        viewModelScope.launch {
            try {
                val response = apiService.getChats().execute()
                if (response.isSuccessful) {
                    val chatItems = response.body()?.map { (id, chatResponse) ->
                        ChatItem(
                            id = id,
                            name = chatResponse.participants?.joinToString(", ") ?: "Unknown",
                            isOnline = chatResponse.isOnline,
                            lastSeen = chatResponse.lastSeen ?: "Unknown",
                            lastMessage = chatResponse.lastMessage ?: "No message"
                        )
                    } ?: emptyList()
                    _chatList.value = chatItems
                } else {
                    _errorMessage.value = "Ошибка загрузки чатов: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка: ${e.localizedMessage}"
            }
        }
    }

    fun loadMessages(chatId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getMessages(chatId).execute()
                if (response.isSuccessful) {
                    val messages = response.body()?.values?.sortedBy { it.timestamp } ?: emptyList()
                    _messages.value = messages
                } else {
                    _errorMessage.value = "Ошибка загрузки сообщений: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка: ${e.localizedMessage}"
            }
        }
    }

    fun sendMessage(chatId: String, senderId: String, message: String) {
        viewModelScope.launch {
            try {
                val messageRequest = MessageRequest(senderId, message, System.currentTimeMillis())
                val response = apiService.sendMessage(chatId, messageRequest).execute()
                if (response.isSuccessful) {
                    loadMessages(chatId)
                } else {
                    _errorMessage.value = "Ошибка отправки сообщения: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка: ${e.localizedMessage}"
            }
        }
    }
}

data class ChatRequest(
    val participants: List<String>,
    val lastMessage: String,
    val timestamp: Long
)

data class ChatResponse(
    val isOnline: Boolean = false,
    val lastSeen: String = "",
    val participants: List<String> = emptyList(),
    val name: String = "",
    val timestamp: Long = 0L,
    val lastMessage: String = ""
)



data class MessageRequest(
    val senderId: String,
    val message: String,
    val timestamp: Long
)

data class MessageResponse(
    val senderId: String,
    val message: String,
    val timestamp: Long
)



