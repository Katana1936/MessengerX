package com.example.messengerx.view

import ChatViewModel
import StoriesBar
import StoryDataStore
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.messengerx.view.contact.ContactsScreen
import com.example.messengerx.view.contact.ContactsViewModel
import com.example.messengerx.view.contact.ContactsViewModelFactory
import com.example.messengerx.view.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val databaseReference by lazy { FirebaseDatabase.getInstance().getReference("users") }
    private lateinit var tokenDataStoreManager: TokenDataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenDataStoreManager = TokenDataStoreManager(this)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            checkUserInDatabase(currentUser.uid,
                onSuccess = { setupMainScreen() },
                onFailure = { navigateToLogin() }
            )
        } else {
            navigateToLogin()
        }
    }

    private fun setupMainScreen() {
        setContent {
            val apiService = RetrofitClient.getInstance()
            val storyDataStore = StoryDataStore(this)
            ThemeMessengerX {
                MainScreen(
                    apiService = apiService,
                    storyDataStore = storyDataStore
                )
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun checkUserInDatabase(userId: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        databaseReference.child(userId).get().addOnCompleteListener { task ->
            if (task.isSuccessful && task.result.exists()) {
                onSuccess()
            } else {
                onFailure()
            }
        }
    }
}

@Composable
fun MainScreen(apiService: ApiService, storyDataStore: StoryDataStore) {
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
            storyDataStore = storyDataStore,
            modifier = Modifier.padding(innerPadding)
        )
    }
}


@Composable
fun NavigationHost(
    navController: NavHostController,
    apiService: ApiService,
    storyDataStore: StoryDataStore,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "chats",
        modifier = modifier
    ) {
        composable("chats") {
            val chatViewModel = remember { ChatViewModel(apiService) }
            ChatsScreen(
                viewModel = chatViewModel,
                storyDataStore = storyDataStore,
                userId = "user1",
                apiService = apiService,
                onChatClick = { chatId, chatName ->
                    navController.navigate("chat/$chatId/${chatName}")
                }
            )
        }
        composable("contacts") {
            val contactsViewModel = remember { ContactsViewModelFactory(apiService).create(ContactsViewModel::class.java) }
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
    }
}




@Composable
fun ChatsScreen(
    viewModel: ChatViewModel = viewModel(),
    storyDataStore: StoryDataStore,
    userId: String,
    apiService: ApiService,
    onChatClick: (String, String) -> Unit
) {
    val chatList by viewModel.chatList.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadChats()
    }

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text(
                text = "Chats",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                color = MaterialTheme.colorScheme.onBackground
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
                    ChatItemCard(
                        chat = chat,
                        apiService = apiService
                    ) {
                        onChatClick(chat.id, chat.name)
                    }
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
