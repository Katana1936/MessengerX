package com.example.messengerx.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.messengerx.ui.theme.MessengerXTheme
import com.example.messengerx.view.chat.ChatItem
import com.example.messengerx.view.chat.ChatItemCard
import com.example.messengerx.view.chat.ChatViewModel
import com.example.messengerx.BottomNavigationBar
import com.example.messengerx.ParentComposable
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MessengerXTheme {
                // Get instance of ChatViewModel
                val chatViewModel: ChatViewModel = viewModel()
                val chatListState = chatViewModel.chatList.collectAsState()

                // Create HazeState
                val hazeState = remember { HazeState() }

                // Create main Box to contain everything
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Background content with the haze modifier
                    ChatListScreen(
                        chatList = chatListState.value,
                        hazeState = hazeState // Pass hazeState to ChatListScreen
                    )

                    // BottomNavigationBar with the hazeChild modifier
                    BottomNavigationBar(
                        hazeState = hazeState,
                        onItemSelected = { /* Handle item selection */ },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(chatList: List<ChatItem>, hazeState: HazeState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .haze(state = hazeState) // Apply haze here
    ) {
        TopAppBar(
            title = { Text(text = "Чаты") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(largeRadialGradient)
        ) {
            items(chatList) { chat ->
                ChatItemCard(chat)
            }
        }
    }
}
