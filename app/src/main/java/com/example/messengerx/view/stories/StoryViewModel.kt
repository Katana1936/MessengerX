package com.example.messengerx.view.stories

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messengerx.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Story(
    val imageUrl: String = "",
    val timestamp: Long = 0L,
    val caption: String = ""
)

class StoryViewModel(private val apiService: ApiService) : ViewModel() {
    // URI текущей фотографии
    var currentPhotoUri: Uri? by mutableStateOf(null)

    // Поток для хранения историй
    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories

    /**
     * Добавление истории через API
     */
    fun addStory(userId: String, story: Story, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                // Выполняем запрос на добавление истории через API
                val response = apiService.addStory(userId, story).execute()
                if (response.isSuccessful) {
                    fetchStories(userId) // Обновляем список историй
                    onComplete() // Вызываем коллбэк завершения
                } else {
                    println("Ошибка добавления истории: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("Ошибка подключения: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Получение историй через API
     */
    fun fetchStories(userId: String) {
        viewModelScope.launch {
            try {
                // Запрашиваем список историй через API
                val response = apiService.getUserStories(userId).execute()
                if (response.isSuccessful) {
                    val storiesList = response.body()?.values?.toList() ?: emptyList()
                    _stories.value = storiesList
                } else {
                    println("Ошибка получения историй: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("Ошибка подключения: ${e.localizedMessage}")
            }
        }
    }
}
