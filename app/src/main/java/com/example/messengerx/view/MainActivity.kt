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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.messengerx.BottomNavigationBar
import com.example.messengerx.api.ApiService
import com.example.messengerx.api.RetrofitClient
import com.example.messengerx.ui.theme.ThemeMessengerX
import com.example.messengerx.view.chat.ChatItemCard
import com.example.messengerx.view.chat.ChatScreen
import com.example.messengerx.view.chat.ChatViewModel
import com.example.messengerx.view.contact.ContactsViewModel
import com.example.messengerx.view.stories.StoriesBar
import com.example.messengerx.view.stories.Story
import com.example.messengerx.view.stories.StoryViewModel

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
    val storyViewModel = StoryViewModel(apiService)
    val hazeState = dev.chrisbanes.haze.HazeState()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                hazeState = hazeState,
                onItemSelected = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavigationHost(
            navController = navController,
            apiService = apiService,
            storyViewModel = storyViewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun NavigationHost(
    navController: NavHostController,
    apiService: ApiService,
    storyViewModel: StoryViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "chats",
        modifier = modifier
    ) {
        composable("chats") {
            ChatsScreen(
                viewModel = ChatViewModel(apiService),
                storyViewModel = storyViewModel,
                userId = "user1",
                onChatClick = { chatId -> navController.navigate("chat/$chatId") }
            )
        }
        composable("contacts") {
            val contactsViewModel = ContactsViewModel(apiService)
            ContactsContent(
                viewModel = contactsViewModel,
                onContactClick = { contactId ->
                    println("Выбран контакт: $contactId")
                }
            )
        }
        composable("account") {
            PlaceholderScreen("Функционал аккаунта временно недоступен")
        }
        composable("settings") {
            PlaceholderScreen("Настройки временно недоступны")
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


@Composable
fun ContactsContent(viewModel: ContactsViewModel, onContactClick: (String) -> Unit) {
    val contactList by viewModel.filteredContacts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            Text("Контакты")
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            LazyColumn {
                items(contactList, key = { it.id }) { contact ->
                    Text(
                        text = contact.name,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun PlaceholderScreen(message: String) {
    Scaffold {
        Column(
            modifier = Modifier.padding(it)
        ) {
            Text(
                text = message,
                color = Color.Gray,
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
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
            StoriesBar(
                viewModel = storyViewModel,
                userId = userId,
                onAddStoryClick = {
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
