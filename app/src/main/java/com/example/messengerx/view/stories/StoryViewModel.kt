package com.example.messengerx.view.stories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messengerx.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Story(
    val imageUrl: String = "",
    val timestamp: Long = 0L
)

class StoryViewModel(private val apiService: ApiService) : ViewModel() {
    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories

    fun addStory(userId: String, story: Story, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.addStory(userId, story).execute()
                if (response.isSuccessful) {
                    fetchStories(userId)
                    onComplete()
                }
            } catch (e: Exception) {
                println("Ошибка добавления истории: ${e.localizedMessage}")
            }
        }
    }

    fun fetchStories(userId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getUserStories(userId).execute()
                if (response.isSuccessful) {
                    val stories = response.body()?.values?.toList() ?: emptyList()
                    _stories.value = stories
                }
            } catch (e: Exception) {
                println("Ошибка получения историй: ${e.localizedMessage}")
            }
        }
    }
}
