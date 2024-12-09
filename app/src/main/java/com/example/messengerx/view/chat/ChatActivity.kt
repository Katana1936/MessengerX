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
import com.example.messengerx.api.ApiService
import com.example.messengerx.api.RetrofitClient
import com.example.messengerx.ui.theme.ThemeMessengerX
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch

class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = RetrofitClient.getInstance()
        val chatId = intent.getStringExtra("chatId") ?: return

        setContent {
            ThemeMessengerX {
                ChatScreen(chatId = chatId, apiService = apiService)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(chatId: String, apiService: ApiService) {
    var messages by remember { mutableStateOf<List<MessageResponse>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var messageText by remember { mutableStateOf("") }

    // Загружаем сообщения при открытии экрана
    LaunchedEffect(chatId) {
        try {
            val response = apiService.getMessages(chatId).execute()
            if (response.isSuccessful) {
                messages = response.body()?.values?.sortedBy { it.timestamp } ?: emptyList()
            } else {
                errorMessage = "Ошибка загрузки сообщений: ${response.message()}"
            }
        } catch (e: Exception) {
            errorMessage = "Ошибка: ${e.localizedMessage}"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Чат $chatId") },
                navigationIcon = {
                    IconButton(onClick = { /* Обработка возврата */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (!errorMessage.isNullOrEmpty()) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(messages) { message ->
                    MessageItem(message)
                }
            }

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
                        if (messageText.isNotBlank()) {
                            sendMessage(chatId, "user1", messageText, apiService) {
                                messages = it
                            }
                            messageText = ""
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Отправить")
                }
            }
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun sendMessage(
    chatId: String,
    senderId: String,
    message: String,
    apiService: ApiService,
    onMessagesUpdated: (List<MessageResponse>) -> Unit
) {
    val messageRequest = MessageRequest(senderId, message, System.currentTimeMillis())
    kotlinx.coroutines.GlobalScope.launch {
        try {
            val response = apiService.sendMessage(chatId, messageRequest).execute()
            if (response.isSuccessful) {
                val updatedMessages = apiService.getMessages(chatId).execute().body()?.values?.sortedBy { it.timestamp } ?: emptyList()
                onMessagesUpdated(updatedMessages)
            }
        } catch (e: Exception) {
            println("Ошибка отправки сообщения: ${e.localizedMessage}")
        }
    }
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



