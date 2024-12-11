package com.example.messengerx.view.stories

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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

    val requestPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        arePermissionsGranted = permissions.values.all { it }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            println("Фотография успешно сделана")
        }
    }

    val selectFromGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.addStory(
                userId = userId,
                story = Story(
                    imageUrl = uri.toString(),
                    timestamp = System.currentTimeMillis(),
                    caption = "Новая история"
                )
            )
            onBack()
        }
    }

    // Проверка и запрос разрешений
    if (!arePermissionsGranted) {
        PermissionsHandler(
            permissions = listOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            onPermissionsGranted = { arePermissionsGranted = true }
        ) {
            Text("Разрешения предоставлены. Теперь можно добавить историю.")
        }
    } else {
        // Логика отображения BottomSheet
        if (showBottomSheet) {
            val sheetState = rememberModalBottomSheetState()
            val coroutineScope = rememberCoroutineScope()

            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Выберите действие", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        val file = File(context.getExternalFilesDir(null), "story_image.jpg")
                        val photoUri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            file
                        )
                        takePictureLauncher.launch(photoUri)
                        coroutineScope.launch { sheetState.hide() }
                        showBottomSheet = false
                    }) {
                        Text("Снять с камеры")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        selectFromGalleryLauncher.launch("image/*")
                        coroutineScope.launch { sheetState.hide() }
                        showBottomSheet = false
                    }) {
                        Text("Выбрать из галереи")
                    }
                }
            }
        } else {
            // Кнопка открытия BottomSheet
            Button(onClick = { showBottomSheet = true }) {
                Text("Добавить историю")
            }
        }
    }
}


