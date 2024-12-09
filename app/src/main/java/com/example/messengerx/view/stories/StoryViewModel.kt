package com.example.messengerx.view.stories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Story(
    val imageUrl: String = "",
    val timestamp: Long = 0L,
    val caption: String = ""
)

class StoryViewModel : ViewModel() {

    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchStories(userId: String) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            db.collection("stories").document(userId).collection("userStories")
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val fetchedStories = querySnapshot.documents.mapNotNull { it.toObject(Story::class.java) }
                    _stories.value = fetchedStories
                }
                .addOnFailureListener { exception ->
                    _errorMessage.value = "Ошибка загрузки историй: ${exception.localizedMessage}"
                }
        }
    }
}