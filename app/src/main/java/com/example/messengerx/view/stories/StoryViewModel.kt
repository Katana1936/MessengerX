package com.example.messengerx.view.stories

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messengerx.api.ApiService
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StoryViewModel(private val apiService: ApiService) : ViewModel() {

    // StateFlow для хранения списка историй
    private val _stories = MutableStateFlow<List<ApiService.Story>>(emptyList())
    val stories: StateFlow<List<ApiService.Story>> = _stories

    // Метод для получения списка историй пользователя
    fun fetchStories(userId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getUserStories(userId)
                if (response.isSuccessful) {
                    val storyList = response.body()?.values?.toList() ?: emptyList()
                    _stories.value = storyList
                } else {
                    println("Ошибка получения историй: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("Ошибка сети: ${e.message}")
            }
        }
    }

    // Метод для загрузки изображения истории в Firebase
    fun uploadStoryImage(
        userId: String,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val storageRef = FirebaseStorage.getInstance().reference
        val storyRef = storageRef.child("stories/$userId/${System.currentTimeMillis()}.jpg")

        storyRef.putFile(imageUri)
            .addOnSuccessListener {
                storyRef.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }.addOnFailureListener { e ->
                    onFailure("Ошибка получения URL: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                onFailure("Ошибка загрузки файла: ${e.message}")
            }
    }

    // Метод для добавления истории
    fun addStory(userId: String, story: ApiService.Story, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.addStory(userId, story)
                if (response.isSuccessful) {
                    onComplete(true)
                } else {
                    println("Ошибка добавления истории: ${response.errorBody()?.string()}")
                    onComplete(false)
                }
            } catch (e: Exception) {
                println("Ошибка добавления истории: ${e.localizedMessage}")
                onComplete(false)
            }
        }
    }
}
