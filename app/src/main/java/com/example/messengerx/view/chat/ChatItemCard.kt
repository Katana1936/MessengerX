package com.example.messengerx.view.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.messengerx.api.ApiService

@Composable
fun ChatItemCard(chat: ApiService.ChatItem, onClick: () -> Unit = {}) {
    Card(
        colors = CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {

            Text(
                text = chat.name,
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
