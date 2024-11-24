package com.example.messengerx.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.messengerx.ui.theme.MessengerXTheme
import com.example.messengerx.view.chat.ChatItem
import com.example.messengerx.view.chat.ChatItemCard
import com.example.messengerx.view.chat.ChatViewModel
import com.example.messengerx.BottomNavigationBar
import dev.chrisbanes.haze.HazeState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MessengerXTheme {
                // Получаем экземпляр ChatViewModel
                val chatViewModel: ChatViewModel = viewModel()
                val chatListState = chatViewModel.chatList.collectAsState()

                // Создаем состояние для Haze (если используете эту библиотеку)
                val hazeState = remember { HazeState() }

                // Основной интерфейс с нижней навигационной панелью
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(
                            hazeState = hazeState,
                            onItemSelected = { /* Обработка выбора пунктов */ }
                        )
                    }
                ) { innerPadding ->
                    // Экран списка чатов
                    ChatListScreen(
                        chatList = chatListState.value,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(chatList: List<ChatItem>, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Чаты") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF0F3FF)
                )
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(0xFFF0F3FF))
                    .padding(paddingValues)
            ) {
                items(chatList) { chat ->
                    ChatItemCard(chat)
                }
            }
        }
    )
}
