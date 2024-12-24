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
                val storyList = apiService.getUserStories(userId).values.toList()
                _stories.value = storyList
            } catch (e: Exception) {
                println("Ошибка получения историй: ${e.localizedMessage}")
            }
        }
    }

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

    fun addStory(userId: String, story: ApiService.Story, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                apiService.addStory(userId, story)
                onComplete(true)
            } catch (e: Exception) {
                println("Ошибка добавления истории: ${e.localizedMessage}")
                onComplete(false)
            }
        }
    }
}
