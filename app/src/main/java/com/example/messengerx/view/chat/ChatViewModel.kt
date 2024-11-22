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
        // Инициализация с пустым списком или загрузка из источника данных
        loadChats()
    }

    private fun loadChats() {
        viewModelScope.launch {
            // TODO: Загрузите данные из базы данных или другого источника
            // Пример статических данных для демонстрации
            _chatList.value = listOf(
                ChatItem("Amber Griffin", R.drawable.avatar_carmen, true, ""),
                ChatItem("Andreea Wells", R.drawable.avatar_carmen, true, ""),
                ChatItem("Kathryn Hill", R.drawable.avatar_carmen, false, "5 минут назад"),
                ChatItem("Kelly McCoy", R.drawable.avatar_carmen, false, "7 минут назад"),
                ChatItem("Tyler Banks", R.drawable.avatar_carmen, false, "9 минут назад"),
                ChatItem("Carmen Mendez", R.drawable.avatar_carmen, false, "10 минут назад")
            )
        }
    }
}
