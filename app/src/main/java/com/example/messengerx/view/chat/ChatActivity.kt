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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.messengerx.api.ApiService
import com.example.messengerx.api.RetrofitClient
import com.example.messengerx.ui.theme.ThemeMessengerX

class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = RetrofitClient.getInstance()
        val chatId = intent.getStringExtra("chatId") ?: return
        val chatName = intent.getStringExtra("chatName") ?: "Chat"

        setContent {
            ThemeMessengerX {
                ChatScreen(chatId = chatId, chatName = chatName, apiService = apiService)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(chatId: String, chatName: String, apiService: ApiService) {
    val viewModel: ChatViewModel = viewModel()

    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMessages(chatId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(chatName) },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(messages) { message ->
                    MessageItem(message)
                }
            }

            UserInput(
                messageText = messageText,
                onMessageTextChange = { messageText = it },
                onMessageSend = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessage(chatId, "user1", messageText)
                        messageText = ""
                    }
                }
            )
        }
    }
}

@Composable
fun UserInput(
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    onMessageSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        TextField(
            value = messageText,
            onValueChange = onMessageTextChange,
            placeholder = { Text("Enter message") },
            modifier = Modifier.weight(1f)
        )
        Button(
            onClick = onMessageSend,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text("Send")
        }
    }
}

@Composable
fun MessageItem(message: ApiService.MessageResponse) {
    val senderId = message.senderId.stringValue ?: "Unknown Sender"
    val textMessage = message.message.stringValue ?: "No Message"

    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = "From: $senderId",
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            text = textMessage,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
