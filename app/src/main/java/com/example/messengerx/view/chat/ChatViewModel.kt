package com.example.messengerx.view.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messengerx.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class ChatViewModel : ViewModel() {
    private val _chatList = MutableStateFlow<List<ChatItem>>(emptyList())
    val chatList: StateFlow<List<ChatItem>> = _chatList

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadChats()
    }

    private fun loadChats() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.getInstance(context).getChats().awaitResponse()
                }

                if (response.isSuccessful) {
                    response.body()?.let { chatMap ->
                        val chatItems = chatMap.mapNotNull { (id, data) ->
                            val participants = data.participants ?: emptyList()
                            val lastMessage = data.lastMessage ?: "No message"
                            val timestamp = data.timestamp ?: 0L

                            ChatItem(
                                id = id,
                                name = participants.joinToString(", "),
                                isOnline = data.isOnline,
                                lastSeen = data.lastSeen ?: "Unknown",
                                lastMessage = lastMessage
                            )
                        }
                        _chatList.value = chatItems
                        _errorMessage.value = null
                    } ?: run {
                        _errorMessage.value = "Не удалось получить данные чатов"
                    }
                } else {
                    _errorMessage.value = "Ошибка загрузки данных: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Не удалось загрузить чаты: ${e.localizedMessage}"
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
    val id: String,
    val participants: List<String>?,
    val lastMessage: String?,
    val timestamp: Long?,
    val isOnline: Boolean = false,
    val lastSeen: String? = "Unknown"
)


