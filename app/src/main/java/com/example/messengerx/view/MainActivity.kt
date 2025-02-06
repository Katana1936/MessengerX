package com.example.messengerx.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import com.example.messengerx.view.chat.ChatItemCard
import com.example.messengerx.view.chat.ChatScreen
import com.example.messengerx.view.chat.ChatViewModel
import com.example.messengerx.view.contact.ContactsScreen
import com.example.messengerx.view.contact.ContactsViewModel
import com.example.messengerx.view.contact.ContactsViewModelFactory
import com.example.messengerx.view.login.LoginActivity
import com.example.messengerx.view.stories.AddStoryScreen
import com.example.messengerx.view.stories.StoriesCarousel
import com.example.messengerx.view.stories.StoryViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            setupMainScreen()
        } else {
            navigateToLogin()
        }
    }

    private fun setupMainScreen() {
        setContent {
            val apiService = RetrofitClient.getInstance()

            ThemeMessengerX {
                MainScreen(apiService)
            }
        }
    }
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}

@Composable
fun MainScreen(apiService: ApiService) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(onItemSelected = { route ->
                navController.navigate(route) {
                    launchSingleTop = true
                    restoreState = true
                }
            })
        }
    ) { innerPadding ->
        NavigationHost(
            navController = navController,
            apiService = apiService,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun NavigationHost(
    navController: androidx.navigation.NavHostController,
    apiService: ApiService,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "chats",
        modifier = modifier
    ) {
        composable("chats") {
            val chatViewModel = remember { ChatViewModel(apiService) }
            val storyViewModel = remember { StoryViewModel(apiService) }
            ChatsScreen(
                chatViewModel = chatViewModel,
                storyViewModel = storyViewModel,
                apiService = apiService,
                onChatClick = { chatId, chatName ->
                    navController.navigate("chat/$chatId/$chatName")
                },
                onNewChatClick = {
                    navController.navigate("contacts")
                },
                onAddStoryClick = {
                    navController.navigate("add_story")
                }
            )
        }
        composable("contacts") {
            val contactsViewModel = remember {
                ContactsViewModelFactory(apiService).create(ContactsViewModel::class.java)
            }
            ContactsScreen(viewModel = contactsViewModel)
        }
        composable("account") {
            PlaceholderScreen("Аккаунт временно недоступен")
        }
        composable("settings") {
            PlaceholderScreen("Настройки временно недоступны")
        }
        composable(
            route = "chat/{chatId}/{chatName}",
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType },
                navArgument("chatName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: return@composable
            val chatName = backStackEntry.arguments?.getString("chatName") ?: "Чат"
            ChatScreen(
                chatId = chatId,
                chatName = chatName,
                apiService = apiService
            )
        }
        composable("add_story") {
            val storyViewModel = remember { StoryViewModel(apiService) }
            AddStoryScreen(
                viewModel = storyViewModel,
                userId = "user1",
                onStoryPublished = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun ChatsScreen(
    chatViewModel: ChatViewModel,
    storyViewModel: com.example.messengerx.view.stories.StoryViewModel,
    apiService: ApiService,
    onChatClick: (String, String) -> Unit,
    onNewChatClick: () -> Unit,
    onAddStoryClick: () -> Unit
) {
    val chatList by chatViewModel.chatList.collectAsState()
    val errorMessage by chatViewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        chatViewModel.loadChats()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewChatClick,
                content = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Новый чат"
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                StoriesCarousel(
                    viewModel = storyViewModel,
                    userId = "user1",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                IconButton(
                    onClick = onAddStoryClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Добавить историю",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = "Чаты",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    errorMessage != null -> {
                        Text(
                            text = errorMessage ?: "Ошибка загрузки",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    chatList.isEmpty() -> {
                        Text(
                            text = "Чатов пока нет",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        LazyColumn {
                            items(chatList) { chat ->
                                ChatItemCard(chat = chat, apiService = apiService) {
                                    onChatClick(chat.id, chat.name)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun PlaceholderScreen(message: String) {
    Scaffold {
        Column(modifier = Modifier.padding(it)) {
            Text(
                text = message,
                color = Color.Gray,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
