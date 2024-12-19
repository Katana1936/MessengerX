package com.example.messengerx.view.stories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messengerx.firebase.FirestoreHelper
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StoryViewModel : ViewModel() {
    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories

    fun addStory(userId: String, story: Story, onComplete: () -> Unit) {
        FirestoreHelper.firestore.collection("stories")
            .document(userId)
            .collection("userStories")
            .document(story.id)
            .set(story)
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener { println("Ошибка добавления истории") }
    }

    fun fetchStories(userId: String) {
        FirestoreHelper.firestore.collection("stories")
            .document(userId)
            .collection("userStories")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val storyList = querySnapshot.documents.mapNotNull { it.toObject(Story::class.java) }
                _stories.value = storyList
            }
            .addOnFailureListener { println("Ошибка загрузки историй") }
    }
}



data class Story(
    val id: String = "",
    val imageUrl: String = "",
    val timestamp: Long = 0L,
    val userId: String = ""
)
