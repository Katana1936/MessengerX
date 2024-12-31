package com.example.messengerx.view.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messengerx.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val apiService: ApiService) : ViewModel() {

    private val _chatList = MutableStateFlow<List<ApiService.ChatItem>>(emptyList())
    val chatList: StateFlow<List<ApiService.ChatItem>> = _chatList

    private val _messages = MutableStateFlow<List<ApiService.MessageResponse>>(emptyList())
    val messages: StateFlow<List<ApiService.MessageResponse>> = _messages

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadChats() {
        viewModelScope.launch {
            try {
                val response = apiService.getChats()
                val chatItems = response.documents.map { document ->
                    ApiService.ChatItem(
                        id = document.name.substringAfterLast("/"), // Получение ID
                        name = document.fields["name"]?.stringValue ?: "Без имени"
                    )
                }
                _chatList.value = chatItems
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка загрузки чатов: ${e.localizedMessage}"
            }
        }
    }

    fun loadMessages(chatId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getMessages(chatId)
                val messages = response.documents.map { document ->
                    ApiService.MessageResponse(
                        senderId = document.fields["senderId"] ?: ApiService.FieldValue(),
                        message = document.fields["message"] ?: ApiService.FieldValue(),
                        timestamp = document.fields["timestamp"] ?: ApiService.FieldValue()
                    )
                }
                _messages.value = messages
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка загрузки сообщений: ${e.localizedMessage}"
            }
        }
    }


    fun sendMessage(chatId: String, senderId: String, message: String) {
        viewModelScope.launch {
            try {
                val messageRequest = ApiService.MessageRequest(
                    senderId = ApiService.FieldValue(stringValue = senderId),
                    message = ApiService.FieldValue(stringValue = message),
                    timestamp = ApiService.FieldValue(timestampValue = System.currentTimeMillis().toString())
                )
                apiService.sendMessage(chatId, ApiService.FirestoreDocumentRequest(messageRequest))
                loadMessages(chatId)
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка отправки сообщения: ${e.localizedMessage}"
            }
        }
    }
}
