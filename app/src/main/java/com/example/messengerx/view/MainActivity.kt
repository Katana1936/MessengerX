package com.example.messengerx.view

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.messengerx.BottomNavigationBar
import com.example.messengerx.api.ApiService
import com.example.messengerx.api.RetrofitClient
import com.example.messengerx.ui.theme.ThemeMessengerX
import com.example.messengerx.view.chat.ChatItem
import com.example.messengerx.view.chat.ChatItemCard
import com.example.messengerx.view.chat.ChatScreen

class MainActivity : ComponentActivity() {
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        apiService = RetrofitClient.getInstance()

        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.decorView.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        setContent {
            ThemeMessengerX(isTransparent = true) {
                MainScreen(apiService)
            }
        }
    }
}

@Composable
fun MainScreen(apiService: ApiService) {
    val navController = rememberNavController()
    var chatList by remember { mutableStateOf<List<ChatItem>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Загружаем чаты при создании экрана
    LaunchedEffect(Unit) {
        try {
            val response = apiService.getChats().execute()
            if (response.isSuccessful) {
                chatList = response.body()?.map { (id, chatResponse) ->
                    ChatItem(
                        id = id,
                        name = chatResponse.participants?.joinToString(", ") ?: "Unknown",
                        isOnline = chatResponse.isOnline,
                        lastSeen = chatResponse.lastSeen ?: "Unknown",
                        lastMessage = chatResponse.lastMessage ?: "No message"
                    )
                } ?: emptyList()
            } else {
                errorMessage = "Ошибка загрузки чатов: ${response.message()}"
            }
        } catch (e: Exception) {
            errorMessage = "Ошибка: ${e.localizedMessage}"
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                hazeState = dev.chrisbanes.haze.HazeState(),
                onItemSelected = { route ->
                    when (route) {
                        "Чаты" -> navController.navigate("chats")
                        "Контакты" -> navController.navigate("contacts")
                        "Аккаунт" -> navController.navigate("account")
                        "Настройки" -> navController.navigate("settings")
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "chats",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("chats") {
                ChatsScreen(
                    chatList = chatList,
                    errorMessage = errorMessage,
                    onChatClick = { chatId ->
                        navController.navigate("chat/$chatId")
                    }
                )
            }
            composable(
                route = "chat/{chatId}",
                arguments = listOf(navArgument("chatId") { type = NavType.StringType })
            ) { backStackEntry ->
                val chatId = backStackEntry.arguments?.getString("chatId") ?: return@composable
                ChatScreen(chatId = chatId, apiService = apiService)
            }
        }
    }
}

@Composable
fun ChatsScreen(
    chatList: List<ChatItem>,
    errorMessage: String?,
    onChatClick: (String) -> Unit
) {
    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (!errorMessage.isNullOrEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
            LazyColumn {
                items(chatList, key = { it.id }) { chat ->
                    ChatItemCard(chat = chat) {
                        onChatClick(chat.id)
                    }
                }
            }
        }
    }
}
