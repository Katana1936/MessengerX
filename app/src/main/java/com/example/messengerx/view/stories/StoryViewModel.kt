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

    private val db = FirebaseFirestore.getInstance()

    fun isFirstStory(userId: String, callback: (Boolean) -> Unit) {
        db.collection("stories").document(userId).collection("userStories")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val isFirst = querySnapshot.isEmpty
                callback(isFirst)
            }
            .addOnFailureListener { exception ->
                println("Ошибка проверки первой истории: ${exception.localizedMessage}")
            }
    }

    fun addStory(userId: String, story: Story, onComplete: (() -> Unit) = {}) {
        db.collection("stories").document(userId).collection("userStories")
            .add(story)
            .addOnSuccessListener {
                fetchStories(userId)
                onComplete() // Вызываем, если передано
            }
            .addOnFailureListener { exception ->
                println("Ошибка добавления истории: ${exception.localizedMessage}")
            }
    }



    fun fetchStories(userId: String) {
        viewModelScope.launch {
            db.collection("stories").document(userId).collection("userStories")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val storiesList = querySnapshot.documents.mapNotNull { document ->
                        document.toObject(Story::class.java)
                    }
                    _stories.value = storiesList
                }
                .addOnFailureListener { exception ->
                    println("Error fetching stories: ${exception.localizedMessage}")
                }
        }
    }
}