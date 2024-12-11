package com.example.messengerx.view.stories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.messengerx.R

@Composable
fun StoriesBar(viewModel: StoryViewModel, userId: String, onAddStoryClick: () -> Unit) {
    val stories by viewModel.stories.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current
    var selectedStoryUrl by remember { mutableStateOf<String?>(null) }

    // Загружаем истории при открытии
    LaunchedEffect(userId) {
        viewModel.fetchStories(userId)
    }

    // Удаление устаревших историй
    LaunchedEffect(stories) {
        val currentTime = System.currentTimeMillis()
        val expiredStories = stories.filter { currentTime - it.timestamp > 24 * 60 * 60 * 1000 }
        expiredStories.forEach { story ->
            viewModel.deleteStory(userId, story) // Удаление из Firestore
        }
    }

    if (!errorMessage.isNullOrEmpty()) {
        Text(
            text = errorMessage!!,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(8.dp)
        )
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Добавить историю
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .clickable { onAddStoryClick() }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "Добавить историю",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(24.dp),
                        tint = Color.White
                    )
                }
                Text(text = "Моя история", style = MaterialTheme.typography.bodySmall)
            }
        }

        // Существующие истории
        items(stories) { story ->
            StoryPreview(
                name = story.caption,
                imageUrl = story.imageUrl,
                onClick = { selectedStoryUrl = story.imageUrl }
            )
        }
    }

    // Полноэкранное изображение
    selectedStoryUrl?.let { imageUrl ->
        FullScreenImageDialog(
            imageUrl = imageUrl,
            onDismiss = { selectedStoryUrl = null }
        )
    }
}

@Composable
fun StoryPreview(name: String, imageUrl: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .clickable { onClick() }
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Превью истории",
                modifier = Modifier.fillMaxSize()
            )
        }
        Text(text = name, style = MaterialTheme.typography.bodySmall, maxLines = 1)
    }
}

@Composable
fun FullScreenImageDialog(imageUrl: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Полный экран",
            modifier = Modifier.fillMaxSize()
        )
    }
}
