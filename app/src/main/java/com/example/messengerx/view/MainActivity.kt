package com.example.messengerx.view

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
import com.example.messengerx.ui.theme.ThemeMessengerX
import com.example.messengerx.view.chat.ChatItemCard
import com.example.messengerx.view.chat.ChatScreen
import com.example.messengerx.view.chat.ChatViewModel
import com.example.messengerx.view.contact.ContactsViewModel
import com.example.messengerx.view.stories.AddStoryScreen
import com.example.messengerx.view.stories.StoriesBar
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
    val arePermissionsGranted = remember { mutableStateOf(false) } // Обновление на MutableState

    val requestPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        arePermissionsGranted.value = permissions.values.all { it }
    }

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
        if (arePermissionsGranted.value) {
            NavigationHost(
                navController = navController,
                apiService = apiService,
                storyViewModel = storyViewModel,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Требуются разрешения для использования камеры и галереи")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    requestPermissionsLauncher.launch(
                        arrayOf(
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.READ_MEDIA_IMAGES,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    )
                }) {
                    Text("Запросить разрешения")
                }
            }
        }
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
                navController = navController // Передаем контроллер навигации
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
        // Новый маршрут для добавления истории
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
    navController: NavController, // Добавляем NavController для навигации
    onChatClick: (String) -> Unit
) {
    val chatList by viewModel.chatList.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Обновленный StoriesBar с переходом
            StoriesBar(
                viewModel = storyViewModel,
                userId = userId,
                onAddStoryClick = { requestPermissions, arePermissionsGranted ->
                    if (arePermissionsGranted) {
                        navController.navigate("add_story")
                    } else {
                        requestPermissions()
                    }
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

