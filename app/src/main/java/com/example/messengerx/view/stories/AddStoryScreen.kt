package com.example.messengerx.view.stories

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStoryScreen(
    viewModel: StoryViewModel,
    userId: String,
    onStoryPublished: () -> Unit, // Callback для возврата на MainActivity после публикации
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var isUploading by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Лаунчер для камеры
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) {
            imageUri = null // Сбрасываем URI, если фото не удалось сделать
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить историю") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                if (imageUri == null) {
                    // Кнопка для съемки фото
                    Button(onClick = {
                        val file = File(context.getExternalFilesDir(null), "story_image_${System.currentTimeMillis()}.jpg")
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            file
                        )
                        imageUri = uri
                        takePictureLauncher.launch(uri)
                    }) {
                        Text("Сделать фото")
                    }
                } else {
                    // Предпросмотр фото
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Предпросмотр", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(16.dp))

                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Превью истории",
                            modifier = Modifier
                                .fillMaxWidth(0.8f) // Ширина в 80% экрана
                                .aspectRatio(16 / 9f) // Соотношение сторон 16:9
                                .background(MaterialTheme.colorScheme.surface)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Кнопки переснять и выложить
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Кнопка переснять фото
                            Button(onClick = {
                                val file = File(context.getExternalFilesDir(null), "story_image_${System.currentTimeMillis()}.jpg")
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.provider",
                                    file
                                )
                                imageUri = uri
                                takePictureLauncher.launch(uri)
                            }) {
                                Text("Переснять")
                            }

                            // Кнопка опубликовать
                            Button(
                                onClick = {
                                    isUploading = true
                                    val story = Story(
                                        imageUrl = imageUri.toString(),
                                        timestamp = System.currentTimeMillis()
                                    )
                                    viewModel.addStory(userId, story) {
                                        isUploading = false
                                        onStoryPublished() // Возвращаемся на MainActivity
                                    }
                                },
                                enabled = !isUploading
                            ) {
                                if (isUploading) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("Выложить")
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

