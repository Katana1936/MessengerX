package com.example.messengerx.view.stories

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messengerx.api.ApiService
import com.example.messengerx.api.StoryDataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StoryViewModel(
    private val apiService: ApiService,
    private val storyDataStoreManager: StoryDataStoreManager
) : ViewModel() {

    private val _stories = MutableStateFlow<List<ApiService.Story>>(emptyList())
    val stories: StateFlow<List<ApiService.Story>> = _stories

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

    fun addLocalStory(story: ApiService.Story) {
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
        // Здесь просто сохраняем локально URI как строку
        onSuccess(imageUri.toString())
    }

    // Сеть нам пока не нужна
    fun addStory(userId: String, story: ApiService.Story, onComplete: (Boolean) -> Unit) {
        addLocalStory(story)
        onComplete(true)
    }
}

