package com.example.messengerx.view.stories

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import java.io.File

@Composable
fun CreateStories(onPhotoSelected: (Uri) -> Unit) {
    val context = LocalContext.current

    // Используем `remember` для хранения состояния URI
    val photoUriState = remember { mutableStateOf<Uri?>(null) }
    val isPhotoTakenState = remember { mutableStateOf(false) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            isPhotoTakenState.value = true
        } else {
            photoUriState.value = null
        }
    }

    val selectFromGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            photoUriState.value = it
            isPhotoTakenState.value = true
        }
    }

    if (!isPhotoTakenState.value) {
        // Экран выбора способа добавления фото
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                val file = File(context.getExternalFilesDir(null), "story_image.jpg")
                val photoUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                photoUriState.value = photoUri
                takePictureLauncher.launch(photoUri)
            }) {
                Text("Снять с камеры")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                selectFromGalleryLauncher.launch("image/*")
            }) {
                Text("Выбрать из галереи")
            }
        }
    } else {
        // Экран предпросмотра фото
        val photoUri = photoUriState.value
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            photoUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .size(200.dp)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                photoUriState.value = null
                isPhotoTakenState.value = false
            }) {
                Text("Переснять")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                photoUri?.let { uri -> onPhotoSelected(uri) }
            }) {
                Text("Выставить")
            }
        }
    }
}






