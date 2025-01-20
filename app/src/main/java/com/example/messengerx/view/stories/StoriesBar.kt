package com.example.messengerx.view.stories

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.messengerx.api.ApiService
import java.io.File

@Composable
fun StoriesBar(
    viewModel: StoryViewModel,
    userId: String,
    modifier: Modifier = Modifier
) {
    val stories by viewModel.stories.collectAsState()
    var selectedStoryUrl by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Лаунчер для камеры
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imageUri != null) {
            // Отправляем фото на сервер
            uploadStory(viewModel, userId, imageUri!!)
        }
    }

    // Лаунчер для галереи
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            // Отправляем фото на сервер
            uploadStory(viewModel, userId, uri)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            // Кнопка добавления истории
            item {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Gray.copy(alpha = 0.8f))
                        .clickable {
                            // Открываем выбор: камера или галерея
                            val file = File(context.getExternalFilesDir(null), "story_${System.currentTimeMillis()}.jpg")
                            val uri = Uri.fromFile(file)
                            imageUri = uri
                            takePictureLauncher.launch(uri)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                }
            }

            // Отображение историй
            itemsIndexed(stories) { index, story ->
                val scale = if (index == 0) 1.2f else 1f // Масштаб для выделения
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Gray)
                        .clickable {
                            selectedStoryUrl = story.imageUrl // Выбор истории
                        },
                    contentAlignment = Alignment.Center
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

    // Полноэкранный просмотр выбранной истории
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
            modifier = Modifier.fillMaxSize()
        )
    }
}

fun uploadStory(viewModel: StoryViewModel, userId: String, imageUri: Uri) {
    viewModel.uploadStoryImage(
        userId = userId,
        imageUri = imageUri,
        onSuccess = { imageUrl ->
            val story = ApiService.Story(
                id = System.currentTimeMillis().toString(),
                imageUrl = imageUrl,
                timestamp = System.currentTimeMillis(),
                userId = userId
            )
            viewModel.addStory(userId, story) {
                println("История добавлена")
            }
        },
        onFailure = { errorMessage ->
            println("Ошибка загрузки: $errorMessage")
        }
    )
}

