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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

@Composable
fun ChatItemCard(chat: ChatItem, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onClick() }
    ) {
        // Размытие и фон
        Box(
            modifier = Modifier
                .matchParentSize()
                .blur(15.dp) // Эффект размытия
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0x802BE4DC), // Прозрачные цвета
                            Color(0x80243484),
                            Color(0x80EEAAEE)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp) // Скругленные углы
                )
        )

        // Контент карточки
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent), // Прозрачный фон для карточки
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
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
}


// Обновленная модель данных
data class ChatItem(
    val name: String,
    val isOnline: Boolean,
    val lastSeen: String
)
