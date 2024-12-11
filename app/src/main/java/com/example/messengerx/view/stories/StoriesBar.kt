package com.example.messengerx.view.stories

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoriesBar(
    viewModel: StoryViewModel,
    userId: String,
    onAddStoryClick: (requestPermissions: () -> Unit, arePermissionsGranted: Boolean) -> Unit
) {
    val stories by viewModel.stories.collectAsState()
    val state = rememberCarouselState { stories.size + 1 }
    var selectedStoryUrl by remember { mutableStateOf<String?>(null) }
    val arePermissionsGranted = remember { mutableStateOf(false) }

    val requestPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        arePermissionsGranted.value = permissions.values.all { it }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        HorizontalMultiBrowseCarousel(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            preferredItemWidth = 75.dp,
            itemSpacing = 4.dp
        ) { index ->
            if (index == 0) {
                // Кнопка добавления истории
                Box(
                    modifier = Modifier
                        .width(75.dp)
                        .height(100.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color.Gray)
                        .clickable {
                            onAddStoryClick(
                                {
                                    requestPermissionsLauncher.launch(
                                        arrayOf(
                                            android.Manifest.permission.CAMERA,
                                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                                        )
                                    )
                                },
                                arePermissionsGranted.value
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }
            } else {
                // Отображение историй
                val story = stories[index - 1]
                Box(
                    modifier = Modifier
                        .width(75.dp)
                        .height(100.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .clickable {
                            selectedStoryUrl = story.imageUrl
                        }
                ) {
                    AsyncImage(
                        model = story.imageUrl,
                        contentDescription = story.caption,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    selectedStoryUrl?.let { imageUrl ->
        FullScreenImageDialog(
            imageUrl = imageUrl,
            onDismiss = { selectedStoryUrl = null }
        )
    }
}



@Composable
fun FullScreenImageDialog(imageUrl: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}