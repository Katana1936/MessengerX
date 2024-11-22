package com.example.messengerx.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.messengerx.BottomNavigationBar
import com.example.messengerx.R
import com.example.messengerx.ui.theme.MessengerXTheme
import com.example.messengerx.view.chat.ChatListScreen
import com.example.messengerx.view.chat.ChatItem

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Предполагается, что пользователь уже вошёл в систему
        setContent {
            MessengerXTheme {
                // Создаём примерный список чатов. В будущем замените на данные из БД.
                val chatList = listOf(
                    ChatItem("Alice", R.drawable.ic_avatar1, true, "10:00 AM"),
                    ChatItem("Bob", R.drawable.ic_avatar2, false, "Yesterday"),
                    // Добавьте другие чаты здесь
                )

                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(
                            hazeState = rememberHazeState(),
                            onItemSelected = { /* Обработка выбора пунктов */ }
                        )
                    }
                ) { innerPadding ->
                    ChatListScreen(
                        chatList = chatList,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
