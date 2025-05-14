package com.example.messengerx.view.stories

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messengerx.api.StoryDataStoreManager
import com.example.messengerx.model.Story
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StoryViewModel(
    private val storyDataStoreManager: StoryDataStoreManager
) : ViewModel() {

    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories

    init {
        loadLocalStories()
    }

    private fun loadLocalStories() {
        viewModelScope.launch {
            storyDataStoreManager.stories.collect { localStories ->
                _stories.value = localStories
            }
        }
    }

    fun addLocalStory(story: Story) {
        viewModelScope.launch {
            val updatedStories = _stories.value + story
            storyDataStoreManager.saveStories(updatedStories)
        }
    }

    fun uploadStoryImage(
        userId: String,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        // TODO: Заменить заглушку на реальную загрузку изображения
        // Например, с использованием Multipart через Retrofit
        onSuccess(imageUri.toString())
    }

    fun addStory(story: Story, onComplete: (Boolean) -> Unit) {
        addLocalStory(story)
        onComplete(true)
    }

    fun fetchStories(userId: String) {
        // Можно добавить фильтрацию по userId, если надо
        loadLocalStories()
    }
}
