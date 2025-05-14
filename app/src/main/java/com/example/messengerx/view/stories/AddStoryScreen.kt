package com.example.messengerx.view.stories

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.messengerx.BuildConfig
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
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            uploadStory(viewModel, userId, photoUri!!, onStoryPublished)
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
                title = { Text("Добавить историю") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            StoryActionButton("Сделать фото") {
                val uri = createImageUri(context)
                if (uri != null) {
                    photoUri = uri
                    takePictureLauncher.launch(uri)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            StoryActionButton("Выбрать из галереи") {
                pickImageLauncher.launch("image/*")
            }

            Spacer(modifier = Modifier.height(16.dp))

            StoryActionButton("Отмена", onClick = onBack)
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

private fun createImageUri(context: Context): Uri? {
    return try {
        val imageFile = File(
            context.getExternalFilesDir(null),
            "story_${System.currentTimeMillis()}.jpg"
        )
        FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.provider",
            imageFile
        )
    } catch (e: Exception) {
        null
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
        onSuccess = { viewModel.addStory(userId, onSuccess) },
        onFailure = { Log.e("AddStory", "Ошибка загрузки: $it") }
    )
}
