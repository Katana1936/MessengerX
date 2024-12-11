package com.example.messengerx.view.stories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messengerx.api.ApiService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Story(
    val imageUrl: String = "",
    val timestamp: Long = 0L,
    val caption: String = ""
)

class StoryViewModel(private val apiService: ApiService) : ViewModel() {

    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun addStory(userId: String, story: Story) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            db.collection("stories").document(userId).collection("userStories")
                .add(story)
                .addOnSuccessListener {
                    fetchStories(userId) // Обновить список историй после добавления
                }
                .addOnFailureListener { exception ->
                    _errorMessage.value = "Ошибка добавления истории: ${exception.localizedMessage}"
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
                } else {
                    _errorMessage.value = "Ошибка: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка загрузки: ${e.localizedMessage}"
            }
        }
    }

    fun deleteStory(userId: String, story: Story) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            db.collection("stories").document(userId).collection("userStories")
                .whereEqualTo("timestamp", story.timestamp)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        document.reference.delete()
                    }
                }
                .addOnFailureListener { exception ->
                    _errorMessage.value = "Ошибка удаления истории: ${exception.localizedMessage}"
                }
        }
    }

}
