package com.example.messengerx.view

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.messengerx.view.stories.StoriesBar
import com.example.messengerx.view.stories.StoryViewModel
import com.example.messengerx.view.chat.ChatItemCard
import com.example.messengerx.view.chat.ChatScreen
import com.example.messengerx.view.chat.ChatViewModel
import com.example.messengerx.view.stories.Story

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
    val storyViewModel: StoryViewModel = remember { StoryViewModel() }

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
                Column {
                    StoriesBar(
                        viewModel = storyViewModel,
                        userId = "user1",
                        onAddStoryClick = {
                            storyViewModel.addStory(
                                "user1",
                                Story(
                                    imageUrl = "https://example.com/path/to/image.jpg",
                                    timestamp = System.currentTimeMillis(),
                                    caption = "Новая история"
                                )
                            )
                        }
                    )
                    ChatsScreen(apiService = apiService) { chatId ->
                        navController.navigate("chat/$chatId")
                    }
                }
            }
            composable("contacts") {
                Text("Контакты еще не реализованы")
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
    viewModel: ChatViewModel,
    storyViewModel: StoryViewModel,
    userId: String,
    onChatClick: (String) -> Unit
) {
    val chatList by viewModel.chatList.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Добавляем StoriesBar
            StoriesBar(
                viewModel = storyViewModel,
                userId = userId,
                onAddStoryClick = {
                    // Добавление истории
                    storyViewModel.addStory(
                        userId = userId,
                        story = Story(
                            imageUrl = "https://example.com/path/to/image.jpg",
                            timestamp = System.currentTimeMillis(),
                            caption = "Новая история"
                        )
                    )
                }
            )

            if (!errorMessage.isNullOrEmpty()) {
                Text(
                    text = "Ошибка: $errorMessage",
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






