package com.example.messengerx.view.stories

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
    onStoryPublished: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) imageUri = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить историю") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (imageUri == null) {
                Button(onClick = {
                    val file = File(context.getExternalFilesDir(null), "story_${System.currentTimeMillis()}.jpg")
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                    imageUri = uri
                    takePictureLauncher.launch(uri)
                }) {
                    Text("Сделать фото")
                }
            } else {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Предпросмотр",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16 / 9f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        isUploading = true
                        val story = Story(
                            id = System.currentTimeMillis().toString(),
                            imageUrl = imageUri.toString(),
                            timestamp = System.currentTimeMillis(),
                            userId = userId
                        )
                        viewModel.addStory(userId, story) {
                            isUploading = false
                            onStoryPublished()
                        }
                    },
                    enabled = !isUploading
                ) {
                    Text("Опубликовать")
                }
            }
        }
    }
}
