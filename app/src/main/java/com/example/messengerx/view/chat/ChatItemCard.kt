package com.example.messengerx.view.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChatItemCard(chat: ChatItem, onClick: () -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onClick() } // Открытие конкретного чата
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF2BE4DC).copy(alpha = 0.3f),
                        Color(0xFF243484).copy(alpha = 0.3f),
                        Color(0xFFEEAAEE).copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chat.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (chat.isOnline) "Online" else "Last seen ${chat.lastSeen}",
                    fontSize = 14.sp,
                    color = if (chat.isOnline) Color.Green else Color.Gray
                )
            }
        }
    }
}



data class ChatRequest(
    val participants: List<String>,
    val lastMessage: String,
    val timestamp: Long
)

data class ChatResponse(
    val id: String,
    val participants: List<String>,
    val lastMessage: String,
    val timestamp: Long
)





