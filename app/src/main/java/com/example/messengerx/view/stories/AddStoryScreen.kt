package com.example.messengerx.view.stories

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember


@Composable
fun AddStoryScreen(
    viewModel: StoryViewModel,
    userId: String,
    onBack: () -> Unit
) {
    // Локальное состояние для проверки разрешений
    val arePermissionsGranted = remember { mutableStateOf(false) }

    val requestPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        arePermissionsGranted.value = permissions.values.all { it }
    }

    CreateStories(
        onPhotoSelected = { selectedPhotoUri ->
            viewModel.addStory(
                userId = userId,
                story = Story(
                    imageUrl = selectedPhotoUri.toString(),
                    timestamp = System.currentTimeMillis(),
                    caption = "Новая история"
                )
            )
            onBack()
        },
        requestPermissions = {
            requestPermissionsLauncher.launch(
                arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        },
        arePermissionsGranted = arePermissionsGranted.value
    )
}





