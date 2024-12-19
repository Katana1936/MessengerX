package com.example.messengerx.view.stories

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoriesBar(
    viewModel: StoryViewModel,
    userId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedStoryUrl by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) } // Для индикатора загрузки

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imageUri != null) {
            val story = Story(
                id = System.currentTimeMillis().toString(),
                imageUrl = imageUri.toString(),
                timestamp = System.currentTimeMillis(),
                userId = userId
            )
            coroutineScope.launch {
                isUploading = true
                viewModel.addStory(userId, story) {
                    isUploading = false
                    imageUri = null // Сбрасываем URI после успешной отправки
                    viewModel.fetchStories(userId) // Обновляем истории
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchStories(userId) // Запрос историй при первом запуске
    }

    val stories by viewModel.stories.collectAsState()
    val state = rememberCarouselState { stories.size + 1 }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        HorizontalMultiBrowseCarousel(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            preferredItemWidth = 75.dp,
            itemSpacing = 4.dp
        ) { index ->
            if (index == 0) {
                // Кнопка для добавления новой истории
                Box(
                    modifier = Modifier
                        .width(75.dp)
                        .height(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Gray.copy(alpha = 0.8f))
                        .clickable(enabled = !isUploading) { // Блокируем кнопку во время загрузки
                            val file = File(context.getExternalFilesDir(null), "story_${System.currentTimeMillis()}.jpg")
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                file
                            )
                            imageUri = uri
                            takePictureLauncher.launch(uri)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isUploading) {
                        // Индикатор загрузки
                        Text(
                            text = "Загрузка...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    } else {
                        Text(
                            "+",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )
                    }
                }
            } else {
                // История пользователя
                val story = stories[index - 1]
                Box(
                    modifier = Modifier
                        .width(75.dp)
                        .height(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            selectedStoryUrl = story.imageUrl
                        }
                ) {
                    AsyncImage(
                        model = story.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    // Полноэкранное отображение истории
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
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        )
    }
}
