package com.example.messengerx.view

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.messengerx.BottomNavigationBar
import com.example.messengerx.ui.theme.ThemeMessengerX
import com.example.messengerx.view.StoriesAdd.StoriesBar
import com.example.messengerx.view.chat.ChatItemCard
import com.example.messengerx.view.chat.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import dev.chrisbanes.haze.HazeState

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.decorView.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        setContent {
            ThemeMessengerX(isTransparent = true) {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val hazeState = remember { HazeState() }
    val chatViewModel: ChatViewModel = viewModel()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                hazeState = hazeState,
                onItemSelected = { route ->
                    when (route) {
                        "Чаты" -> navController.navigate("chats") { launchSingleTop = true }
                        "Контакты" -> navController.navigate("contacts") { launchSingleTop = true }
                        "Аккаунт" -> navController.navigate("account") { launchSingleTop = true }
                        "Настройки" -> navController.navigate("settings") { launchSingleTop = true }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "chats"
            ) {
                composable("chats") {
                    ChatsScreen(chatViewModel) { chatId ->
                        navController.navigate("chat/$chatId")
                    }
                }
            }
        }
    }
}

@Composable
fun ChatsScreen(viewModel: ChatViewModel = viewModel(), onChatClick: (String) -> Unit = {}) {
    val chatList by viewModel.chatList.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding)) {
            StoriesBar(onAddStoryClick = { /* Добавить логику добавления истории */ })

            if (!errorMessage.isNullOrEmpty()) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
            LazyColumn {
                items(chatList, key = { it.id }) { chat ->
                    ChatItemCard(chat = chat) { onChatClick(chat.id) }
                }
            }
        }
    }
}
