package com.example.messengerx.view.stories

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.messengerx.api.ApiService
import java.io.File
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


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
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imageUri != null) {
            uploadStory(viewModel, userId, imageUri!!) {
                onStoryPublished()
            }
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            uploadStory(viewModel, userId, uri) {
                onStoryPublished()
            }
        }
    }

    Scaffold(
        topBar = {
            Text(
                text = "Добавить историю",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    val file = File(
                        context.getExternalFilesDir(null),
                        "story_${System.currentTimeMillis()}.jpg"
                    )
                    val uri = Uri.fromFile(file)
                    imageUri = uri
                    takePictureLauncher.launch(uri)
                }
            ) {
                Text("Сделать фото")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    pickImageLauncher.launch("image/*")
                }
            ) {
                Text("Выбрать из галереи")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onBack) {
                Text("Отмена")
            }
        }
    }
}

fun uploadStory(viewModel: StoryViewModel, userId: String, imageUri: Uri, onSuccess: () -> Unit) {
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
            println("Ошибка загрузки: $errorMessage")
        }
    )
}
