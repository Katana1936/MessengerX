package com.example.messengerx.view.stories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoriesBar(viewModel: StoryViewModel, userId: String, onAddStoryClick: () -> Unit) {
    val stories by viewModel.stories.collectAsState()
    val state = rememberCarouselState { stories.size }
    var selectedStoryUrl by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Загрузка историй при открытии
    LaunchedEffect(userId) {
        viewModel.fetchStories(userId)
    }

    Column {
        // Карусель с историями
        HorizontalMultiBrowseCarousel(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(8.dp),
            preferredItemWidth = 150.dp,
            itemSpacing = 8.dp
        ) { index ->
            val story = stories[index]
            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(160.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable {
                        coroutineScope.launch {
                            selectedStoryUrl = story.imageUrl
                        }
                    }
            ) {
                AsyncImage(
                    model = story.imageUrl,
                    contentDescription = story.caption,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Кнопка добавления истории
        Box(
            modifier = Modifier
                .padding(8.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.primary)
                .clickable { onAddStoryClick() }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Добавить историю", color = MaterialTheme.colorScheme.onPrimary)
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
fun FullScreenImageDialog(imageUrl: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}
