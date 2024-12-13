package com.example.messengerx.view.stories

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.messengerx.ui.theme.white
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStoryScreen(
    viewModel: StoryViewModel,
    userId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var isUploading by remember { mutableStateOf(false) }
    var showFirstStoryMessage by remember { mutableStateOf(false) }

    // Локальное состояние для управления превью
    val previewUri = viewModel.currentPhotoUri

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && viewModel.currentPhotoUri != null) {
            // URI автоматически обновляется в ViewModel
        }
    }

    val selectFromGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.currentPhotoUri = it // Обновляем превью в ViewModel
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить историю") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(onClick = { viewModel.showBottomSheet = true }) {
                    Text("Добавить историю")
                }

                previewUri?.let { uri ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Предпросмотр")
                        Spacer(modifier = Modifier.height(16.dp))
                        AsyncImage(
                            model = uri,
                            contentDescription = "Превью истории",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16 / 9f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            isUploading = true
                            viewModel.addStory(
                                userId = userId,
                                story = Story(imageUrl = uri.toString(), timestamp = System.currentTimeMillis())
                            ) {
                                isUploading = false
                                showFirstStoryMessage = true
                                onBack()
                            }
                        }) {
                            Text("Опубликовать")
                        }
                    }
                }
            }
        }
    )

    if (viewModel.showBottomSheet) {
        StoryBottomSheet(
            onTakePhoto = {
                val file = File(context.getExternalFilesDir(null), "story_image.jpg")
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                viewModel.currentPhotoUri = uri // Устанавливаем URI в ViewModel
                takePictureLauncher.launch(uri)
            },
            onSelectFromGallery = {
                selectFromGalleryLauncher.launch("image/*")
            },
            onDismiss = { viewModel.showBottomSheet = false }
        )
    }

    if (showFirstStoryMessage) {
        AlertDialog(
            onDismissRequest = {
                showFirstStoryMessage = false
                onBack()
            },
            title = { Text("Поздравляем!") },
            text = { Text("Ура, ваша первая история!") },
            confirmButton = {
                Button(onClick = {
                    showFirstStoryMessage = false
                    onBack()
                }) {
                    Text("Закрыть")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryBottomSheet(
    onTakePhoto: () -> Unit,
    onSelectFromGallery: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkGray)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onTakePhoto,
                colors = ButtonDefaults.buttonColors(containerColor = LightGray)
            ) {
                Text("Сделать фото", color = white)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onSelectFromGallery,
                colors = ButtonDefaults.buttonColors(containerColor = LightGray)
            ) {
                Text("Выбрать из галереи", color = white)
            }
        }
    }
}





