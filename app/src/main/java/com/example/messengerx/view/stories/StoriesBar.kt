package com.example.messengerx.view.stories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.messengerx.R

@Composable
fun StoriesBar(viewModel: StoryViewModel, userId: String, onAddStoryClick: () -> Unit) {
    val stories by viewModel.stories.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Загружаем истории
    LaunchedEffect(userId) {
        viewModel.fetchStories(userId)
    }

    if (!errorMessage.isNullOrEmpty()) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(8.dp)
        )
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Первый элемент: добавление истории
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

        // Отображение историй
        items(stories) { story ->
            StoryAvatar(name = story.caption, imageUrl = story.imageUrl)
        }
    }
}


@Composable
fun StoryAvatar(name: String, imageUrl: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
        ) {
            androidx.compose.foundation.Image(
                painter = rememberImagePainter(imageUrl),
                contentDescription = "История",
                modifier = Modifier.fillMaxSize()
            )
        }
        Text(text = name, style = MaterialTheme.typography.bodySmall, maxLines = 1)
    }
}


