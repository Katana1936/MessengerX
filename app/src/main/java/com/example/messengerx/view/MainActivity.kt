package com.example.messengerx.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.messengerx.BottomNavigationBar
import com.example.messengerx.ui.theme.ThemeMessengerX
import com.example.messengerx.view.StoriesAdd.StoriesBar
import com.example.messengerx.view.chat.ChatItemCard
import com.example.messengerx.view.chat.ChatViewModel
import com.example.messengerx.view.chat.ChatViewModelFactory
import com.example.messengerx.view.contact.ContactsScreen
import com.example.messengerx.view.contact.ContactsViewModel
import dev.chrisbanes.haze.HazeState

class MainActivity : ComponentActivity() {

    // Регистрация камеры для получения результата
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            // Здесь можно сохранить изображение или загрузить его на сервер
        }
    }

    // Функция для открытия камеры
    fun openCamera() {
        cameraLauncher.launch()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Устанавливаем прозрачность окна
        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.decorView.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        // Проверка первого запуска
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPref.getBoolean("is_first_launch", true)

        if (isFirstLaunch) {
            // Обновляем флаг первого запуска
            sharedPref.edit().putBoolean("is_first_launch", false).apply()

            // Запускаем экран приветствия
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
            return
        }

        // Устанавливаем основной контент
        setContent {
            ThemeMessengerX(isTransparent = true) {
                MainScreen(onAddStoryClick = { openCamera() }) // Передаём функцию открытия камеры
            }
        }
    }
}

@Composable
fun MainScreen(onAddStoryClick: () -> Unit) {
    val navController = rememberNavController()
    val hazeState = remember { HazeState() }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                hazeState = hazeState,
                onItemSelected = { route ->
                    when (route) {
                        "Главная" -> navController.navigate("home")
                        "Контакты" -> navController.navigate("contacts")
                        "Чаты" -> navController.navigate("home")
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
                    HomeScreen(onAddStoryClick = onAddStoryClick)
                }
                composable("contacts") {
                    val viewModel: ContactsViewModel = viewModel()
                    ContactsScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    chatViewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(LocalContext.current.applicationContext)
    ),
    onAddStoryClick: () -> Unit
) {
    val chatList by chatViewModel.chatList.collectAsState()

    Column {
        // Добавляем StoriesBar для отображения историй
        StoriesBar(onAddStoryClick = onAddStoryClick)

        // Список чатов
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(chatList) { chat ->
                ChatItemCard(chat = chat)
            }
        }
    }
}
