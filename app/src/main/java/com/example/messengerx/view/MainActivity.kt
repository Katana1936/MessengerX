package com.example.messengerx.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.messengerx.BottomNavigationBar
import com.example.messengerx.view.chat.ChatItemCard
import com.example.messengerx.view.chat.ChatViewModel
import com.example.messengerx.view.chat.ChatViewModelFactory
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Проверка первого запуска
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPref.getBoolean("is_first_launch", true)

        if (isFirstLaunch) {
            // Отмечаем, что первый запуск завершен
            sharedPref.edit().putBoolean("is_first_launch", false).apply()

            // Переход к WelcomeActivity
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish() // Закрываем текущую активность
            return
        }

        // Устанавливаем контент для MainActivity
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val hazeState = remember { HazeState() }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                hazeState = hazeState,
                onItemSelected = { route ->
                    when (route) {
                        "Главная" -> navController.navigate("home")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE3F2FD))
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    HomeScreen()
                }
            }
        }
    }
}



@Composable
fun HomeScreen(
    chatViewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(LocalContext.current.applicationContext)
    )
) {
    val chatList by chatViewModel.chatList.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(chatList) { chat ->
            ChatItemCard(chat = chat)
        }
    }
}
