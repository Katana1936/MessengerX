package com.example.messengerx.view.stories

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.messengerx.PermissionsHandler
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStoryScreen(
    viewModel: StoryViewModel,
    userId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var arePermissionsGranted by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var previewUri by remember { mutableStateOf<Uri?>(null) }
    var isPreviewVisible by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            Log.d("AddStoryScreen", "Фото успешно сделано")
            isPreviewVisible = true
        }
    }

    val selectFromGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            Log.d("AddStoryScreen", "Выбрано изображение: $uri")
            previewUri = it
            isPreviewVisible = true
        }
    }

    PermissionsHandler(
        permissions = buildList {
            add(android.Manifest.permission.CAMERA)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                add(android.Manifest.permission.READ_MEDIA_IMAGES)
                add(android.Manifest.permission.READ_MEDIA_VIDEO)
            } else {
                add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        },
        onPermissionsGranted = {
            arePermissionsGranted = true
        }
    ) {
        Text("Нет разрешений для добавления истории")
    }


    if (arePermissionsGranted) {
        Button(onClick = {
            showBottomSheet = true
        }) {
            Text("Добавить историю")
        }
    }

    if (arePermissionsGranted) {
        Button(onClick = {
            showBottomSheet = true
            Log.d("AddStoryScreen", "Кнопка нажата, showBottomSheet = $showBottomSheet")
        }) {
            Text("Добавить историю")
        }
    } else {
        Text("Нет разрешений для добавления истории")
    }

    if (showBottomSheet) {
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = bottomSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Выберите действие", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    // Логика для съёмки фото
                    Log.d("AddStoryScreen", "Съёмка фото выбрана")
                    showBottomSheet = false
                }) {
                    Text("Сделать фото")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    // Логика для выбора из галереи
                    Log.d("AddStoryScreen", "Выбор из галереи выбран")
                    showBottomSheet = false
                }) {
                    Text("Выбрать из галереи")
                }
            }
        }
    }


    if (isPreviewVisible) {
        AlertDialog(
            onDismissRequest = { isPreviewVisible = false },
            title = { Text("Предварительный просмотр") },
            text = {
                previewUri?.let {
                    Text("Вы хотите опубликовать историю или сделать новое фото?")
                }
            },
            confirmButton = {
                Button(onClick = {
                    previewUri?.let {
                        viewModel.addStory(
                            userId = userId,
                            story = Story(
                                imageUrl = it.toString(),
                                timestamp = System.currentTimeMillis(),
                                caption = "Новая история"
                            )
                        )
                        onBack()
                    }
                }) {
                    Text("Опубликовать")
                }
            },
            dismissButton = {
                Button(onClick = {
                    isPreviewVisible = false
                    showBottomSheet = true
                }) {
                    Text("Переделать")
                }
            }
        )
    }
}