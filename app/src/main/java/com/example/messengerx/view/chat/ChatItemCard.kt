package com.example.messengerx.view.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.messengerx.api.ApiService

@Composable
fun ChatItemCard(
    chat: ApiService.ChatItem,
    apiService: ApiService,
    onClick: () -> Unit = {}
) {
    var lastMessage by remember { mutableStateOf("Loading...") }
    var lastMessageTime by remember { mutableStateOf("...") }

    LaunchedEffect(chat.id) {
        try {
            val messages = apiService.getMessages(chat.id).values.sortedByDescending { it.timestamp }
            if (messages.isNotEmpty()) {
                val message = messages.first()
                lastMessage = message.message
                lastMessageTime = formatTimeAgo(message.timestamp)
            } else {
                lastMessage = "No messages yet"
                lastMessageTime = "N/A"
            }
        } catch (e: Exception) {
            lastMessage = "Error loading messages"
            lastMessageTime = "N/A"
        }
    }


    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f)) {
                // Avatar
                Surface(
                    modifier = Modifier
                        .size(50.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                ) {
                    // Text avatar with the first letter of the name
                    Text(
                        text = chat.name.firstOrNull()?.toString()?.uppercase() ?: "",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = chat.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Text(
                        text = lastMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1
                    )
                }
            }

            Text(
                text = lastMessageTime,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

fun formatTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val minutes = diff / 60000
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 60 -> "$minutes mins ago"
        hours < 24 -> "$hours hrs ago"
        else -> "$days days ago"
    }
}
