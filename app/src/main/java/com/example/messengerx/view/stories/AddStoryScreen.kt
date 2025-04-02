package com.example.messengerx.view.stories

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.messengerx.api.ApiService
import java.io.File
import android.util.Log

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

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        imageUri?.takeIf { success }?.let { uri ->
            uploadStory(viewModel, userId, uri, onStoryPublished)
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            uploadStory(viewModel, userId, it, onStoryPublished)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить историю") },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            StoryActionButton(text = "Сделать фото") {
                val file = File(
                    context.getExternalFilesDir(null),
                    "story_${System.currentTimeMillis()}.jpg"
                )
                val uri = Uri.fromFile(file)
                imageUri = uri
                takePictureLauncher.launch(uri)
            }

            Spacer(modifier = Modifier.height(16.dp))

            StoryActionButton(text = "Выбрать из галереи") {
                pickImageLauncher.launch("image/*")
            }

            Spacer(modifier = Modifier.height(16.dp))

            StoryActionButton(text = "Отмена", onClick = onBack)
        }
    }
}

@Composable
private fun StoryActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text)
    }
}

private fun uploadStory(
    viewModel: StoryViewModel,
    userId: String,
    imageUri: Uri,
    onSuccess: () -> Unit
) {
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
            viewModel.addStory(userId, story) { isSuccess ->
                if (isSuccess) onSuccess()
            }
        },
        onFailure = { errorMessage ->
            Log.e("AddStory", "Ошибка загрузки: $errorMessage")
        }
    )
}
