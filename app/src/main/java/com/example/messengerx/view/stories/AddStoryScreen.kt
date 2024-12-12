package com.example.messengerx.view.stories

import android.net.Uri
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
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStoryScreen(
    viewModel: StoryViewModel,
    userId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showBottomSheet by remember { mutableStateOf(false) }
    var previewUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var showFirstStoryMessage by remember { mutableStateOf(false) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) showBottomSheet = false
    }

    val selectFromGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            previewUri = it
            showBottomSheet = false
        }
    }

    Button(onClick = { showBottomSheet = true }) {
        Text("Добавить историю")
    }

    if (showBottomSheet) {
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false }) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = {
                    val file = File(context.getExternalFilesDir(null), "story_image.jpg")
                    val photoUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                    previewUri = photoUri
                    takePictureLauncher.launch(photoUri)
                }) {
                    Text("Сделать фото")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { selectFromGalleryLauncher.launch("image/*") }) {
                    Text("Выбрать из галереи")
                }
            }
        }
    }

    previewUri?.let { uri ->
        if (isUploading) {
            CircularProgressIndicator()
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Предпросмотр")
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
                    Text(text = "Опубликовать")
                }
            }
        }
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


