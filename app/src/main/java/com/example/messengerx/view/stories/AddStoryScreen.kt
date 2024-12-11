package com.example.messengerx.view.stories

import androidx.compose.runtime.Composable

@Composable
fun AddStoryScreen(
    viewModel: StoryViewModel,
    userId: String,
    onBack: () -> Unit
) {
    CreateStories { selectedPhotoUri ->
        viewModel.addStory(
            userId = userId,
            story = Story(
                imageUrl = selectedPhotoUri.toString(),
                timestamp = System.currentTimeMillis(),
                caption = "Новая история"
            )
        )
        onBack()
    }
}




