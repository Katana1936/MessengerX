package com.example.messengerx.view

import android.content.Intent
import android.os.Bundle
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.messengerx.BottomNavigationBar
import com.example.messengerx.api.ApiService
import com.example.messengerx.api.RetrofitClient
import com.example.messengerx.api.TokenDataStoreManager
import com.example.messengerx.ui.theme.ThemeMessengerX
import com.example.messengerx.view.chat.ChatItemCard
import com.example.messengerx.view.chat.ChatScreen
import com.example.messengerx.view.chat.ChatViewModel
import com.example.messengerx.view.contact.ContactsViewModel
import com.example.messengerx.view.login.LoginActivity
import com.example.messengerx.view.stories.AddStoryScreen
import com.example.messengerx.view.stories.StoriesBar
import com.example.messengerx.view.stories.StoryViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private lateinit var tokenDataStoreManager: TokenDataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tokenDataStoreManager = TokenDataStoreManager(this)

        lifecycleScope.launch {
            val token = tokenDataStoreManager.token.first()
            if (token.isNullOrEmpty()) {
                navigateToLogin()
            } else {
                setupMainScreen()
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun setupMainScreen() {
        setContent {
            ThemeMessengerX(isTransparent = true) {
                MainScreen(apiService = RetrofitClient.getInstance())
            }
        }
    }
}


@Composable
fun MainScreen(apiService: ApiService) {
    val navController = rememberNavController()
    val storyViewModel = remember { StoryViewModel(apiService) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                hazeState = dev.chrisbanes.haze.HazeState(),
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
                onChatClick = { chatId -> navController.navigate("chat/$chatId") },
                navController = navController
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
        composable("add_story") {
            AddStoryScreen(
                viewModel = storyViewModel,
                userId = "user1",
                onBack = { navController.popBackStack() } // Возврат на предыдущий экран
            )
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
    navController: NavController,
    onChatClick: (String) -> Unit
) {
    val chatList by viewModel.chatList.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding)) {
            StoriesBar(
                viewModel = storyViewModel,
                userId = userId,
                navController = navController
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



