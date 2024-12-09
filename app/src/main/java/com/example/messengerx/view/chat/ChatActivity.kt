package com.example.messengerx.view.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.messengerx.ui.theme.ThemeMessengerX
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.messengerx.api.RetrofitClient
import com.example.messengerx.view.chat.ChatViewModel
import com.example.messengerx.view.chat.ChatViewModelFactory

class ChatActivity : ComponentActivity() {
    private lateinit var chatViewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = RetrofitClient().createApiService()
        chatViewModel = ViewModelProvider(this, ChatViewModelFactory(apiService))[ChatViewModel::class.java]

        val chatId = intent.getStringExtra("chatId") ?: return

        setContent {
            ThemeMessengerX {
                ChatScreen(chatId = chatId, viewModel = chatViewModel)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(chatId: String, viewModel: ChatViewModel = viewModel()) {
    val messages by viewModel.messages.collectAsState()
    var messageText by remember { mutableStateOf("") }

    // Загружаем сообщения при открытии экрана
    LaunchedEffect(chatId) {
        viewModel.loadMessages(chatId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Чат $chatId") },
                navigationIcon = {
                    IconButton(onClick = { /* Обработайте возврат, если нужно */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Список сообщений
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(messages) { message ->
                        MessageItem(message)
                    }
                }
                // Поле ввода сообщения
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Введите сообщение") },
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            viewModel.sendMessage(chatId, "user1", messageText)
                            messageText = ""
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Отправить")
                    }
                }
            }
        }
    )
}

@Composable
fun MessageItem(message: MessageResponse) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = "От: ${message.senderId}",
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            text = message.message,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
data class MessageResponse(
    val senderId: String,
    val message: String,
    val timestamp: Long
)


