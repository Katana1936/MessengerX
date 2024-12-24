package com.example.messengerx.view.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messengerx.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val apiService: ApiService) : ViewModel() {

    private val _messages = MutableStateFlow<List<ApiService.MessageResponse>>(emptyList())
    val messages: StateFlow<List<ApiService.MessageResponse>> = _messages

    private val _chatList = MutableStateFlow<List<ApiService.ChatItem>>(emptyList())
    val chatList: StateFlow<List<ApiService.ChatItem>> = _chatList

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadChats() {
        viewModelScope.launch {
            try {
                val response = apiService.getChats()
                val chatItems = response.map { (id, chatResponse) ->
                    ApiService.ChatItem(
                        id = id,
                        name = chatResponse.name.ifEmpty { "Без имени" }
                    )
                }
                _chatList.value = chatItems
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка подключения: ${e.localizedMessage}"
            }
        }
    }

    fun loadMessages(chatId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getMessages(chatId)
                val sortedMessages = response.values.sortedBy { it.timestamp }
                _messages.value = sortedMessages
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка: ${e.localizedMessage}"
            }
        }
    }

    fun sendMessage(chatId: String, senderId: String, message: String) {
        viewModelScope.launch {
            try {
                val messageRequest = ApiService.MessageRequest(senderId, message, System.currentTimeMillis())
                apiService.sendMessage(chatId, messageRequest)
                loadMessages(chatId)
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка отправки сообщения: ${e.localizedMessage}"
            }
        }
    }
}
