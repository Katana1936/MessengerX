package com.example.messengerx.view.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messengerx.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val _chatList = MutableStateFlow<List<ChatItem>>(emptyList())
    val chatList: StateFlow<List<ChatItem>> = _chatList

    init {
        loadChats()
    }

    private fun loadChats() {
        viewModelScope.launch {
            // Загрузка статических данных для демонстрации
            _chatList.value = listOf(
                ChatItem("Alice", isOnline = true, lastSeen = "10:00 AM"),
                ChatItem("Bob", isOnline = false, lastSeen = "Yesterday"),
                ChatItem("Charlie", isOnline = false, lastSeen = "2 days ago")
                // Добавьте другие чаты здесь
            )
        }
    }
}
