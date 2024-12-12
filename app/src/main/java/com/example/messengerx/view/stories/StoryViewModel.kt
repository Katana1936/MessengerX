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

    fun addStory(userId: String, story: Story) {
        viewModelScope.launch {
            db.collection("stories").document(userId).collection("userStories")
                .add(story)
                .addOnSuccessListener {
                    // После добавления, обновляем список историй
                    fetchStories(userId)
                }
                .addOnFailureListener { exception ->
                    println("Ошибка добавления истории: ${exception.localizedMessage}")
                }
        }
    }

    private fun fetchStories(userId: String) {
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
                    println("Ошибка загрузки историй: ${exception.localizedMessage}")
                }
        }
    }
}

