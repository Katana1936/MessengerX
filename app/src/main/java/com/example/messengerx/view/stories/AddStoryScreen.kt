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
    var showFirstStoryMessage by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            isPreviewVisible = true
        }
    }

    val selectFromGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
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
        Text("No permissions to add story")
    }

    if (arePermissionsGranted) {
        Button(onClick = {
            showBottomSheet = true
        }) {
            Text("Add Story")
        }
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
                Button(onClick = {
                    val file = File(context.getExternalFilesDir(null), "story_image.jpg")
                    val photoUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                    previewUri = photoUri
                    takePictureLauncher.launch(photoUri)
                    coroutineScope.launch { bottomSheetState.hide() }
                    showBottomSheet = false
                }) {
                    Text("Take Photo")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    selectFromGalleryLauncher.launch("image/*")
                    coroutineScope.launch { bottomSheetState.hide() }
                    showBottomSheet = false
                }) {
                    Text("Select from Gallery")
                }
            }
        }
    }

    if (isPreviewVisible) {
        AlertDialog(
            onDismissRequest = { isPreviewVisible = false },
            title = { Text("Preview") },
            text = {
                previewUri?.let {
                    Text("Do you want to publish the story or take a new photo?")
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
                                caption = "New Story"
                            )
                        ) { isFirst ->
                            if (isFirst as Boolean) {
                                showFirstStoryMessage = true
                            }
                            onBack()
                        }
                    }
                }) {
                    Text("Publish")
                }
            },
            dismissButton = {
                Button(onClick = {
                    isPreviewVisible = false
                    showBottomSheet = true
                }) {
                    Text("Retake")
                }
            }
        )
    }

    if (showFirstStoryMessage) {
        AlertDialog(
            onDismissRequest = { showFirstStoryMessage = false },
            title = { Text("Congratulations!") },
            text = { Text("Hurray, your first story!") },
            confirmButton = {
                Button(onClick = { showFirstStoryMessage = false }) {
                    Text("Close")
                }
            }
        )
    }
}
