package com.example.messengerx.view.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(chatList: List<ChatItem>, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Чаты") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF0F3FF)
                )
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(0xFFF0F3FF))
                    .padding(paddingValues)
            ) {
                items(chatList) { chat ->
                    ChatItemCard(chat)
                }
            }
        }
    )
}
